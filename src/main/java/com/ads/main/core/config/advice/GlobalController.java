package com.ads.main.core.config.advice;

import com.ads.main.core.enums.advertiser.AdGroupStatus;
import com.ads.main.core.enums.common.Bank;
import com.ads.main.core.enums.campaign.CampaignStatus;
import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.core.enums.campaign.PaymentTerms;
import com.ads.main.core.enums.user.UserStatus;
import com.ads.main.core.vo.CodeVo;
import com.ads.main.core.vo.RespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/xauth")
public class GlobalController {

}

