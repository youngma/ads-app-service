package com.ads.main.entity;

import com.ads.main.core.config.jpa.BaseEntity;
import com.ads.main.core.enums.inquiry.InquiryStatus;
import com.ads.main.core.enums.inquiry.InquiryType;
import com.ads.main.core.enums.inquiry.convert.InquiryStatusConvert;
import com.ads.main.core.enums.inquiry.convert.InquiryTypeConvert;
import com.ads.main.vo.inquiry.req.AdInquiryReqVo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 문의사항
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "AD_INQUIRY")
public class AdInquiryEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 순번
     */
    @Id
    @Column(name = "SEQ", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    /**
     * 문의 종류
     */
    @Column(name = "INQUIRY_TYPE")
    @Convert(converter = InquiryTypeConvert.class)
    private InquiryType inquiryType;


    /**
     * 광고 그룹 코드
     */
    @Column(name = "GROUP_CODE", nullable = false)
    private String groupCode;


    /**
     * 캠페인 코드
     */
    @Column(name = "CAMPAIGN_CODE")
    private String campaignCode;

    /**
     * 문의 사항 제목
     */
    @Column(name = "QUIZ_TITLE")
    private String quizTitle;

    /**
     * 문의 사항 제목
     */
    @Column(name = "TITLE")
    private String title;

    /**
     * 문의 사항 내용
     */
    @Column(name = "ANSWER")
    private String answer;

    /**
     * 상태
     */
    @Column(name = "STATUS")
    @Convert(converter = InquiryStatusConvert.class)
    private InquiryStatus status;

    /**
     * 질문 등록 일시
     */
    @Column(name = "REQUEST_AT")
    private LocalDateTime requestAt;

    /**
     * 답변 등록 일시
     */
    @Column(name = "ANSWER_AT")
    private LocalDateTime answerAt;

    /**
     * 전화 번호
     */
    @Column(name = "PHONE")
    private String phone;


    public static AdInquiryEntity inquiry(AdInquiryReqVo inquiryReqVo) {
        AdInquiryEntity adInquiryEntity = new AdInquiryEntity();

        adInquiryEntity.setInquiryType(inquiryReqVo.getInquiryType());
        adInquiryEntity.setGroupCode(inquiryReqVo.getGroupCode());
        adInquiryEntity.setQuizTitle(inquiryReqVo.getQuizTitle());
        adInquiryEntity.setTitle(inquiryReqVo.getTitle());
        adInquiryEntity.setPhone(inquiryReqVo.getPhone());
        adInquiryEntity.setInsertedId(inquiryReqVo.getUser());
        adInquiryEntity.setRequestAt(LocalDateTime.now());
        adInquiryEntity.setStatus(InquiryStatus.Request);

        if (InquiryType.Quiz.equals(inquiryReqVo.getInquiryType())) {
            adInquiryEntity.setCampaignCode(inquiryReqVo.getCampaignCode());
        }

        return adInquiryEntity;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AdInquiryEntity that = (AdInquiryEntity) o;
        return getSeq() != null && Objects.equals(getSeq(), that.getSeq());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
