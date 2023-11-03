package com.ads.main.vo.campaign.resp;

import com.ads.main.vo.FilesVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class AdQuizVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 순번
     */
    private Long seq;


    /**
     * 캠페인 순번
     */
    private AdCampaignMasterVo adCampaignMasterVo;


    /**
     * 참여 방법
     */
    private String useHow;


    /**
     * 퀴즈 문제
     */
    private String quizTitle;

    /**
     * 퀴즈 정답
     */
    private String quizAnswer;

    /**
     * 리스트 노출 이미지
     */
    private FilesVo mainImage;

    /**
     * 상세 노출 이미지1
     */
    private FilesVo detailImage1;

    /**
     *상세 노출 이미지2
     */
    private FilesVo detailImage2;

    /**
     * 랜딩 URL(PC)
     */
    private String targetUrlPc;

    /**
     * 랜딩 URL(MOBILE)
     */
    private String targetUrlMobile;

    /**
     * 상품 코드
     */
    private String goodsCode;

}
