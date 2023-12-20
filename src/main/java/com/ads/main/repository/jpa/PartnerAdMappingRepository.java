package com.ads.main.repository.jpa;

import com.ads.main.entity.PartnerAdMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PartnerAdMappingRepository extends JpaRepository<PartnerAdMappingEntity, Long>, JpaSpecificationExecutor<PartnerAdMappingEntity> {

}
