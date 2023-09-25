package com.ads.main.entity.mapper;

import com.ads.main.core.config.convert.GenericMapper;
import com.ads.main.entity.AdCampaignMasterEntity;
import com.ads.main.vo.campaign.resp.AdCampaignMasterVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {
            AdSmartStoreConvert.class,
            AdQuizConvert.class,
            FilesConverter.class,
        }
)
public interface AdCampaignMasterConvert extends GenericMapper<AdCampaignMasterVo, AdCampaignMasterEntity>  {

    @Mapping(target = "smartStore", source = "adSmartStoreEntity")
    @Mapping(target = "quiz", source = "adQuizEntity")
    @Override
    AdCampaignMasterVo toDto(AdCampaignMasterEntity e);

}
