package com.ads.main.entity.mapper;

import com.ads.main.core.config.convert.GenericMapper;
import com.ads.main.entity.AdInquiryEntity;
import com.ads.main.entity.FilesEntity;
import com.ads.main.vo.FilesVo;
import com.ads.main.vo.inquiry.req.AdInquiryReqVo;
import com.ads.main.vo.inquiry.resp.AdInquiryVo;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdInquiryConverter extends GenericMapper<AdInquiryVo, AdInquiryEntity> {

    @Mapping(target = "statusName", ignore = true)
    @Mapping(target = "inquiryTypeName", ignore = true)
    @Override
    AdInquiryVo toDto(AdInquiryEntity e);


    @Override
    List<AdInquiryVo> toDtoList(List<AdInquiryEntity> e);

    @Mapping(target = "updatedId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "seq", ignore = true)
    @Mapping(target = "requestAt", ignore = true)
    @Mapping(target = "insertedId", ignore = true)
    @Mapping(target = "insertedAt", ignore = true)
    @Mapping(target = "answerAt", ignore = true)
    @Mapping(target = "answer", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(AdInquiryReqVo dto, @MappingTarget AdInquiryEntity entity);
}
