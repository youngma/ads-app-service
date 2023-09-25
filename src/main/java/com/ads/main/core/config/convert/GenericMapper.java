package com.ads.main.core.config.convert;

import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;


public interface GenericMapper<Dto, Entity> {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Dto toDto(Entity e);

    List<Dto> toDtoList(List<Entity> e);

}
