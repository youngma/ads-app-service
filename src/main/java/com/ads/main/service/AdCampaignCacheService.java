package com.ads.main.service;

import com.ads.main.entity.AdCampaignMasterEntity;
import com.ads.main.entity.RptAdAnswerEntity;
import com.ads.main.entity.mapper.AdCampaignMasterConvert;
import com.ads.main.entity.mapper.RptAdAnswerConvert;
import com.ads.main.repository.jpa.RptAdAnswerEntityRepository;
import com.ads.main.repository.querydsl.QAdvertiserCampaignMasterRepository;
import com.ads.main.vo.campaign.resp.AdCampaignMasterVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ads.main.enums.AdGroupException.NO_AD;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdCampaignCacheService {

    private final RptAdAnswerEntityRepository rptAdAnswerEntityRepository;

    private final QAdvertiserCampaignMasterRepository qAdvertiserCampaignMasterRepository;

    private final AdCampaignMasterConvert adCampaignMasterConvert;
    private final RptAdAnswerConvert rptAdAnswerConvert;

    @Cacheable(
            cacheNames = "ad-campaign"
            , key = "#campaignCode"
            , unless = "#result == null"
    )
    public AdCampaignMasterVo findCampaignByCode(String campaignCode) {
        log.debug("# campaign cache => {}", campaignCode);
        Optional<AdCampaignMasterEntity> adCampaignMasterEntityOptional = qAdvertiserCampaignMasterRepository.findAdCampaign(campaignCode);
        AdCampaignMasterEntity adCampaignMasterEntity = adCampaignMasterEntityOptional.orElseThrow(NO_AD::throwErrors);
        return adCampaignMasterConvert.toDto(adCampaignMasterEntity);
    }


    @Cacheable(
            cacheNames = "quiz-answer"
            , key = "#campaignCode"
            , unless = "#result == null"
    )
    public String findQuizAnswerByCode(String campaignCode) {
        Optional<String> quizAnswerOpt = qAdvertiserCampaignMasterRepository.findQuizAnswer(campaignCode);
        log.debug("# campaign answer => {}, {}", campaignCode, quizAnswerOpt);
        return quizAnswerOpt.orElseThrow(NO_AD::throwErrors);
    }


    @Cacheable(
        cacheNames = "quiz-answer"
        , unless = "#result == false"
    )
    public boolean findQuizAnswerByCodeUser(String campaignCode, String user) {
        log.debug("# campaign join => {}", campaignCode);
       Optional<RptAdAnswerEntity> rptAdAnswerEntityOptional = rptAdAnswerEntityRepository.findFirstByCampaignCodeAndUserKey(campaignCode, user);
       return rptAdAnswerEntityOptional.isPresent();
    }

}
