package com.ads.main.vo.campaign.req;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;


@Getter
@Setter
public class RptAdRequest{


    private final String groupCode;
    private final String requestId;
    private final String campaignCode;
    private final String userAgent;
    private final String remoteIp;
    private int adPrice;
    private int partnerCommission;
    private int userCommission;
    private int adReword;


    public RptAdRequest(String groupCode, String requestId, String campaignCode, String userAgent, String remoteIp) {
        this.groupCode = groupCode;
        this.requestId = requestId;
        this.campaignCode = campaignCode;
        this.userAgent = userAgent;
        this.remoteIp = remoteIp;
    }
}
