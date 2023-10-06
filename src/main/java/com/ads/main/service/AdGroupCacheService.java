package com.ads.main.service;


import com.ads.main.core.config.exception.AppException;
import com.ads.main.core.enums.advertiser.AdGroupStatus;
import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.entity.PartnerAdGroupEntity;
import com.ads.main.repository.jpa.PartnerAdGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.ads.main.enums.AdGroupException.AD_GROUP_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdGroupCacheService {

    private final PartnerAdGroupRepository partnerAdGroupRepository;

    @Cacheable(
            cacheNames = "group-code-valid"
            , key = "#groupCode"
            , unless = "#result == null"
    )
    public void checkAdGroupCode(String groupCode) throws AppException {
        long count = partnerAdGroupRepository.countByGroupCodeAndGroupStatus(groupCode, AdGroupStatus.Approval);
        log.debug("# repo checkAdGroupCode => {}", groupCode);
        if (count == 0) {
            throw AD_GROUP_NOT_FOUND.throwErrors();
        }
    }

    @Cacheable(
            cacheNames = "ad-group-campaign-type"
            , key = "#groupCode"
            , unless = "#result == null"
    )
    public CampaignType findCampaignType(String groupCode) throws AppException {
        PartnerAdGroupEntity partnerAdGroupEntity = partnerAdGroupRepository.findFirstByGroupCodeAndGroupStatus(groupCode, AdGroupStatus.Approval)
                .orElseThrow(AD_GROUP_NOT_FOUND::throwErrors);

        log.debug("# repo findCampaignType => {}", groupCode);

        return partnerAdGroupEntity.getAdType();
    }


}
