package com.ads.main.controller;


import com.ads.main.core.config.exception.AdAnswerException;
import com.ads.main.core.config.exception.AppException;
import com.ads.main.core.config.exception.NoAdException;
import com.ads.main.core.enums.inquiry.InquiryType;
import com.ads.main.core.security.config.dto.Role;
import com.ads.main.core.utils.AppUtils;
import com.ads.main.core.vo.RespVo;
import com.ads.main.enums.AdJoinException;
import com.ads.main.service.AdCampaignService;
import com.ads.main.service.AdGroupCacheService;
import com.ads.main.service.AdInquiryService;
import com.ads.main.vo.campaign.req.RptAdAnswer;
import com.ads.main.vo.campaign.req.RptAdClick;
import com.ads.main.vo.campaign.req.RptAdImpression;
import com.ads.main.vo.campaign.req.RptAdRequest;
import com.ads.main.vo.inquiry.req.AdInquiryReqVo;
import com.ads.main.vo.resp.AnswerResp;
import com.ads.main.vo.resp.PageAds;
import com.ads.main.vo.resp.QuizAds;
import com.ads.main.vo.resp.RewordResp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/app/v1/ads")
@Validated
public class AdsController {


    private final AdCampaignService adCampaignService;
    private final AdGroupCacheService adGroupService;
    private final AdInquiryService adInquiryService;

    @GetMapping("/search/{ad-group}")
    public RespVo<PageAds<QuizAds>> getAds(
            @PathVariable("ad-group") String adGroup,
            @RequestParam("user-key") @Validated @NotBlank(message = "사용자 식별키는 필수 값 입니다.") String user,
            @RequestParam(value = "join", defaultValue = "All") String join,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) throws NoAdException, AppException {
        adGroupService.checkAdGroupCode(adGroup);
        return new RespVo<>(adCampaignService.requestList(adGroup, join, user, page, size));
    }


    // 메인 이미지가 로딩 ( request check )
    @GetMapping("/request/{request-id}/{ad-group}/{ad-code}")
    public void request(
            @PathVariable("request-id") String requestId,
            @PathVariable("ad-group") String adGroup,
            @PathVariable("ad-code") String adCode,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent,
            @Validated @NotBlank @RequestParam("redirect") String redirect,
            @RequestParam("user-key") String userKey,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        RptAdRequest rptAdRequest = new RptAdRequest(adGroup, requestId, adCode, userKey, userAgent,  AppUtils.etRemoteAddr(request));
        log.info("# rptAdRequest => {}", rptAdRequest);

        adCampaignService.markRequest(rptAdRequest);

        response.sendRedirect(new String( HexUtils.fromHexString(redirect)));

    }


    // 광고 상세 정보 요청 ()
    @GetMapping("/detail/{request-id}/{ad-code}")
    public RespVo<QuizAds> detail(
            @PathVariable("request-id") String requestId,
            @PathVariable("ad-code") String adCode,
            @RequestParam("user-key") @Validated @NotBlank(message = "사용자 식별키는 필수 값 입니다.") String user,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent,
            HttpServletRequest request
    ) throws NoAdException {

        RptAdImpression rptAdImpression = new RptAdImpression(Role.PARTNER, requestId, adCode, userAgent, AppUtils.etRemoteAddr(request), BigDecimal.ZERO, user);

        log.info("# rptAdImpression => {}", rptAdImpression);

        QuizAds quizAds = adCampaignService.markImpression(rptAdImpression);

        return  new RespVo<>(quizAds);
    }

    // 광고 상세 이미지 노출 (impression check)
    @GetMapping("/impression/{request-id}")
    public void impression(
            @PathVariable("request-id") String requestId,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent,
            @Validated @NotBlank @RequestParam("redirect") String redirect,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        RptAdImpression rptAdImpression = new RptAdImpression(Role.ADVERTISER, requestId, "", userAgent, AppUtils.etRemoteAddr(request), BigDecimal.ZERO, "");
        log.info("# rptAdImpression => {}", rptAdImpression);

        adCampaignService.saveImpressionLog(rptAdImpression);

        response.sendRedirect(new String( HexUtils.fromHexString(redirect)) );
    }

    // 정답 확인
    @GetMapping("/answer/{request-id}/{ad-code}")
    public RespVo<AnswerResp> answer(
            @PathVariable("request-id") String requestId,
            @PathVariable("ad-code") String adCode,
            @RequestParam(value = "answer") @Validated @NotBlank(message = "정답은 필수 값 입니다.") String answer,
            @RequestParam("user-key") @Validated @NotBlank(message = "사용자 식별키는 필수 값 입니다.") String user,
            HttpServletRequest request,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent
    ) throws NoAdException, AppException, AdAnswerException {

        RptAdAnswer rptAdAnswer = new RptAdAnswer(requestId, adCode, userAgent, AppUtils.etRemoteAddr(request), user, answer);
        log.info("# rptAdAnswer => {}", rptAdAnswer);
        return  new RespVo<>(adCampaignService.checkAnswer(rptAdAnswer));
    }

    // 리워드 적립 회원 리스트
    @GetMapping("/reword/{ad-group}/{ad-code}")
    public RespVo<PageAds<RewordResp>> answer(
            @PathVariable("ad-group") String adGroup,
            @PathVariable("ad-code") String adCode,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) throws NoAdException, AppException {
        return  new RespVo<>(adCampaignService.selectRewords(adGroup, adCode, page, size));
    }

    // 광고주 페이지 랜딩
    @GetMapping("/click/{target}/{request-id}")
    public void click(
            @PathVariable("target") String target,
            @PathVariable("request-id") String requestId,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent,
            @Validated @NotBlank @RequestParam("redirect") String redirect,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, URISyntaxException {

        RptAdClick rptAdClick = new RptAdClick(requestId, target, userAgent, AppUtils.etRemoteAddr(request));

        log.info("# rptAdClick => {}", rptAdClick);

        adCampaignService.saveClickLog(rptAdClick);

        response.sendRedirect(new String( HexUtils.fromHexString(redirect)));

    }


    // 문의 하기(리스팅)
    @PostMapping("/inquiry/{ad-group}")
    public RespVo<String> inquiryByListing(
            @PathVariable("ad-group") String adGroup,
//            @RequestParam("user-key") String user,
            @RequestBody @Validated AdInquiryReqVo adInquiryReqVo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, URISyntaxException, AppException {

        adInquiryReqVo.setInquiryType(InquiryType.Service);
        adInquiryReqVo.setGroupCode(adGroup);
//        adInquiryReqVo.setUser(user);

        adInquiryService.save(adInquiryReqVo);

        return  new RespVo<>("등록 되었습니다.");
    }

    // 문의 하기(캠페인 상세)
    @PostMapping("/inquiry/{ad-group}/{ad-code}")
    public RespVo<String> inquiryByDetail(
            @PathVariable("ad-group") String adGroup,
            @PathVariable("ad-code") String adCode,
            @RequestBody @Validated AdInquiryReqVo adInquiryReqVo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, URISyntaxException, AppException {

        adInquiryReqVo.setInquiryType(InquiryType.Quiz);
        adInquiryReqVo.setGroupCode(adGroup);
        adInquiryReqVo.setCampaignCode(adCode);
//        adInquiryReqVo.setUser(user);

        adInquiryService.save(adInquiryReqVo);

        return  new RespVo<>("등록 되었습니다.");

    }
}
