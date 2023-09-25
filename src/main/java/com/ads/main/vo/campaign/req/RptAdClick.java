package com.ads.main.vo.campaign.req;

import java.math.BigDecimal;

public record RptAdClick(
        String requestId,
        String target,
        String userAgent,
        String remoteIp
) {

}
