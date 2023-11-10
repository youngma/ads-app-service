package com.ads.main.entity.mapper;

import com.ads.main.core.config.convert.GenericMapper;
import com.ads.main.entity.RptAdRequestEntity;
import com.ads.main.vo.report.resp.RptAdRequestVo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface RptAdRequestConvert extends GenericMapper<RptAdRequestVo, RptAdRequestEntity> {
    @Override
    RptAdRequestVo toDto(RptAdRequestEntity e);

    @Override
    List<RptAdRequestVo> toDtoList(List<RptAdRequestEntity> e);


}
