package com.ads.main.vo.resp;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Setter;
import org.apache.tomcat.util.buf.HexUtils;

@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandingVo {

    @JsonIgnore
    private String groupCode;

    @JsonIgnore
    private String requestId;

    @JsonIgnore
    private String appServerUrl;

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

    public LandingVo(String groupCode, String requestId,  String appServerUrl) {
        this.groupCode = groupCode;
        this.requestId = requestId;
        this.appServerUrl = appServerUrl;
    }

    public String getThumb() {
        if (this.campaignCode != null) {
            return this.appServerUrl.concat("/api/v1/ads/request/"+requestId+"/"+groupCode+"/"+this.campaignCode+"?redirect=").concat(HexUtils.toHexString(this.thumb.getBytes()));
        } else {
            return null;
        }
    }

    public String getDetail_page() {
        if (this.campaignCode != null) {
            return this.appServerUrl.concat("/api/v1/ads/detail/"+requestId+"/"+this.campaignCode);
        } else {
            return null;
        }
    }

    public String getDetail() {
        if (this.detail != null) {
            return this.appServerUrl.concat("/api/v1/ads/impression/"+requestId+"?redirect=").concat(HexUtils.toHexString(this.detail.getBytes()) );
        } else {
            return null;
        }
    }

    public String getAnswer() {
        if (this.answer != null) {
            return this.appServerUrl.concat("/api/v1/ads/answer/"+requestId+"/"+answer);
        } else {
            return null;
        }
    }

    public String getHint_ad_pc() {
        if (this.hint_ad_pc != null) {
            return this.appServerUrl.concat("/api/v1/ads/click/hint/"+requestId+"?redirect=").concat(HexUtils.toHexString(this.hint_ad_pc.getBytes()) );
        } else {
            return null;
        }
    }

    public String getHint_ad_mobile() {
        if (this.hint_ad_mobile != null) {




            return this.appServerUrl.concat("/api/v1/ads/click/hint/"+requestId+"?redirect=").concat( HexUtils.toHexString(this.hint_ad_mobile.getBytes()) );
        } else {
            return null;
        }
    }

    public String getAnswer_ad_pc() {
        if (this.answer_ad_pc != null) {
            return this.appServerUrl.concat("/api/v1/ads/click/answer/"+requestId+"?redirect=").concat(HexUtils.toHexString(this.answer_ad_pc.getBytes()) );
        } else {
            return null;
        }
    }

    public String getAnswer_ad_mobile() {
        if (this.answer_ad_mobile != null) {
            return this.appServerUrl.concat("/api/v1/ads/click/answer/"+requestId+"?redirect=").concat(HexUtils.toHexString(this.answer_ad_mobile.getBytes()) );
        } else {
            return null;
        }
    }
}
