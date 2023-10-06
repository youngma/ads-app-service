package com.ads.main.vo.campaign;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class RptAdAnswerVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 광고 요청 코드
     */
    @NotNull(message = "requestId can not null")
    private String requestId;


    /**
     * 캠페인 코드
     */
    @NotNull(message = "campaignCode can not null")
    private String campaignCode;


    /**
     * 참여자 식별 정보
     */
    @NotNull(message = "userKey can not null")
    private String userKey;


    /**
     * 지급 금액
     */
    @NotNull(message = "cost can not null")
    private BigDecimal cost;


    /**
     * USER-AGENT
     */
    private String userAgent;


    /**
     * 호출 IP ADDRESS
     */
    private String remoteIp;



    /**
     * 퀴즈 정답
     */
    private String answer;


}
