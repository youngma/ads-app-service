package com.ads.main.service;


import com.ads.main.core.config.exception.NoAdException;
import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.core.utils.PageMapper;
import com.ads.main.entity.AdCampaignMasterEntity;
import com.ads.main.entity.mapper.AdCampaignMasterConvert;
import com.ads.main.entity.mapper.RptAdAnswerConvert;
import com.ads.main.enums.AdException;
import com.ads.main.repository.querydsl.QAdvertiserCampaignMasterRepository;
import com.ads.main.repository.template.AdAnswerTemplate;
import com.ads.main.repository.template.AdClickTemplate;
import com.ads.main.repository.template.AdImpressionTemplate;
import com.ads.main.repository.template.AdRequestTemplate;
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

import static com.ads.main.enums.AdGroupException.NO_AD;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdCampaignService {


    @Value("${app.file-server}")
    private String FILE_SERVER;


    @Value("${app.app-server}")
    private String APP_SERVER;

    private final AdGroupCacheService adGroupService;
    private final AdCampaignCacheService adCampaignCacheService;


    private final QAdvertiserCampaignMasterRepository qAdvertiserCampaignMasterRepository;


    private final AdRequestTemplate adRequestTemplate;
    private final AdImpressionTemplate adImpressionTemplate;
    private final AdAnswerTemplate adAnswerTemplate;
    private final AdClickTemplate adClickTemplate;

    private final AdCampaignMasterConvert adCampaignMasterConvert;
    private final RptAdAnswerConvert rptAdAnswerConvert;
    private PageMapper<AdCampaignMasterVo, AdCampaignMasterEntity> pageMapper;

    @PostConstruct
    private void initPageMappers() {
        pageMapper =  new PageMapper<>(adCampaignMasterConvert);
    }

    public PageAds<QuizAds> requestList(String groupCode, int page, int size) throws NoAdException {

        CampaignType campaignType = adGroupService.findCampaignType(groupCode);

        Page<AdCampaignMasterEntity> adCampaignMasterEntity= qAdvertiserCampaignMasterRepository.findAdCampaigns(campaignType, PageRequest.of(page - 1, size));

        if (adCampaignMasterEntity.isEmpty()) {
            throw AdException.NO_AD.throwErrors();
        }

        return convert(pageMapper.convert(adCampaignMasterEntity));
    }

    private PageAds<QuizAds> convert(Page<AdCampaignMasterVo> vos) {

        List<QuizAds> quizAds = vos.getContent().stream().map(ad -> {

            String requestId = RandomStringUtils.randomAlphabetic(15);

            LandingVo landing = new LandingVo(requestId, APP_SERVER);
            landing.setThumb(generateUrl("quiz-list", ad));
            landing.setDetail_page(ad.getCampaignCode());

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
                adRequestTemplate.saveAll(List.of(request));
            });
            future.get();
        } catch (Exception e) {
            log.debug("# request save error, {}", request);
            adRequestTemplate.saveAll(List.of(request));
        }
    }

    public QuizAds markImpression(RptAdImpression impression) {
        AdCampaignMasterVo adCampaignMaster = adCampaignCacheService.findCampaignByCode(impression.campaignCode());

        log.info("# adCampaignMaster => {}", adCampaignMaster);

        CampaignType campaignType = CampaignType.valueOf(adCampaignMaster.getCampaignType());



        switch (campaignType)  {
            case Quiz01 -> {
                saveImpressionLog(impression);

                LandingVo landing = new LandingVo(impression.requestId(), APP_SERVER);

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
        }
        throw NO_AD.throwErrors();
    }

    public void saveImpressionLog(RptAdImpression impression) {

        try {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                adImpressionTemplate.saveAll(List.of(impression));
            });
            future.get();

        } catch (Exception e) {
            log.debug("# impression save error, {}", impression);
            adImpressionTemplate.saveAll(List.of(impression));
        }

    }



    public String checkAnswer(RptAdAnswer rptAdAnswer) {
        String answer = adCampaignCacheService.findQuizAnswerByCode(rptAdAnswer.campaignCode());

        log.debug("# 정답 => {}", answer);
        if (adCampaignCacheService.findQuizAnswerByCodeUser(rptAdAnswer.campaignCode(), rptAdAnswer.user())) {
            return "이미 참여한 퀴즈 입니다.";
        }

        if (rptAdAnswer.answer().equals(answer)) {
            // save call
            saveAnswerLog(rptAdAnswer, BigDecimal.valueOf(100L));
            return "정답 입니다.";
        }

        return "오답 입니다..";
    }


    private void saveAnswerLog(RptAdAnswer adAnswer, BigDecimal cost) {
        RptAdAnswerVo rptAdAnswerVo = new RptAdAnswerVo();
        rptAdAnswerConvert.updateFromDto(adAnswer, rptAdAnswerVo);
        rptAdAnswerVo.setCost(cost);
        try {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                adAnswerTemplate.saveAll(List.of(rptAdAnswerVo));
            });
            future.get();
        } catch (Exception e) {
            adAnswerTemplate.saveAll(List.of(rptAdAnswerVo));
        }
    }


    public void saveClickLog(RptAdClick click) {
        try {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                adClickTemplate.saveAll(List.of(click));
            });
            future.get();
        } catch (Exception e) {
            log.debug("# click save error, {}", click);
            adClickTemplate.saveAll(List.of(click));
        }
    }

}
