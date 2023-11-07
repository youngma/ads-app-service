package com.ads.main.enums;

import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.core.vo.CodeVo;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


@Getter
public enum CampaignJoinType {


    Join("true", "참여"),
    Not_Join("false", "미참여"),
    All("all", "전체"),

    ;


    private final String code;
    private final String name;


    CampaignJoinType(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static CampaignJoinType of(final String code) {
        return Arrays.stream(CampaignJoinType.values())
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElseGet(() -> CampaignJoinType.All);
    }



}
