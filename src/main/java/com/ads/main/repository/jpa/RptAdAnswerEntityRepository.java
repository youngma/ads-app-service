package com.ads.main.repository.jpa;

import com.ads.main.entity.RptAdAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface RptAdAnswerEntityRepository extends JpaRepository<RptAdAnswerEntity, String>, JpaSpecificationExecutor<RptAdAnswerEntity> {

    Optional<RptAdAnswerEntity> findFirstByCampaignCodeAndUserKey(String campaignCode, String user);

    HashSet<RptAdAnswerEntity> findAllByCampaignCodeInAndUserKey (Set<String> campaignCodes, String user);
}
