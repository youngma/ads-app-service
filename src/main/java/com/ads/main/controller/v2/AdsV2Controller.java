package com.ads.main.controller.v2;


import com.ads.main.core.config.exception.AdAnswerException;
import com.ads.main.core.config.exception.AppException;
import com.ads.main.core.config.exception.NoAdException;
import com.ads.main.core.enums.inquiry.InquiryType;
import com.ads.main.core.security.config.dto.Role;
import com.ads.main.core.utils.AppUtils;
import com.ads.main.core.vo.RespVo;
import com.ads.main.service.AdCampaignService;
import com.ads.main.service.AdGroupCacheService;
import com.ads.main.service.AdInquiryService;
import com.ads.main.vo.adGroup.resp.PartnerPostBackVo;
import com.ads.main.vo.campaign.req.RptAdAnswer;
import com.ads.main.vo.campaign.req.RptAdClick;
import com.ads.main.vo.campaign.req.RptAdImpression;
import com.ads.main.vo.campaign.req.RptAdRequest;
import com.ads.main.vo.inquiry.req.AdInquiryReqVo;
import com.ads.main.vo.resp.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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
@RequestMapping("/app/v2/ads")
@Validated
public class AdsV2Controller {


    private final AdCampaignService adCampaignService;
    private final AdGroupCacheService adGroupService;
    private final AdInquiryService adInquiryService;

    @GetMapping("/search/{ad-group}")
    public RespVo<PageAds<QuizAdsV2>> getAds(
            @PathVariable("ad-group") String adGroup,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) throws NoAdException, AppException {
        adGroupService.checkAdGroupCode(adGroup);
        return new RespVo<>(adCampaignService.requestList(adGroup, page, size));
    }

    // 광고 상세 정보 요청 ()
    @GetMapping("/check/{ad-group}/{ad-code}")
    public RespVo<QuizAds> detail(
            @PathVariable("ad-group") String adGroup,
            @PathVariable("ad-code") String adCode,
            @RequestParam("user-key") @Validated @NotBlank(message = "사용자 식별키는 필수 값 입니다.") String user,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent,
            HttpServletRequest request
    ) throws NoAdException {

        String requestId = RandomStringUtils.randomAlphabetic(15);

        RptAdRequest rptAdRequest = new RptAdRequest(adGroup, requestId, adCode, user, userAgent,  AppUtils.etRemoteAddr(request));
        RptAdImpression rptAdImpression = new RptAdImpression(Role.PARTNER, requestId, adCode, userAgent, AppUtils.etRemoteAddr(request), BigDecimal.ZERO, user);
        QuizAds quizAds = adCampaignService.markRequestAndImpression(rptAdRequest, rptAdImpression);

        return  new RespVo<>(quizAds);
    }

}
