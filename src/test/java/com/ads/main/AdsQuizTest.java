package com.ads.main;

import com.ads.main.core.config.exception.AppException;
import com.ads.main.core.config.exception.NoAdException;
import com.ads.main.service.AdCampaignService;
import com.ads.main.vo.TestLandingVo;
import com.ads.main.vo.TestRespVo;
import com.ads.main.vo.resp.PageAds;
import com.ads.main.vo.resp.QuizAds;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class}) // When using JUnit5
@SpringBootTest(properties = { "spring.profiles.active", "test" })
@Slf4j
public class AdsQuizTest {


    @Autowired
    private AdCampaignService adCampaignService;


//    @Test
    public void QuizAnswer() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1000);

        List<Thread> threadList = Stream
                .generate(() -> new Thread(new LockAnswer(countDownLatch, this.adCampaignService)))
                .limit(1000)
                .toList();

        threadList.forEach(Thread::start);
        countDownLatch.await();
    }


    @Test
    public void test02() {

        int adPrice = 200;
        int partnerCommission;
        int userCommission;

        int partnerAdPrice = Math.round(((float) adPrice / 100) * 30);
        int userAdPrice = Math.round(((float) partnerAdPrice / 100) * 30);


        log.info("{}, {}",partnerAdPrice,userAdPrice );
    }

    private class LockAnswer implements Runnable {
        private final CountDownLatch countDownLatch;
        private final AdCampaignService adCampaignService;

        private final ObjectMapper objectMapper = new ObjectMapper();
        private final String user = RandomStringUtils.randomAlphabetic(5);

        public LockAnswer(CountDownLatch countDownLatch, AdCampaignService adCampaignService) {
            this.countDownLatch = countDownLatch;
            this.adCampaignService = adCampaignService;
        }

        @Override
        public void run() {

            try {
                PageAds<QuizAds> quizAds = adCampaignService.requestList("WezFzQSZME", "all", "user-key",1, 10);
                quizAds.content().forEach(ad -> {
                    String thumb = ad.getLanding().getThumb();
                    String detailPage = ad.getLanding().getDetail_page();

                    try {

                        HttpClient client = HttpClient.newHttpClient();

                        HttpRequest req1 =  HttpRequest.newBuilder().GET().uri(new URI(thumb)).build();
                        HttpResponse<String> response1 = client.send(req1, HttpResponse.BodyHandlers.ofString());

                        TestRespVo details = objectMapper.readValue(new URL(detailPage), TestRespVo.class);

                        if (details != null) {

                            TestLandingVo landingVo = details.getResult().getLanding();

                            Stream.of(
                                    landingVo.getDetail(),
                                    landingVo.getHint_ad_pc(),
                                    landingVo.getAnswer_ad_pc()
                            ).forEach(t -> {
                                try {
                                    HttpRequest req =  HttpRequest.newBuilder().GET().uri(new URI(t)).build();
                                    client.send(req, HttpResponse.BodyHandlers.ofString());
                                } catch (URISyntaxException | IOException | InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                            HttpRequest req_answer =  HttpRequest.newBuilder().GET().uri(new URI(landingVo.getAnswer().concat("?answer=정답&user-key=" + user))).build();
                            client.send(req_answer, HttpResponse.BodyHandlers.ofString());

                        }

                    } catch (URISyntaxException | IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                });

            } catch (NoAdException | AppException e) {
                throw new RuntimeException(e);
            } finally {
                countDownLatch.countDown();
            }
//            couponService.useCouponWithLock(this.couponKey);
        }
    }

}
