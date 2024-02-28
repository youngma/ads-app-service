package com.ads.main.vo.resp;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Setter;
import org.apache.tomcat.util.buf.HexUtils;

@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandingV2Vo {

    @JsonIgnore
    private String groupCode;

    @JsonIgnore
    private String appServerUrl;

    @JsonIgnore
    private String userKey;

    private String campaignCode;

    // 광고 요청
    // private String request;
    private String thumb;

    // 광고 상세
    private String detail;

    // 퀴즈 정답 요청
    private String answer;

    // 광고주 랜딩 페이지 (힌트)
    private String hint_ad_pc;
    private String hint_ad_mobile;

    // 광고주 랜딩 페이지(정답)
    private String answer_ad_pc;
    private String answer_ad_mobile;

    // 리워드 적립 회원 조회
    private String reword;

    public LandingV2Vo(String groupCode, String appServerUrl, String userKey) {
        this.groupCode = groupCode;
        this.appServerUrl = appServerUrl;
        this.userKey = userKey;
    }


    public String getThumb() {
        if (this.campaignCode != null) {
            return this.thumb;
        } else {
            return null;
        }
    }

    public String getDetail_page() {
        return null;
    }

    public String getDetail() {
        if (this.detail != null) {
            return this.detail;
        } else {
            return null;
        }
    }

    public String getAnswer() {
        if (this.answer != null) {
            return this.appServerUrl.concat("/app/v2/ads/answer/quiz-ads/"+answer).concat("?user-key=").concat(userKey);
        } else {
            return null;
        }
    }

    public String getReword() {
        if (this.reword != null) {
            return this.appServerUrl.concat("/app/v2/ads/reword/"+groupCode+"/"+reword);
        } else {
            return null;
        }
    }

    public String getHint_ad_pc() {
        if (this.hint_ad_pc != null) {
            return this.appServerUrl.concat("/app/v2/ads/click/hint/quiz-ads?redirect=").concat(HexUtils.toHexString(this.hint_ad_pc.getBytes()) );
        } else {
            return null;
        }
    }

    public String getHint_ad_mobile() {
        if (this.hint_ad_mobile != null) {
            return this.appServerUrl.concat("/app/v2/ads/click/hint/quiz-ads?redirect=").concat( HexUtils.toHexString(this.hint_ad_mobile.getBytes()) );
        } else {
            return null;
        }
    }

    public String getAnswer_ad_pc() {
        if (this.answer_ad_pc != null) {
            return this.appServerUrl.concat("/app/v2/ads/click/answer/quiz-ads?redirect=").concat(HexUtils.toHexString(this.answer_ad_pc.getBytes()) );
        } else {
            return null;
        }
    }

    public String getAnswer_ad_mobile() {
        if (this.answer_ad_mobile != null) {
            return this.appServerUrl.concat("/app/v2/ads/click/answer/quiz-ads?redirect=").concat(HexUtils.toHexString(this.answer_ad_mobile.getBytes()) );
        } else {
            return null;
        }
    }
}
