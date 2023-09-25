package com.ads.main.controller;


import com.ads.main.core.config.exception.NoAdException;
import com.ads.main.core.security.config.dto.Role;
import com.ads.main.core.utils.AppUtils;
import com.ads.main.core.vo.RespVo;
import com.ads.main.service.AdCampaignService;
import com.ads.main.service.AdGroupCacheService;
import com.ads.main.vo.campaign.req.RptAdAnswer;
import com.ads.main.vo.campaign.req.RptAdClick;
import com.ads.main.vo.campaign.req.RptAdImpression;
import com.ads.main.vo.campaign.req.RptAdRequest;
import com.ads.main.vo.resp.PageAds;
import com.ads.main.vo.resp.QuizAds;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.URLEncoder;
import org.apache.logging.log4j.util.Strings;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/ads")
public class AdsController {


    private final AdCampaignService adCampaignService;
    private final AdGroupCacheService adGroupService;

    @GetMapping("/search/{ad-group}")
    public RespVo<PageAds<QuizAds>> getAds(
            @PathVariable("ad-group") String adGroup,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) throws NoAdException {
//        log.debug("# adGroup {}, page {}, size {}", adGroup, page, size);
        adGroupService.checkAdGroupCode(adGroup);
        return new RespVo<>(adCampaignService.requestList(adGroup, page, size));
    }


    // 메인 이미지가 로딩 ( request check )
    @GetMapping("/request/{request-id}/{ad-code}")
    public void request(
            @PathVariable("request-id") String requestId,
            @PathVariable("ad-code") String adCode,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent,
            @Validated @NotBlank @RequestParam("redirect") String redirect,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {


        log.info("# requestId => {}", requestId);
        log.info("# adCode => {}", adCode);
        log.info("# userAgent => {}", userAgent);
        log.info("# remoteIp => {}", AppUtils.etRemoteAddr(request));
        log.info("# redirect => {}", redirect);

        adCampaignService.markRequest(new RptAdRequest(requestId, adCode, userAgent,  AppUtils.etRemoteAddr(request)));

        response.sendRedirect(new String( HexUtils.fromHexString(redirect)));

    }


    // 광고 상세 정보 요청 ()
    @GetMapping("/detail/{request-id}/{ad-code}")
    public RespVo<QuizAds> detail(
            @PathVariable("request-id") String requestId,
            @PathVariable("ad-code") String adCode,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent,
            HttpServletRequest request
    ) throws NoAdException {

        log.info("# requestId => {}", requestId);
        log.info("# adCode => {}", adCode);
        log.info("# userAgent => {}", userAgent);

        QuizAds quizAds = adCampaignService.markImpression(new RptAdImpression(Role.PARTNER, requestId, adCode, userAgent, AppUtils.etRemoteAddr(request), BigDecimal.ZERO));

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

        log.info("# requestId => {}", requestId);
        log.info("# redirect => {}", redirect);

        adCampaignService.saveImpressionLog(new RptAdImpression(Role.ADVERTISER, requestId, "", userAgent, AppUtils.etRemoteAddr(request), BigDecimal.ZERO));

        response.sendRedirect(new String( HexUtils.fromHexString(redirect)));

    }

    // 정답 확인
    @GetMapping("/answer/{request-id}/{ad-code}")
    public String answer(
            @PathVariable("request-id") String requestId,
            @PathVariable("ad-code") String adCode,
            @RequestParam("answer") String answer,
            @RequestParam("user-key") String user,
            HttpServletRequest request,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent
    ) {

        log.info("# requestId => {}", requestId);
        log.info("# adCode => {}", adCode);
        log.info("# answer => {}", answer);
        log.info("# user => {}", user);

        return adCampaignService.checkAnswer(new RptAdAnswer(requestId, adCode, userAgent, AppUtils.etRemoteAddr(request), user, answer, BigDecimal.ZERO));
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

        log.info("# requestId => {}", requestId);
        log.info("# redirect => {}", redirect);
        log.info("# userAgent => {}", userAgent);

        adCampaignService.saveClickLog(new RptAdClick(requestId, target, userAgent, AppUtils.etRemoteAddr(request)));




        response.sendRedirect(new String( HexUtils.fromHexString(redirect)));

    }

}
