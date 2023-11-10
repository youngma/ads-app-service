package com.ads.main.service;

import com.ads.main.core.config.exception.AppException;
import com.ads.main.core.config.exception.NoAdException;
import com.ads.main.core.enums.campaign.CampaignType;
import com.ads.main.enums.AdException;
import com.ads.main.enums.AdGroupException;
import com.ads.main.enums.AdJoinException;
import com.ads.main.vo.campaign.req.RptAdAnswer;
import com.ads.main.vo.campaign.resp.AdCampaignMasterVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.ads.main.enums.AdException.NO_AD;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdQuizRedisService {

    private final static DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final RedissonClient redissonClient;


    private final ObjectMapper objectMapper;


    @Value("${app.quiz.join-key.daily}")
    private String dailyJoinKey;


    @Value("${app.quiz.join-key.total}")
    private String totalJoinKey;

    @Value("${app.quiz.join-key.user}")
    private String userJoinKey;


    @Value("${app.quiz.lock-key}")
    private String quizLockKey;



    @PostConstruct
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Cacheable(
            cacheNames = "ad-campaign"
            , key = "#campaignCode"
            , unless = "#result == null"
    )
    public AdCampaignMasterVo findCampaignByCode(String campaignCode) throws NoAdException {
        RMap<String, String> maps = redissonClient.getMap(CampaignType.Quiz01.getCode());
        String campaignStrStr =  maps.get(campaignCode);
        try {
            if (maps.containsKey(campaignCode)) {
                return objectMapper.readValue(campaignStrStr, AdCampaignMasterVo.class);
            }
        } catch (JsonProcessingException e) {
            log.error("# ad detail parsing error: ", e);
        }
        throw AdException.NO_AD.throwErrors();
    }


    // 퀴즈 참여 프로세스
    public boolean joinProcess(AdCampaignMasterVo adCampaignMaster, RptAdAnswer rptAdAnswer) throws AppException {

        log.debug("# AD -> {}", adCampaignMaster);

        String quizCode = adCampaignMaster.getCampaignCode();
        long total = adCampaignMaster.getTotalParticipationLimit();
        long daily = adCampaignMaster.getDayParticipationLimit();
        String answer = adCampaignMaster.getQuiz().getQuizAnswer();

        log.debug("# 정답 => {}", answer);


        final String lockName = quizLockKey.concat(":lock");

        final RLock lock = redissonClient.getLock(lockName);
        final String worker = Thread.currentThread().getName();

        try {
            if(!lock.tryLock(1, 3, TimeUnit.SECONDS))
                throw AdJoinException.ANSWER_WAIT.throwErrors();

            final long check = joinCountCheck(quizCode, total, daily);

            if(check < 0L) {
                throw AdJoinException.JOIN_LIMIT.throwErrors();
            }

            log.info("현재 진행중 인 사람 : {}", worker);

            if (joinUserCheck(quizCode, rptAdAnswer.user())) {
                throw AdJoinException.ALREADY_JOIN.throwErrors();
            }

            if (!answer.equals(rptAdAnswer.answer())) {
                throw AdJoinException.NO_ANSWER.throwErrors();
            }

            joinCountIncrement(quizCode, rptAdAnswer.user());

            return true;

        } catch (InterruptedException e) {
            log.error("# joinCount Error", e);
            throw AdJoinException.ANSWER_WAIT.throwErrors();
        } finally {
            if(lock != null && lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    private long joinCountCheck(String quizCode, long total, long daily) {

        String dailyKey = getKey(quizCode, "daily");
        String totalKey = getKey(quizCode, "total");

        RAtomicLong dailyNumber = redissonClient.getAtomicLong(dailyKey);
        RAtomicLong totalNumber = redissonClient.getAtomicLong(totalKey);

        log.debug("# Quiz [{}] , Total : {}, Daily: {}", quizCode, dailyNumber.get(), totalNumber.get());

        if (dailyNumber.get() == daily) {
            return -1;
        }

        if (totalNumber.get() == total) {
            return -1;
        }

        return dailyNumber.get();
    }

    private boolean joinUserCheck(String quizCode, String user) {
        String userKey = getKey(quizCode, "user");
        RSet<String> userSet =  redissonClient.getSet(userKey);
        return userSet.contains(user);
    }

    private void joinCountIncrement(String quizCode, String user) {
        String dailyKey = getKey(quizCode, "daily");
        String totalKey = getKey(quizCode, "total");
        String userKey = getKey(quizCode, "user");
        redissonClient.getAtomicLong(dailyKey).incrementAndGet();
        redissonClient.getAtomicLong(totalKey).incrementAndGet();
        redissonClient.getSet(userKey).add(user);
    }

    private String getKey(String quizCode, String type) {
        String date = LocalDateTime.now().format(formatter);
        return switch (type) {
            case  "daily" -> quizCode.concat(":").concat(date).concat(":").concat(dailyJoinKey);
            case  "total" -> quizCode.concat(":").concat(totalJoinKey);
            case  "user" -> quizCode.concat(":").concat(userJoinKey);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
