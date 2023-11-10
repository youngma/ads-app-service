package com.ads.main.vo.campaign.req;

import java.math.BigDecimal;

public record RptAdAnswer(
        String requestId,
        String campaignCode,
        String userAgent,
        String remoteIp,
        String user,
        String answer
) {

}
