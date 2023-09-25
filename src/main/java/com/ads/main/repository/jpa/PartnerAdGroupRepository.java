package com.ads.main.repository.jpa;

import com.ads.main.core.enums.advertiser.AdGroupStatus;
import com.ads.main.entity.PartnerAdGroupEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PartnerAdGroupRepository extends JpaRepository<PartnerAdGroupEntity, Long>, JpaSpecificationExecutor<PartnerAdGroupEntity> {

    @EntityGraph(attributePaths = { "partnerEntity", "logoFileEntity",  "pointIconFileEntity" })
    long countByGroupCodeAndGroupStatus(String groupCode, AdGroupStatus status);
    Optional<PartnerAdGroupEntity> findFirstByGroupCodeAndGroupStatus(String groupCode, AdGroupStatus status);

}
