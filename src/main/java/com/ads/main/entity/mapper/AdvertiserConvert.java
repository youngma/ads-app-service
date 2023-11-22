package com.ads.main.entity.mapper;

import com.ads.main.core.config.convert.GenericMapper;
import com.ads.main.entity.AdvertiserEntity;
import com.ads.main.vo.campaign.resp.AdvertiserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(
        componentModel = "spring"
        ,uses = { FilesConverter.class }
)
public interface AdvertiserConvert extends GenericMapper<AdvertiserVo, AdvertiserEntity> {

    @Override
    AdvertiserVo toDto(AdvertiserEntity e);

    @Mapping(target = "advertiserUserEntities",ignore = true)
    @Mapping(target = "advertiserAccountEntities", ignore = true)
    @Mapping(target = "adCampaignMasterEntities", ignore = true)
    AdvertiserEntity toEntity(AdvertiserVo d);


}
