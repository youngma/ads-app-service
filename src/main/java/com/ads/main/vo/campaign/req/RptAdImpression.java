package com.ads.main.vo.campaign.req;

import com.ads.main.core.security.config.dto.Role;

import java.math.BigDecimal;

public record RptAdImpression(Role role, String requestId, String campaignCode, String userAgent, String remoteIp, BigDecimal adCost) {

}
