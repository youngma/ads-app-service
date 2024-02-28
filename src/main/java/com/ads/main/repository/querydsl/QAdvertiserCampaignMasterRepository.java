package com.ads.main.repository.querydsl;

import com.ads.main.core.enums.campaign.CampaignStatus;
import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.entity.AdCampaignMasterEntity;
import com.ads.main.entity.PartnerAdGroupEntity;
import com.ads.main.entity.QPartnerAdGroupEntity;
import com.ads.main.entity.QPartnerAdMappingEntity;
import com.ads.main.enums.CampaignJoinType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.ads.main.entity.QAdCampaignMasterEntity.adCampaignMasterEntity;
import static com.ads.main.entity.QPartnerAdGroupEntity.partnerAdGroupEntity;
import static com.ads.main.entity.QPartnerAdMappingEntity.partnerAdMappingEntity;
import static com.ads.main.entity.QRptAdAnswerEntity.rptAdAnswerEntity;

@Repository
@RequiredArgsConstructor
public class QAdvertiserCampaignMasterRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;

    public Page<AdCampaignMasterEntity> findAdCampaigns(CampaignType campaignType, String groupCode, CampaignJoinType campaignJoinType, Pageable pageable) {
        return findAdCampaigns(campaignType, groupCode, campaignJoinType, null, pageable);
    }

    public Page<AdCampaignMasterEntity> findAdCampaigns(CampaignType campaignType, String groupCode, CampaignJoinType campaignJoinType, String user, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(adCampaignMasterEntity.campaignStatus.eq(CampaignStatus.Approval));
        builder.and(adCampaignMasterEntity.exposureStatus.eq(true));
        builder.and(adCampaignMasterEntity.adStartDate.before(LocalDateTime.now()));
        builder.and(adCampaignMasterEntity.adEndDate.after(LocalDateTime.now()));

        builder.and(adCampaignMasterEntity.seq.in(JPAExpressions
                .select(partnerAdMappingEntity.campaignSeq)
                .from(partnerAdMappingEntity)
                .join(partnerAdGroupEntity)
                .on(partnerAdGroupEntity.groupSeq.eq(partnerAdMappingEntity.groupSeq))
                .where(partnerAdGroupEntity.groupCode.eq(groupCode))));

        switch (campaignJoinType) {
            case Join -> builder.and(adCampaignMasterEntity.campaignCode.in(JPAExpressions
                    .select(rptAdAnswerEntity.campaignCode)
                    .from(rptAdAnswerEntity)
                    .where(rptAdAnswerEntity.userKey.eq(user))));

            case Not_Join -> builder.and(adCampaignMasterEntity.campaignCode.notIn(JPAExpressions
                    .select(rptAdAnswerEntity.campaignCode)
                    .from(rptAdAnswerEntity)
                    .where(rptAdAnswerEntity.userKey.eq(user))));
        }

        List<AdCampaignMasterEntity> adCampaignEntities =  jpaQueryFactory.select(
                        adCampaignMasterEntity
                )
                .from(adCampaignMasterEntity)
                .leftJoin(adCampaignMasterEntity.adSmartStoreEntity).fetchJoin()
                .leftJoin(adCampaignMasterEntity.adSmartStoreEntity.image).fetchJoin()
                .leftJoin(adCampaignMasterEntity.adQuizEntity).fetchJoin()
                .leftJoin(adCampaignMasterEntity.adQuizEntity.mainImage).fetchJoin()
                .leftJoin(adCampaignMasterEntity.adQuizEntity.detailImage1).fetchJoin()
                .leftJoin(adCampaignMasterEntity.adQuizEntity.detailImage2).fetchJoin()
                .where(
                    builder
                )

                .orderBy(adCampaignMasterEntity.seq.desc())

                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())

                .fetch();

        Long count = jpaQueryFactory.select(adCampaignMasterEntity.count())
                .from(adCampaignMasterEntity)
                .where(
                    builder
                )
                .fetchOne();

        return new PageImpl<>(adCampaignEntities, pageable, Objects.requireNonNullElse(count, 0L));
    }

    public Optional<AdCampaignMasterEntity> findAdCampaign(String campaignCode) {

        return Optional.ofNullable(jpaQueryFactory.select(
                    adCampaignMasterEntity
                )
                .from(adCampaignMasterEntity)
                .leftJoin(adCampaignMasterEntity.advertiserEntity).fetchJoin()
                .leftJoin(adCampaignMasterEntity.adSmartStoreEntity).fetchJoin()
                .leftJoin(adCampaignMasterEntity.adSmartStoreEntity.image).fetchJoin()
                .leftJoin(adCampaignMasterEntity.adQuizEntity).fetchJoin()
                .leftJoin(adCampaignMasterEntity.adQuizEntity.mainImage).fetchJoin()
                .leftJoin(adCampaignMasterEntity.adQuizEntity.detailImage1).fetchJoin()
                .leftJoin(adCampaignMasterEntity.adQuizEntity.detailImage2).fetchJoin()

                .where(
                        adCampaignMasterEntity.campaignStatus.eq(CampaignStatus.Approval)
                                .and(adCampaignMasterEntity.campaignCode.eq(campaignCode))
                                .and(adCampaignMasterEntity.adStartDate.before(LocalDateTime.now()))
                                .and(adCampaignMasterEntity.adEndDate.after(LocalDateTime.now()))

                )
                .fetchOne());
    }

    public Optional<String> findQuizAnswer(String campaignCode) {

        return Optional.ofNullable(jpaQueryFactory.select(
                    adCampaignMasterEntity.adQuizEntity.quizAnswer
                )
                .from(adCampaignMasterEntity)
                .innerJoin(adCampaignMasterEntity.adQuizEntity)
                .where(
                    adCampaignMasterEntity.campaignStatus.eq(CampaignStatus.Approval)
                    .and(adCampaignMasterEntity.campaignCode.eq(campaignCode))
                    .and(adCampaignMasterEntity.adStartDate.before(LocalDateTime.now()))
                    .and(adCampaignMasterEntity.adEndDate.after(LocalDateTime.now()))
                )
                .fetchOne());
    }

}
