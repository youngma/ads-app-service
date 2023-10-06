package com.ads.main.enums;

import com.ads.main.core.config.exception.AppException;

public enum AdGroupException {

    AD_GROUP_NOT_FOUND("파트너 광고 그룹를 찾을 수 없습니다."),
    PERIOD_ENDS("기간이 종료 되었습니다."),
//    ANSWER_WAIT("잠시후 다시 시도 해주세요."),
//    JOIN_LIMIT("참여자 수 를 초과 하였습니다."),
    ;

    private final String message;

    AdGroupException(String message) {
        this.message = message;
    }

    public AppException throwErrors() {
        return new AppException(this.message);
    }

}


