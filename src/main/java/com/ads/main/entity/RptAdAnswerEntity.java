package com.ads.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * 광고 정답 현황
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "RPT_AD_ANSWER")
public class RptAdAnswerEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 광고 요청 코드
     */
    @Id
    @Column(name = "REQUEST_ID", nullable = false)
    private String requestId;

    /**
     * 캠페인 코드
     */
    @Column(name = "CAMPAIGN_CODE", nullable = false)
    private String campaignCode;

    /**
     * 참여자 식별 정보
     */
    @Column(name = "USER_KEY", nullable = false)
    private String userKey;

    /**
     * 지급 금액
     */
    @Column(name = "REWORD", nullable = false)
    private BigDecimal reword;

    /**
     * USER-AGENT
     */
    @Column(name = "USER_AGENT")
    private String userAgent;

    /**
     * 호출 IP ADDRESS
     */
    @Column(name = "REMOTE_IP")
    private String remoteIp;


    /**
     * 정답
     */
    @Column(name = "ANSWER")
    private String answer;

    /**
     * 퀴즈 정답 입력 일시
     */
    @Column(name = "ANSWER_AT", nullable = false)
    @CreatedDate
    private LocalDateTime answerAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        RptAdAnswerEntity that = (RptAdAnswerEntity) o;
        return getRequestId() != null && Objects.equals(getRequestId(), that.getRequestId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
