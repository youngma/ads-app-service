package com.ads.main.entity.mapper;

import com.ads.main.core.config.convert.GenericMapper;
import com.ads.main.core.enums.advertiser.AdGroupStatus;
import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.entity.PartnerAdGroupEntity;
import com.ads.main.vo.adGroup.resp.PartnerAdGroupVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(
        componentModel = "spring"
        ,uses = { FilesConverter.class }
)
public interface PartnerAdGroupConvert extends GenericMapper<PartnerAdGroupVo, PartnerAdGroupEntity> {

    @Mapping(target = "groupStatusName", ignore = true)
    @Mapping(target = "adTypeName", ignore = true)
    @Mapping(target = "pointIconFile", source = "pointIconFileEntity")
    @Mapping(target = "logoFile", source = "logoFileEntity")
    @Mapping(target = "adType", source = "adType", qualifiedByName = "adTypeToValue")
    @Mapping(target = "groupStatus", source = "groupStatus", qualifiedByName = "groupStatusToValue")
    @Override
    PartnerAdGroupVo toDto(PartnerAdGroupEntity e);


    @Named("adTypeToEnum")
    default CampaignType adTypeToEnum(String source) {
        return CampaignType.of(source);
    }

    @Named("adTypeToValue")
    default String adTypeToValue(CampaignType adType) {
        return adType.getCode();
    }

    @Named("groupStatusToEnum")
    default AdGroupStatus groupStatusToEnum(String source) {
        return AdGroupStatus.of(source);
    }

    @Named("groupStatusToValue")
    default String groupStatusToValue(AdGroupStatus adGroupStatus) {
        return adGroupStatus.getCode();
    }


}
