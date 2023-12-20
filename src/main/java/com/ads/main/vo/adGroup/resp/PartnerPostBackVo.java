package com.ads.main.vo.adGroup.resp;

import com.ads.main.core.enums.advertiser.AdGroupStatus;
import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.vo.FilesVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartnerPostBackVo implements Serializable {

    /**
     * 광고 요청 코드
     */
    private String requestId;

    /**
     * 광고 그룹 코드
     */
    private String groupCode;

    /**
     * 캠페인 코드
     */
    private String campaignCode;


    private String usrKey;


    /**
     * 사용자 지급 금액
     */
    private Integer userCommission;


    /**
     * 파트너 지급 금액
     */
    private Integer partnerCommission;


    /**
     * 파트너 노출 포인트
     */
    private Integer adReword;

}
