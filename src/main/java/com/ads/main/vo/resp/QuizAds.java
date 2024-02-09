package com.ads.main.vo.resp;


import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.vo.adGroup.resp.PartnerAdGroupVo;
import com.ads.main.vo.campaign.resp.AdCampaignMasterVo;
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


    private boolean isJoined;

    /**
     * 상품 코드
     */
    private String goodsCode;


    private LandingVo landing;


    private Integer reword;

    private String pointName;

    private Integer joinUserCount;

    @JsonIgnore
    private PartnerAdGroupVo partnerAdGroupVo;

    @JsonIgnore
    private AdCampaignMasterVo adCampaignMasterVo;


    public Integer getReword() {

        if (adCampaignMasterVo == null || partnerAdGroupVo == null) {
            return reword;
        }

        int adPrice = adCampaignMasterVo.getAdPrice().intValue();

        int partnerCommission = 0;
        int userCommission = 0;

        // 파트너 광고 수수료
//        int partnerAdPrice = Math.round((float) (adPrice * partnerAdGroupVo.getCommissionRate()) / 100);

        // 유저 포인트
//        int userAdPrice = Math.round((float) (adPrice - partnerAdPrice *  partnerAdGroupVo.getUserCommissionRate()) / 100);

        int partnerAdPrice = Math.round(((float) adPrice / 100) * partnerAdGroupVo.getCommissionRate());
        int userAdPrice = Math.round(((float) partnerAdPrice / 100) * partnerAdGroupVo.getUserCommissionRate());

        if (CampaignType.Quiz02.getCode().equals(adCampaignMasterVo.getCampaignType())) {
            partnerCommission = adCampaignMasterVo.getCommissionRate().intValue();
            userCommission = adCampaignMasterVo.getUserCommissionRate().intValue();
        } else {
            partnerCommission = partnerAdPrice;
            userCommission = userAdPrice;
        }

        return Math.round((float) (userCommission * partnerAdGroupVo.getRewordRate()));
    }
}
