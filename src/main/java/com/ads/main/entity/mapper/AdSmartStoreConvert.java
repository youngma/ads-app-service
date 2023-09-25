package com.ads.main.entity.mapper;

import com.ads.main.core.config.convert.GenericMapper;
import com.ads.main.core.enums.campaign.PaymentTerms;
import com.ads.main.entity.AdSmartStoreEntity;
import com.ads.main.vo.campaign.resp.AdSmartStoreVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
            FilesConverter.class
        }
)
public interface AdSmartStoreConvert extends GenericMapper<AdSmartStoreVo, AdSmartStoreEntity>  {

    @Mapping(target = "adCampaignMasterVo", ignore = true)
    @Mapping(target = "paymentTerms", source = "paymentTerms", qualifiedByName = "paymentTermsToValue")
    @Override
    AdSmartStoreVo toDto(AdSmartStoreEntity e);

    @Override
    List<AdSmartStoreVo> toDtoList(List<AdSmartStoreEntity> e);

    @Named("paymentTermsToEnum")
    default PaymentTerms paymentTermsToEnum(String source) {
        return PaymentTerms.of(source);
    }

    @Named("paymentTermsToValue")
    default String paymentTermsToValue(PaymentTerms paymentTerms) {
        return paymentTerms.getCode();
    }

}
