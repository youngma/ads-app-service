package com.ads.main.core.clients.api;

import com.ads.main.core.clients.WebClients;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Map;

public interface MobonApi extends WebClients {

    @GetExchange(value = "/servlet/adbnMobileBanner")
    String search(
            @RequestHeader Map<String, String> headers,
            @RequestParam String s,
            @RequestParam String cntad,
            @RequestParam String cntsr,
            @RequestParam String bntype,
            @RequestParam String sslRedirect
    );
}
