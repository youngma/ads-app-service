package com.ads.main.vo.inquiry.req;

import com.ads.main.core.enums.inquiry.InquiryStatus;
import com.ads.main.core.enums.inquiry.InquiryType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdInquiryReqVo implements Serializable {

    private InquiryType inquiryType;


    private String groupCode;

    private String campaignCode;

    @NotNull(message = "유저 정보는 필수값 입니다.")
    private String user;

    /**
     * 퀴즈 제목
     */
    @NotNull(message = "퀴즈 제목은 필수값 입니다.")
    private String quizTitle;

    /**
     * 문의 사항 제목
     */
    @NotNull(message = "문의 사항 제목은 필수값 입니다.")
    private String title;


    private InquiryStatus status;

    @NotNull(message = "전화번호는 은 필수값 입니다.")
    private String phone;
}
