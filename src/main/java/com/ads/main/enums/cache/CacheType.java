package com.ads.main.enums.cache;

import lombok.Getter;

@Getter
public enum CacheType {

    GROUP_CODE("ad-group", 10, 1000),     //
    GROUP_CODE_VALID("group-code-valid", 50, 1000),     //
    AD_GROUP("ad-group-campaign-type", 60, 1000),    // 파트너 광고 그룹
    AD_CAMPAIGN("ad-campaign", 1, 1000),               // 캠페인
    QUIZ_ANSWER("quiz-answer", 1, 1000),               // 캠페인 정답
    QUIZ_JOIN("quiz-join", 60 * 60 * 24, 10000),       // 캠페인 정답

    ;
    private final String name;
    private final int expiredAfterWriter;
    private final int maximumSize;

    CacheType(String name, int expiredAfterWriter, int maximumSize) {
        this.name = name;
        this.expiredAfterWriter = expiredAfterWriter;
        this.maximumSize = maximumSize;
    }
}
