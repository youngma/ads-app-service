package com.ads.main.enums;

import com.ads.main.core.config.exception.AppException;

public enum AdJoinException {

    ANSWER_WAIT("잠시후 다시 시도 해주세요."),
    JOIN_LIMIT("참여자 수 를 초과 하였습니다."),
    NO_ANSWER("오답 입니다."),
    ALREADY_JOIN("이미 참여한 퀴즈 입니다."),
    ;

    private final String message;

    AdJoinException(String message) {
        this.message = message;
    }

    public AppException throwErrors() {
        return new AppException(this.message);
    }

}


