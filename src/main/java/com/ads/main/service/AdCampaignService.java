package com.ads.main.service;


import com.ads.main.core.config.exception.AppException;
import com.ads.main.core.config.exception.NoAdException;
import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.core.utils.PageMapper;
import com.ads.main.entity.AdCampaignMasterEntity;
import com.ads.main.entity.mapper.AdCampaignMasterConvert;
import com.ads.main.entity.mapper.RptAdAnswerConvert;
import com.ads.main.enums.AdException;
import com.ads.main.repository.jpa.PartnerAdGroupRepository;
import com.ads.main.repository.querydsl.QAdvertiserCampaignMasterRepository;
import com.ads.main.repository.template.AdAnswerTemplate;
import com.ads.main.repository.template.AdClickTemplate;
import com.ads.main.repository.template.AdImpressionTemplate;
import com.ads.main.repository.template.AdRequestTemplate;
import com.ads.main.vo.adGroup.resp.PartnerAdGroupVo;
import com.ads.main.vo.campaign.RptAdAnswerVo;
import com.ads.main.vo.campaign.req.RptAdAnswer;
import com.ads.main.vo.campaign.req.RptAdClick;
import com.ads.main.vo.campaign.req.RptAdImpression;
import com.ads.main.vo.campaign.req.RptAdRequest;
import com.ads.main.vo.campaign.resp.AdCampaignMasterVo;
import com.ads.main.vo.resp.LandingVo;
import com.ads.main.vo.resp.PageAds;
import com.ads.main.vo.resp.QuizAds;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.ads.main.enums.AdException.NO_AD;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdCampaignService {
    private final PartnerAdGroupRepository partnerAdGroupRepository;


    // 광고 이미지 서버
    @Value("${app.file-server}")
    private String FILE_SERVER;


    // 광고 앱 서버(랜딩 로그 적재)
    @Value("${app.app-server}")
    private String APP_SERVER;


    private final AdGroupCacheService adGroupService;

//    private final AdCampaignCacheService adCampaignCacheService;

    private final AdQuizRedisService quizRedisService;

    private final QAdvertiserCampaignMasterRepository qAdvertiserCampaignMasterRepository;

    // 로그 저장용
    private final AdRequestTemplate adRequestTemplate;
    private final AdImpressionTemplate adImpressionTemplate;
    private final AdAnswerTemplate adAnswerTemplate;
    private final AdClickTemplate adClickTemplate;

    // Entity <-> Vo 변경용
    private final AdCampaignMasterConvert adCampaignMasterConvert;
    private final RptAdAnswerConvert rptAdAnswerConvert;
    private PageMapper<AdCampaignMasterVo, AdCampaignMasterEntity> pageMapper;

    @PostConstruct
    private void initPageMappers() {
        pageMapper =  new PageMapper<>(adCampaignMasterConvert);
    }


    public PageAds<QuizAds> requestList(String groupCode, int page, int size) throws NoAdException, AppException {

        CampaignType campaignType = adGroupService.findCampaignType(groupCode);

        Page<AdCampaignMasterEntity> adCampaignMasterEntity= qAdvertiserCampaignMasterRepository.findAdCampaigns(campaignType, PageRequest.of(page - 1, size));

        if (adCampaignMasterEntity.isEmpty()) {
            throw AdException.NO_AD.throwErrors();
        }

        return convert(groupCode, pageMapper.convert(adCampaignMasterEntity));
    }

    private PageAds<QuizAds> convert(String groupCode, Page<AdCampaignMasterVo> vos) {

        List<QuizAds> quizAds = vos.getContent().stream().map(ad -> {

            String requestId = RandomStringUtils.randomAlphabetic(15);

            LandingVo landing = new LandingVo(groupCode, requestId, APP_SERVER);
            landing.setThumb(generateUrl("quiz-list", ad));
            landing.setCampaignCode(ad.getCampaignCode());

            return QuizAds.builder()
                    .requestId(requestId)
                    .campaignName(ad.getCampaignName())
                    .campaignCode(ad.getCampaignCode())
                    .totalParticipationLimit(ad.getTotalParticipationLimit())
                    .dayParticipationLimit(ad.getDayParticipationLimit())
                    .quizTitle(ad.getQuiz().getQuizTitle())
                    .landing(landing)
                    .build();
        }).toList();

        return new PageAds<>(quizAds, vos.getTotalPages(), vos.getTotalPages(), vos.getSize());
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
                    int partnerCommission = 0;
                    int userCommission = 0;


                    int partnerAdPrice = Math.round((float) (adPrice * partnerAdGroupVo.getCommissionRate()) / 100);
                    int userAdPrice = Math.round((float) (adPrice - partnerAdPrice *  partnerAdGroupVo.getUserCommissionRate()) / 100);

                    log.debug("# Quiz01 {}, {},  {} ", adPrice,  partnerAdGroupVo.getCommissionRate(), partnerAdGroupVo.getUserCommissionRate());

                    if (CampaignType.Quiz02.getCode().equals(campaignMasterVo.getCampaignType())) {
                        log.debug("# Quiz02 {}, {}. {} ", adPrice,  campaignMasterVo.getCommissionRate(), campaignMasterVo.getUserCommissionRate());

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
                    e.printStackTrace();
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

        log.info("# adCampaignMaster => {}", adCampaignMaster);

        CampaignType campaignType = CampaignType.of(adCampaignMaster.getCampaignType());

        if (campaignType == CampaignType.Quiz01) {
            saveImpressionLog(impression);

            LandingVo landing = new LandingVo(null, impression.requestId(), APP_SERVER);

            landing.setThumb(generateUrl("quiz-list", adCampaignMaster));
            landing.setDetail(generateUrl("quiz-detail-1", adCampaignMaster));

            landing.setAnswer(adCampaignMaster.getCampaignCode());

            landing.setHint_ad_mobile(adCampaignMaster.getQuiz().getTargetUrlPc());
            landing.setHint_ad_pc(adCampaignMaster.getQuiz().getTargetUrlMobile());

            landing.setAnswer_ad_pc(adCampaignMaster.getQuiz().getTargetUrlPc());
            landing.setAnswer_ad_mobile(adCampaignMaster.getQuiz().getTargetUrlMobile());

            return QuizAds.builder()
                    .requestId(impression.requestId())
                    .campaignName(adCampaignMaster.getCampaignName())
                    .campaignCode(adCampaignMaster.getCampaignCode())
                    .totalParticipationLimit(adCampaignMaster.getTotalParticipationLimit())
                    .dayParticipationLimit(adCampaignMaster.getDayParticipationLimit())
                    .quizTitle(adCampaignMaster.getQuiz().getQuizTitle())
                    .landing(landing)
                    .build();
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

    public String checkAnswer(RptAdAnswer rptAdAnswer) throws NoAdException, AppException {
        AdCampaignMasterVo adCampaignMaster = quizRedisService.findCampaignByCode(rptAdAnswer.campaignCode());

        String ret = quizRedisService.joinProcess(adCampaignMaster, rptAdAnswer);
        saveAnswerLog(rptAdAnswer, BigDecimal.ZERO);

        return ret;
    }

    private void saveAnswerLog(RptAdAnswer adAnswer, BigDecimal cost) {
        RptAdAnswerVo rptAdAnswerVo = new RptAdAnswerVo();
        rptAdAnswerConvert.updateFromDto(adAnswer, rptAdAnswerVo);
        rptAdAnswerVo.setCost(cost);
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

}
