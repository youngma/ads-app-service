package com.ads.main.vo.campaign.resp;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


@Data
public class AdCampaignMasterVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 순번
     */
    private Long seq;


    /**
     * 캠페인 명
     */
    private String campaignName;

    /**
     * 캠페인 타입
     */
    private String campaignType;

    /**
     * 캠페인 코드
     */
    private String campaignCode;


    /**
     * 캠페인 설명
     */
    private String campaignDesc;


    /**
     * 총 참여 가능 인원 횟수
     */
    private Integer totalParticipationLimit;


    /**
     * 일일 참여제한 횟수
     */
    private Integer dayParticipationLimit;


    private AdSmartStoreVo smartStore;
    private AdQuizVo quiz;
}
