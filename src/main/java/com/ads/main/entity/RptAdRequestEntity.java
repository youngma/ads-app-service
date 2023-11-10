package com.ads.main.entity;

import com.ads.main.entity.ids.RptAdRequestID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 광고 요청
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@IdClass(RptAdRequestID.class)
@Table(name = "RPT_AD_REQUEST")
public class RptAdRequestEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 광고요청코드
     */
    @Id
    @Column(name = "REQUEST_ID", nullable = false)
    private String requestId;

    /**
     * 캠페인 코드
     */
    @Id
    @Column(name = "CAMPAIGN_CODE", nullable = false)
    private String campaignCode;

    /**
     * 광고 그룹 코드
     */
    @Id
    @Column(name = "GROUP_CODE", nullable = false)
    private String groupCode;

    /**
     * USER-AGENT
     */
    @Column(name = "USER_AGENT")
    private String userAgent;

    /**
     * 요청 일자
     */
    @Column(name = "REQUEST_AT", nullable = false)
    private Date requestAt;

    /**
     * 호출 IP ADDRESS
     */
    @Column(name = "REMOTE_IP")
    private String remoteIp;

    /**
     * 광고금액
     */
    @Column(name = "AD_PRICE")
    private Integer adPrice;

    /**
     * 사용자 지급금액
     */
    @Column(name = "USER_COMMISSION")
    private Integer userCommission;

    /**
     * 파트너 지급 금액
     */
    @Column(name = "PARTNER_COMMISSION")
    private Integer partnerCommission;

    /**
     * 지급 포인트(원)
     */
    @Column(name = "AD_POINT")
    private Integer adPoint;

    /**
     * 지급 포인트(파트너 포인트 기준)
     */
    @Column(name = "AD_REWORD")
    private Integer adReword;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        RptAdRequestEntity that = (RptAdRequestEntity) o;
        return getRequestId() != null && Objects.equals(getRequestId(), that.getRequestId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
