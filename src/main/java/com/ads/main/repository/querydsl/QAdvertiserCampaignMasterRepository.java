package com.ads.main.repository.querydsl;

import com.ads.main.core.enums.campaign.CampaignStatus;
import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.entity.AdCampaignMasterEntity;
import com.ads.main.entity.QAdQuizEntity;
import com.ads.main.vo.campaign.resp.AdQuizVo;
import com.querydsl.core.types.Projections;
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

@Repository
@RequiredArgsConstructor
public class QAdvertiserCampaignMasterRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;

    public Page<AdCampaignMasterEntity> findAdCampaigns(CampaignType campaignType, Pageable pageable) {

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
                        adCampaignMasterEntity.campaignStatus.eq(CampaignStatus.Approval)
                    .and(adCampaignMasterEntity.campaignType.eq(campaignType))
                    .and(adCampaignMasterEntity.adStartDate.before(LocalDateTime.now()))
                    .and(adCampaignMasterEntity.adEndDate.after(LocalDateTime.now()))

                )
                .orderBy(adCampaignMasterEntity.seq.desc())

                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())

                .fetch();

        Long count = jpaQueryFactory.select(adCampaignMasterEntity.count())
                .from(adCampaignMasterEntity)
                .where(
                    adCampaignMasterEntity.campaignStatus.eq(CampaignStatus.Approval)
                    .and(adCampaignMasterEntity.campaignType.eq(campaignType))
                    .and(adCampaignMasterEntity.adStartDate.before(LocalDateTime.now()))
                    .and(adCampaignMasterEntity.adEndDate.after(LocalDateTime.now()))
                )
                .fetchOne();

        return new PageImpl<>(adCampaignEntities, pageable, Objects.requireNonNullElse(count, 0L));
    }

    public Optional<AdCampaignMasterEntity> findAdCampaign(String campaignCode) {

        return Optional.ofNullable(jpaQueryFactory.select(
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
