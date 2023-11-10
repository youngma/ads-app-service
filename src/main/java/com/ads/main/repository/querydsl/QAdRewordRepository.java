package com.ads.main.repository.querydsl;

import com.ads.main.core.enums.campaign.CampaignStatus;
import com.ads.main.entity.AdCampaignMasterEntity;
import com.ads.main.vo.resp.RewordResp;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.ads.main.entity.QRptAdRequestEntity.rptAdRequestEntity;
import static com.ads.main.entity.QRptAdAnswerEntity.rptAdAnswerEntity;

@Repository
@RequiredArgsConstructor
public class QAdRewordRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;

    public Page<RewordResp> findAdRewordList(String groupCode, String campaignCode, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(rptAdRequestEntity.groupCode.eq(groupCode));
        builder.and(rptAdRequestEntity.campaignCode.eq(campaignCode));

        List<RewordResp> rewordResps =  jpaQueryFactory.select(
                        Projections.bean(RewordResp.class,
                                rptAdAnswerEntity.userKey.as("user"),
                                rptAdAnswerEntity.reword.as("reword")
                        )
                )
                .from(rptAdRequestEntity)
                .join((rptAdAnswerEntity)).on(rptAdRequestEntity.requestId.eq(rptAdAnswerEntity.requestId))
                .where(
                    builder
                )

                .orderBy(rptAdAnswerEntity.answerAt.desc())

                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())

                .fetch();

        Long count = jpaQueryFactory.select(rptAdAnswerEntity.count())
                .from(rptAdRequestEntity)
                .join((rptAdAnswerEntity)).on(rptAdRequestEntity.requestId.eq(rptAdAnswerEntity.requestId))
                .where(
                        builder
                )
                .fetchOne();

        return new PageImpl<>(rewordResps, pageable, Objects.requireNonNullElse(count, 0L));
    }

    public Integer findJoinUserCount(String groupCode, String campaignCode) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(rptAdRequestEntity.groupCode.eq(groupCode));
        builder.and(rptAdRequestEntity.campaignCode.eq(campaignCode));

        Long count = jpaQueryFactory.select(rptAdAnswerEntity.count())
                .from(rptAdRequestEntity)
                .join((rptAdAnswerEntity)).on(rptAdRequestEntity.requestId.eq(rptAdAnswerEntity.requestId))
                .where(
                        builder
                )
                .fetchOne();

        return Objects.requireNonNullElse(count, 0L).intValue();
    }

}
