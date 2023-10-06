package com.ads.main.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.buf.HexUtils;

@Setter
@Getter
public class TestLandingVo {

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

}
