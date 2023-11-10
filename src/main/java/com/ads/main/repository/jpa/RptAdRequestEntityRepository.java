package com.ads.main.repository.jpa;

import com.ads.main.entity.RptAdRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RptAdRequestEntityRepository extends JpaRepository<RptAdRequestEntity, String>, JpaSpecificationExecutor<RptAdRequestEntity> {

    Optional<RptAdRequestEntity> findFirstByRequestId(String requestId);
}
