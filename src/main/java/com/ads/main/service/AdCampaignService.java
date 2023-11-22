package com.ads.main.service;


import com.ads.main.core.clients.AppClientFactory;
import com.ads.main.core.clients.DOMAIN;
import com.ads.main.core.clients.api.MobonApi;
import com.ads.main.core.config.exception.AdAnswerException;
import com.ads.main.core.config.exception.AppException;
import com.ads.main.core.config.exception.NoAdException;
import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.core.utils.PageMapper;
import com.ads.main.entity.AdCampaignMasterEntity;
import com.ads.main.entity.RptAdAnswerEntity;
import com.ads.main.entity.RptAdRequestEntity;
import com.ads.main.entity.mapper.AdCampaignMasterConvert;
import com.ads.main.entity.mapper.RptAdAnswerConvert;
import com.ads.main.entity.mapper.RptAdRequestConvert;
import com.ads.main.enums.AdException;
import com.ads.main.enums.AdJoinException;
import com.ads.main.enums.CampaignJoinType;
import com.ads.main.repository.jpa.RptAdAnswerEntityRepository;
import com.ads.main.repository.jpa.RptAdRequestEntityRepository;
import com.ads.main.repository.querydsl.QAdRewordRepository;
import com.ads.main.repository.querydsl.QAdvertiserCampaignMasterRepository;
import com.ads.main.repository.template.AdAnswerTemplate;
import com.ads.main.repository.template.AdClickTemplate;
import com.ads.main.repository.template.AdImpressionTemplate;
import com.ads.main.repository.template.AdRequestTemplate;
import com.ads.main.vo.adGroup.resp.PartnerAdGroupVo;
import com.ads.main.vo.inf.mobi.MobiAds;
import com.ads.main.vo.report.resp.RptAdAnswerVo;
import com.ads.main.vo.campaign.req.RptAdAnswer;
import com.ads.main.vo.campaign.req.RptAdClick;
import com.ads.main.vo.campaign.req.RptAdImpression;
import com.ads.main.vo.campaign.req.RptAdRequest;
import com.ads.main.vo.campaign.resp.AdCampaignMasterVo;
import com.ads.main.vo.report.resp.RptAdRequestVo;
import com.ads.main.vo.resp.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.ads.main.enums.AdException.NO_AD;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdCampaignService {

    private final AppClientFactory appClientFactory;

    // 광고 이미지 서버
    @Value("${app.file-server}")
    private String FILE_SERVER;


    // 광고 앱 서버(랜딩 로그 적재)
    @Value("${app.app-server}")
    private String APP_SERVER;


    private final AdGroupCacheService adGroupService;


    private final AdQuizRedisService quizRedisService;

    private final RptAdAnswerEntityRepository rptAdAnswerEntityRepository;
    private final RptAdRequestEntityRepository rptAdRequestEntityRepository;
    private final QAdvertiserCampaignMasterRepository qAdvertiserCampaignMasterRepository;
    private final QAdRewordRepository qAdRewordRepository;

    // 로그 저장용
    private final AdRequestTemplate adRequestTemplate;
    private final AdImpressionTemplate adImpressionTemplate;
    private final AdAnswerTemplate adAnswerTemplate;
    private final AdClickTemplate adClickTemplate;

    // Entity <-> Vo 변경용
    private final AdCampaignMasterConvert adCampaignMasterConvert;
    private final RptAdAnswerConvert rptAdAnswerConvert;
    private final RptAdRequestConvert rptAdRequestConvert;
    private PageMapper<AdCampaignMasterVo, AdCampaignMasterEntity> pageMapper;

    private MobonApi mobonApi;
    private ObjectMapper objectMapper;

    @PostConstruct
    private void initPageMappers() {
        pageMapper =  new PageMapper<>(adCampaignMasterConvert);
        mobonApi = appClientFactory.load(DOMAIN.MOBON_API);
        objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

    }


    public PageAds<QuizAds> requestList(String groupCode, String join, String user, int page, int size) throws NoAdException, AppException {

        CampaignType campaignType = adGroupService.findCampaignType(groupCode);
        CampaignJoinType campaignJoinType = CampaignJoinType.of(join);

        Page<AdCampaignMasterEntity> adCampaignMasterEntity= qAdvertiserCampaignMasterRepository.findAdCampaigns(campaignType, campaignJoinType, user, PageRequest.of(page - 1, size));

        if (adCampaignMasterEntity.isEmpty()) {
            throw AdException.NO_AD.throwErrors();
        }

        Set<String> campaignCodeSet = adCampaignMasterEntity.getContent()
                .stream()
                .map(AdCampaignMasterEntity::getCampaignCode)
                .collect(Collectors.toSet());

        Set<String> joinCampaignCodeSet = rptAdAnswerEntityRepository.findAllByCampaignCodeInAndUserKey(campaignCodeSet, user)
                .stream().map(RptAdAnswerEntity::getCampaignCode).collect(Collectors.toSet());

        return convert(groupCode, joinCampaignCodeSet, user, pageMapper.convert(adCampaignMasterEntity));
    }

    private PageAds<QuizAds> convert(String groupCode, Set<String> joinCampaignCodeSet, String user, Page<AdCampaignMasterVo> vos) throws AppException {

        PartnerAdGroupVo partnerAdGroupVo = adGroupService.searchByGroupCode(groupCode);


        List<QuizAds> quizAds = vos.getContent().stream().map(ad -> {

            String requestId = RandomStringUtils.randomAlphabetic(15);

            LandingVo landing = new LandingVo(groupCode, requestId, APP_SERVER, user);
            landing.setThumb(generateUrl("quiz-list", ad));
            landing.setCampaignCode(ad.getCampaignCode());

            return QuizAds.builder()
                    .requestId(requestId)
                    .partnerAdGroupVo(partnerAdGroupVo)
                    .adCampaignMasterVo(ad)
                    .campaignName(ad.getCampaignName())
                    .campaignCode(ad.getCampaignCode())
                    .pointName(partnerAdGroupVo.getPointName())
                    .isJoined(joinCampaignCodeSet.contains(ad.getCampaignCode()))
                    .totalParticipationLimit(ad.getTotalParticipationLimit())
                    .dayParticipationLimit(ad.getDayParticipationLimit())
                    .quizTitle(ad.getQuiz().getQuizTitle())
                    .landing(landing)
                    .build();

        }).toList();

        return new PageAds<>(quizAds, vos.getTotalPages(), vos.getTotalElements(), vos.getSize());
    }

    private String generateUrl(String type, AdCampaignMasterVo ads) {
        switch (type) {
            case "quiz-list" -> {
                return Strings.join(List.of(FILE_SERVER, "files",ads.getQuiz().getMainImage().getFileName()), '/');
            }
            case "quiz-detail-1" -> {
                return Strings.join(List.of(FILE_SERVER, "files",ads.getQuiz().getDetailImage1().getFileName()), '/');
            }
            case "quiz-detail-2" -> {
                return Strings.join(List.of(FILE_SERVER, "files",ads.getQuiz().getDetailImage2().getFileName()), '/');
            }
        }
        return "";
    }

    public void markRequest(RptAdRequest request) {
        try {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {

                    PartnerAdGroupVo partnerAdGroupVo = adGroupService.searchByGroupCode(request.getGroupCode());
                    AdCampaignMasterVo campaignMasterVo = quizRedisService.findCampaignByCode(request.getCampaignCode());

                    int adPrice = campaignMasterVo.getAdPrice().intValue();
                    int partnerCommission;
                    int userCommission;

                    int partnerAdPrice = Math.round((float) (adPrice * partnerAdGroupVo.getCommissionRate()) / 100);
                    int userAdPrice = Math.round((float) (adPrice - partnerAdPrice *  partnerAdGroupVo.getUserCommissionRate()) / 100);

                    if (CampaignType.Quiz02.getCode().equals(campaignMasterVo.getCampaignType())) {
                        partnerCommission = campaignMasterVo.getCommissionRate().intValue();
                        userCommission = campaignMasterVo.getUserCommissionRate().intValue();
                    } else {
                        partnerCommission = adPrice - partnerAdPrice;
                        userCommission = partnerCommission - userAdPrice;
                    }

                    int adReword =  Math.round((float) (userCommission * partnerAdGroupVo.getRewordRate()));

                    request.setAdPrice(adPrice);
                    request.setAdReword(adReword);
                    request.setPartnerCommission(partnerCommission);
                    request.setUserCommission(userCommission);

                } catch (Exception e) {
                    log.error("# request saver error", e);
                    throw new RuntimeException(e);
                }

                adRequestTemplate.saveAll(List.of(request));
            });
            future.get();
        } catch (Exception e) {
            log.debug("# request save error", e);
            adRequestTemplate.saveAll(List.of(request));
        }
    }

    public QuizAds markImpression(RptAdImpression impression) throws NoAdException {

        AdCampaignMasterVo adCampaignMaster = quizRedisService.findCampaignByCode(impression.campaignCode());
        Optional<RptAdAnswerEntity> optionalRptAdAnswerEntity = rptAdAnswerEntityRepository.findFirstByCampaignCodeAndUserKey(impression.campaignCode(), impression.user());

        log.info("# adCampaignMaster => {}", adCampaignMaster);

        CampaignType campaignType = CampaignType.of(adCampaignMaster.getCampaignType());

        if (
            adCampaignMaster.getAdvertiser().getIfCode() != null
            && adCampaignMaster.getIfAdCode() != null
            && !adCampaignMaster.getAdvertiser().getIfCode().isBlank()
            && !adCampaignMaster.getIfAdCode().isBlank()
        ) {
            log.info("# 여기서 광고를 호출 합니다.");
            return interfaceAdvertising(impression, adCampaignMaster, optionalRptAdAnswerEntity.isPresent());
        } else {
            if (
                    campaignType == CampaignType.Quiz01 || campaignType == CampaignType.Quiz02
            ) {
                return manualAdvertising(impression, adCampaignMaster, optionalRptAdAnswerEntity.isPresent());
            }
        }

        throw NO_AD.throwErrors();
    }

    private QuizAds manualAdvertising(RptAdImpression impression, AdCampaignMasterVo adCampaignMaster, boolean isJoined) throws NoAdException {

        RptAdRequestVo  rptAdRequestVo =  getRptAdRequestByAdRequestId(impression.requestId());

        Integer joinUserCount = qAdRewordRepository.findJoinUserCount(rptAdRequestVo.getGroupCode(), rptAdRequestVo.getCampaignCode());

        Integer reword = rptAdRequestVo.getAdReword();
        saveImpressionLog(impression);

        LandingVo landing = new LandingVo(rptAdRequestVo.getGroupCode(), impression.requestId(), APP_SERVER, impression.user());

        landing.setThumb(generateUrl("quiz-list", adCampaignMaster));
        landing.setDetail(generateUrl("quiz-detail-1", adCampaignMaster));

        landing.setAnswer(adCampaignMaster.getCampaignCode());
        landing.setReword(adCampaignMaster.getCampaignCode());

        landing.setHint_ad_mobile(adCampaignMaster.getQuiz().getTargetUrlPc());
        landing.setHint_ad_pc(adCampaignMaster.getQuiz().getTargetUrlMobile());

        landing.setAnswer_ad_pc(adCampaignMaster.getQuiz().getTargetUrlPc());
        landing.setAnswer_ad_mobile(adCampaignMaster.getQuiz().getTargetUrlMobile());

        return QuizAds.builder()
                .requestId(impression.requestId())
                .reword(reword)
                .joinUserCount(joinUserCount)
                .campaignName(adCampaignMaster.getCampaignName())
                .campaignCode(adCampaignMaster.getCampaignCode())
                .totalParticipationLimit(adCampaignMaster.getTotalParticipationLimit())
                .dayParticipationLimit(adCampaignMaster.getDayParticipationLimit())
                .isJoined(isJoined)
                .quizTitle(adCampaignMaster.getQuiz().getQuizTitle())
                .useHow(adCampaignMaster.getQuiz().getUseHow())
                .landing(landing)
                .build();
    }

    public QuizAds interfaceAdvertising(RptAdImpression impression, AdCampaignMasterVo adCampaignMaster, boolean isJoined) throws NoAdException {

        String ifAdCode = adCampaignMaster.getIfAdCode();
        String ifCode = adCampaignMaster.getAdvertiser().getIfCode();

        log.info("# {}, {}", ifAdCode, ifCode);

        if ("MOBI".equals(ifCode)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put(HttpHeaders.USER_AGENT, impression.userAgent());

            String content =  mobonApi.search(headers, ifAdCode, "1","1","99", "Y");
            log.info("# {}", content);
            if (content != null) {

                try {
                    MobiAds mobiAds = objectMapper.readValue(content, MobiAds.class);

                    RptAdRequestVo  rptAdRequestVo =  getRptAdRequestByAdRequestId(impression.requestId());

                    Integer joinUserCount = qAdRewordRepository.findJoinUserCount(rptAdRequestVo.getGroupCode(), rptAdRequestVo.getCampaignCode());

                    Integer reword = rptAdRequestVo.getAdReword();
                    saveImpressionLog(impression);

                    LandingVo landing = new LandingVo(rptAdRequestVo.getGroupCode(), impression.requestId(), APP_SERVER, impression.user());


                    String detailImage = mobiAds.getClient().get(0).getData().get(0).getMimg_850_800();
                    String randing = mobiAds.getClient().get(0).getData().get(0).getSite_url();

                    landing.setThumb(generateUrl("quiz-list", adCampaignMaster));
                    landing.setDetail(detailImage);

                    landing.setAnswer(adCampaignMaster.getCampaignCode());
                    landing.setReword(adCampaignMaster.getCampaignCode());

                    landing.setHint_ad_mobile(randing);
                    landing.setHint_ad_pc(randing);

                    landing.setAnswer_ad_pc(randing);
                    landing.setAnswer_ad_mobile(randing);

                    return QuizAds.builder()
                            .requestId(impression.requestId())
                            .reword(reword)
                            .joinUserCount(joinUserCount)
                            .campaignName(adCampaignMaster.getCampaignName())
                            .campaignCode(adCampaignMaster.getCampaignCode())
                            .totalParticipationLimit(adCampaignMaster.getTotalParticipationLimit())
                            .dayParticipationLimit(adCampaignMaster.getDayParticipationLimit())
                            .isJoined(isJoined)
                            .quizTitle(adCampaignMaster.getQuiz().getQuizTitle())
                            .useHow(adCampaignMaster.getQuiz().getUseHow())
                            .landing(landing)
                            .build();

                } catch (Exception e) {
                    log.debug("# MOBI AD IF ERROR => {}", e);
                    throw NO_AD.throwErrors();
                }
            }
        }

        throw NO_AD.throwErrors();
    }

    public void saveImpressionLog(RptAdImpression impression) {
        try {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> adImpressionTemplate.saveAll(List.of(impression)));
            future.get();

        } catch (Exception e) {
            log.debug("# impression save error, {}", impression);
            adImpressionTemplate.saveAll(List.of(impression));
        }
    }

    private Integer getRewordByAdRequestId(String requestId) throws NoAdException {
        return getRptAdRequestByAdRequestId(requestId).getAdReword();
    }

    private RptAdRequestVo getRptAdRequestByAdRequestId(String requestId) throws NoAdException {
        RptAdRequestEntity rptAdRequestEntity  = rptAdRequestEntityRepository.findFirstByRequestId(requestId).orElseThrow(NO_AD::throwErrors);
        return rptAdRequestConvert.toDto(rptAdRequestEntity);
    }

    public AnswerResp checkAnswer(RptAdAnswer rptAdAnswer) throws NoAdException, AdAnswerException {

        AdCampaignMasterVo adCampaignMaster = quizRedisService.findCampaignByCode(rptAdAnswer.campaignCode());

        boolean ret = quizRedisService.joinProcess(adCampaignMaster, rptAdAnswer);

        if (ret) {
            Integer reword = getRewordByAdRequestId(rptAdAnswer.requestId());
            saveAnswerLog(rptAdAnswer, reword);
            return new AnswerResp(true, "정답 입니다.", reword);
        }

        throw AdJoinException.ANSWER_WAIT.throwErrors();
    }

    private void saveAnswerLog(RptAdAnswer adAnswer, Integer reword) {
        RptAdAnswerVo rptAdAnswerVo = new RptAdAnswerVo();

        rptAdAnswerConvert.updateFromDto(adAnswer, rptAdAnswerVo);
        rptAdAnswerVo.setReword(BigDecimal.valueOf(reword));

        try {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> adAnswerTemplate.saveAll(List.of(rptAdAnswerVo)));
            future.get();
        } catch (Exception e) {
            adAnswerTemplate.saveAll(List.of(rptAdAnswerVo));
        }
    }

    public void saveClickLog(RptAdClick click) {
        try {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> adClickTemplate.saveAll(List.of(click)));
            future.get();
        } catch (Exception e) {
            log.debug("# click save error, {}", click);
            adClickTemplate.saveAll(List.of(click));
        }
    }

    public PageAds<RewordResp> selectRewords(String groupCode, String campaignCode, int page, int size) throws AppException {
        Page<RewordResp>  rewordResps = qAdRewordRepository.findAdRewordList(groupCode, campaignCode, PageRequest.of(page - 1, size));
        return new PageAds<>(rewordResps.getContent(), rewordResps.getTotalPages(), rewordResps.getTotalElements(), rewordResps.getSize());
    }


}
