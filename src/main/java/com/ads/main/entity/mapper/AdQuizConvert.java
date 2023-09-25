package com.ads.main.entity.mapper;

import com.ads.main.core.config.convert.GenericMapper;
import com.ads.main.entity.AdQuizEntity;
import com.ads.main.entity.AdSmartStoreEntity;
import com.ads.main.vo.campaign.resp.AdQuizVo;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
            FilesConverter.class
        }
)
public interface AdQuizConvert extends GenericMapper<AdQuizVo, AdQuizEntity> {

    @Mapping(target = "adCampaignMasterVo", ignore = true)
    @Override
    AdQuizVo toDto(AdQuizEntity e);

    @Override
    List<AdQuizVo> toDtoList(List<AdQuizEntity> e);

}
