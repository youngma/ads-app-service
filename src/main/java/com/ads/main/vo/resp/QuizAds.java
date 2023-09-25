package com.ads.main.vo.resp;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizAds {

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


    private LandingVo landing;
}
