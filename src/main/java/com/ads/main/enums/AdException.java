package com.ads.main.enums;

import com.ads.main.core.config.exception.NoAdException;

public enum AdException {

    NO_AD("존재 하지 않는 광고 입니다."),
    PERIOD_ENDS("기간이 종료 되었습니다."),
    ;

    private final String message;

    AdException(String message) {
        this.message = message;
    }


    public NoAdException throwErrors() {
        return new NoAdException(this.message);
    }

}


