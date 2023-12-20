package com.ads.main.entity.mapper;

import com.ads.main.core.config.convert.GenericMapper;
import com.ads.main.entity.RptAdAnswerEntity;
import com.ads.main.vo.report.resp.RptAdAnswerVo;
import com.ads.main.vo.campaign.req.RptAdAnswer;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface RptAdAnswerConvert extends GenericMapper<RptAdAnswerVo, RptAdAnswerEntity> {
    @Override
    RptAdAnswerVo toDto(RptAdAnswerEntity e);

    @Override
    List<RptAdAnswerVo> toDtoList(List<RptAdAnswerEntity> e);

    @Mapping(target = "postBackStatus", ignore = true)
    @Mapping(target = "postBackResult", ignore = true)
    @Mapping(target = "reword", ignore = true)
    @Mapping(target = "userKey", source = "user")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(RptAdAnswer rptAdAnswer, @MappingTarget RptAdAnswerVo rptAdAnswerVo);


}
