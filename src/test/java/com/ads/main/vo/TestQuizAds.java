package com.ads.main.vo;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TestQuizAds {

    /**
     * 광고 요청 ID
     */
    private String requestId;

    /**
     * 캠페인 명
     */
    private String campaignName;

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

    // 퀴즈 광고 속성

    /**
     * 참여 방법
     */
    private String useHow;

    /**
     * 퀴즈 문제
     */
    private String quizTitle;


    /**
     * 상품 코드
     */
    private String goodsCode;


    private TestLandingVo landing;
}
