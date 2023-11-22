package com.ads.main.entity.mapper;

import com.ads.main.core.config.convert.GenericMapper;
import com.ads.main.core.enums.campaign.CampaignStatus;
import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.entity.AdCampaignMasterEntity;
import com.ads.main.vo.campaign.resp.AdCampaignMasterVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {
            AdSmartStoreConvert.class,
            AdQuizConvert.class,
            FilesConverter.class,
        }
)
public interface AdCampaignMasterConvert extends GenericMapper<AdCampaignMasterVo, AdCampaignMasterEntity>  {

    @Mapping(target = "advertiser", source = "advertiserEntity")
    @Mapping(target = "smartStore", source = "adSmartStoreEntity")
    @Mapping(target = "quiz", source = "adQuizEntity")
    @Mapping(target = "campaignType", source = "campaignType", qualifiedByName = "campaignTypeToValue")
    @Override
    AdCampaignMasterVo toDto(AdCampaignMasterEntity e);


    @Named("campaignTypeToEnum")
    default CampaignType campaignTypeToEnum(String source) {
        return CampaignType.of(source);
    }

    @Named("campaignTypeToValue")
    default String campaignTypeToValue(CampaignType campaignType) {
        return campaignType.getCode();
    }

    @Named("campaignStatusToEnum")
    default CampaignStatus campaignStatusToEnum(String source) {
        return CampaignStatus.of(source);
    }

    @Named("campaignStatusToValue")
    default String campaignStatusToValue(CampaignStatus campaignStatus) {
        return campaignStatus.getCode();
    }
}
