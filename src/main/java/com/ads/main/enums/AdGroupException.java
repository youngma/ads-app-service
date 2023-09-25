package com.ads.main.enums;

public enum AdGroupException {

    AD_GROUP_NOT_FOUND("파트너 광고 그룹를 찾을 수 없습니다."),
    NO_AD("존재 하지 않는 광고 입니다."),
    PERIOD_ENDS("기간이 종료 되었습니다."),
    ;

    private final String message;

    AdGroupException(String message) {
        this.message = message;
    }


    public RuntimeException throwErrors() {
        return new RuntimeException(this.message);
    }

}


