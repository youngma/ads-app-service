package com.ads.main.enums;

import com.ads.main.core.config.exception.AppException;

public enum AdInquiryException {

    ERROR("문의 사항 등록 중 오류가 발생 했습니다."),
    ;

    private final String message;

    AdInquiryException(String message) {
        this.message = message;
    }

    public AppException throwErrors() {
        return new AppException(this.message);
    }

}


