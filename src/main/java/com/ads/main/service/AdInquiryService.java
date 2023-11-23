package com.ads.main.service;


import com.ads.main.core.config.exception.AppException;
import com.ads.main.entity.AdInquiryEntity;
import com.ads.main.entity.mapper.AdInquiryConverter;
import com.ads.main.enums.AdInquiryException;
import com.ads.main.repository.jpa.AdInquiryRepository;
import com.ads.main.vo.inquiry.req.AdInquiryReqVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdInquiryService {

    private final AdInquiryRepository adInquiryRepository;
//    private final AdInquiryConverter adInquiryConverter;

    public void save(AdInquiryReqVo adInquiryReqVo) throws AppException {

        try {
            AdInquiryEntity adInquiryEntity = AdInquiryEntity.inquiry(adInquiryReqVo);
            adInquiryRepository.save(adInquiryEntity);

        } catch (Exception e) {
            log.error("# inquiry save error", e);
            throw AdInquiryException.ERROR.throwErrors();
        }
    }
}
