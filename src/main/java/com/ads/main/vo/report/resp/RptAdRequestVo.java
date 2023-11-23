package com.ads.main.vo.report.resp;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


@Data
public class RptAdRequestVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 광고요청코드
     */
    @NotNull(message = "requestId can not null")
    private String requestId;


    /**
     * 캠페인 코드
     */
    @NotNull(message = "campaignCode can not null")
    private String campaignCode;


    /**
     * 광고 그룹 코드
     */
    @NotNull(message = "groupCode can not null")
    private String groupCode;


    /**
     * USER-AGENT
     */
    private String userAgent;


    /**
     * 요청 일자
     */
    @NotNull(message = "requestAt can not null")
    private Date requestAt;


    /**
     * 호출 IP ADDRESS
     */
    private String remoteIp;


    /**
     * 광고금액
     */
    private Integer adCost;


    /**
     * 사용자 지급금액
     */
    private Integer userCommission;


    /**
     * 파트너 지급금액
     */
    private Integer partnerCommission;



    /**
     * 파트너 노출 포인트
     */
    private Integer adReword;

}
