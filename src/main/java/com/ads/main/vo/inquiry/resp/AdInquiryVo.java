package com.ads.main.vo.inquiry.resp;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


@Data
public class AdInquiryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 순번
     */
    @NotNull(message = "seq can not null")
    private Long seq;


    /**
     * 문의 종류
     */
    private String inquiryType;
    private String inquiryTypeName;


    private String groupCode;

    private String campaignCode;


    /**
     * 문의 사항 제목
     */
    private String title;


    /**
     * 문의 사항 내용
     */
    private String answer;


    /**
     * 상태
     */
    private String status;
    private String statusName;


    /**
     * 질문 등록 일시
     */
    private Date requestAt;


    /**
     * 답변 등록 일시
     */
    private Date answerAt;


    /**
     * 등록 일자
     */
    @NotNull(message = "insertedAt can not null")
    private Date insertedAt;


    /**
     * 등록자
     */
    private String insertedId;


    /**
     * 수정 일자
     */
    @NotNull(message = "updatedAt can not null")
    private Date updatedAt;


    /**
     * 수정자
     */
    private String updatedId;

}
