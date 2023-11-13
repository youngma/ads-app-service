package com.ads.main.repository.jpa;

import com.ads.main.entity.AdInquiryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdInquiryRepository extends JpaRepository<AdInquiryEntity, Long>, JpaSpecificationExecutor<AdInquiryEntity> {

}
