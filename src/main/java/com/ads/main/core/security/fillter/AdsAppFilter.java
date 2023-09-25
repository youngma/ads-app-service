package com.ads.main.core.security.fillter;

import com.ads.main.service.AdGroupCacheService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = { "/api/ads/v1/search/*" })
@RequiredArgsConstructor
public class AdsAppFilter extends OncePerRequestFilter {

    private final AdGroupCacheService adGroupService;
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        int start = request.getRequestURI().lastIndexOf("/");
        String groupCode = request.getRequestURI().substring(start+1);

        adGroupService.findCampaignType(groupCode);

        filterChain.doFilter(request, response);
    }
}
