package com.ads.main;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class}) // When using JUnit5
@SpringBootTest(properties = { "test" })
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class AdsAppApiTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }


    @Test
    @DisplayName("광고 목록 조회")
    public void SearchQuizAds() throws Exception {

        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("user-key", "test");
        info.add("join", "all");
        info.add("page", "1");
        info.add("size", "10");

            String groupCode = "brBJZBMxDe";
            this.mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/app/v1/ads/search/{group-code}", groupCode)
                    .queryParams(info)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    // restdoc
                    .andDo(
                            MockMvcRestDocumentationWrapper.document("ads-search"
                                    , preprocessResponse(prettyPrint())
                                    , pathParameters(
                                        parameterWithName("group-code").description("광고 지면 코드")
                                    )
                                    ,queryParameters(
                                            parameterWithName("user-key").description("파트너사 APP USER 식별키"),
                                            parameterWithName("join").description("참여 여부( true : 참여, fasle: 미참여, all: 전체)"),
                                            parameterWithName("page").description("페이지"),
                                            parameterWithName("size").description("페이지당 광고 ")
                                    )
                                    ,responseFields(
                                        fieldWithPath("result.content.[].requestId").description("광고 요청 키").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.content.[].campaignName").description("광고 제목").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.content.[].campaignCode").description("광고 코드").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.content.[].quizTitle").description("퀴즈 문제").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.content.[].pointName").description("포인트 명").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.content.[].reword").description("적립 포인트").type(JsonFieldType.NUMBER)
                                        , fieldWithPath("result.content.[].totalParticipationLimit").description("총 참여 가능 인원").type(JsonFieldType.NUMBER)
                                        , fieldWithPath("result.content.[].dayParticipationLimit").description("일별 참여 가능 인원").type(JsonFieldType.NUMBER)
                                        , fieldWithPath("result.content.[].joined").description("참여 여부").type(JsonFieldType.BOOLEAN)
                                        , fieldWithPath("result.content.[].landing.thumb").description("메인 페이지 이미지(광고 요청 처리 용도)").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.content.[].landing.detail_page").description("광고 상세 요청(광고 노출 처리 용도)").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.totalPages").description("현제 페이지 수").type(JsonFieldType.NUMBER)
                                        , fieldWithPath("result.totalElements").description("총 광고 수").type(JsonFieldType.NUMBER)
                                        , fieldWithPath("result.size").description("페이지 당 광고수").type(JsonFieldType.NUMBER)
                                    )
                            )
                    )
//                     swagger
                    .andDo(
                        document("ads-search"
                            , preprocessRequest(prettyPrint())
                            , preprocessResponse(prettyPrint())
                            , resource(
                                    ResourceSnippetParameters.builder()
                                        .tag("퀴즈 광고 요청")
                                        .summary("퀴즈 광고 목록 노출")
                                        .description("진행중 인 퀴즈 광고 목록을 노출 한다.")
                                        .pathParameters(
                                                parameterWithName("group-code").description("파트너 광고 그룹 코드")
                                        )
                                        .queryParameters(
                                                parameterWithName("user-key").description("파트너사 APP USER 식별키"),
                                                parameterWithName("join").description("참여 여부( true : 참여, fasle: 미참여, all: 전체)"),
                                                parameterWithName("page").description("파트너 광고 그룹 코드"),
                                                parameterWithName("size").description("파트너 광고 그룹 코드")
                                        )
                                        .responseSchema(
                                                Schema.schema("QuizAdsRespVo")
                                        )
                                        .responseFields(
                                                fieldWithPath("result.content.[].requestId").description("광고 요청 키").type(JsonFieldType.STRING)
                                                , fieldWithPath("result.content.[].campaignName").description("광고 제목").type(JsonFieldType.STRING)
                                                , fieldWithPath("result.content.[].campaignCode").description("광고 코드").type(JsonFieldType.STRING)
                                                , fieldWithPath("result.content.[].quizTitle").description("퀴즈 문제").type(JsonFieldType.STRING)
                                                , fieldWithPath("result.content.[].pointName").description("포인트 명").type(JsonFieldType.STRING)
                                                , fieldWithPath("result.content.[].reword").description("적립 포인트").type(JsonFieldType.NUMBER)
                                                , fieldWithPath("result.content.[].totalParticipationLimit").description("총 참여 가능 인원").type(JsonFieldType.NUMBER)
                                                , fieldWithPath("result.content.[].dayParticipationLimit").description("일별 참여 가능 인원").type(JsonFieldType.NUMBER)
                                                , fieldWithPath("result.content.[].joined").description("참여 여부").type(JsonFieldType.BOOLEAN)
                                                , fieldWithPath("result.content.[].landing.thumb").description("메인 페이지 이미지(광고 요청 처리 용도)").type(JsonFieldType.STRING)
                                                , fieldWithPath("result.content.[].landing.detail_page").description("광고 상세 요청(광고 노출 처리 용도)").type(JsonFieldType.STRING)
                                                , fieldWithPath("result.totalPages").description("현제 페이지 수").type(JsonFieldType.NUMBER)
                                                , fieldWithPath("result.totalElements").description("총 광고 수").type(JsonFieldType.NUMBER)
                                                , fieldWithPath("result.size").description("페이지 당 광고수").type(JsonFieldType.NUMBER)
                                        )
                                        .build()
                            )
                        )
                    );
//
    }


    @Test
    @DisplayName("광고 상세")
    public void QuizAdsDetails() throws Exception {


        String requestId = "test";
        String adCode = "YxGBfKJBsG";

        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("user-key", "test");


        this.mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/app/v1/ads/detail/{request-id}/{ad-code}", requestId, adCode)
                                .accept(MediaType.APPLICATION_JSON)
                                .queryParams(info)
                                .header("user-agent", "mock-mvc")
                )

                .andDo(print())
                .andExpect(status().isOk())
                // restdoc
                .andDo(
                        MockMvcRestDocumentationWrapper.document("ads-details"
                                , preprocessResponse(prettyPrint())
                                , pathParameters(
                                        parameterWithName("request-id").description("광고 요청 코드"),
                                        parameterWithName("ad-code").description("광고 지면 코드")
                                )
                                ,queryParameters(
                                        parameterWithName("user-key").description("파트너사 APP USER 식별키")
                                )
                                ,responseFields(
                                        fieldWithPath("result.requestId").description("광고 요청 키").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.campaignName").description("광고 제목").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.campaignCode").description("광고 코드").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.quizTitle").description("퀴즈 문제").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.pointName").description("포인트 명").type(JsonFieldType.STRING).optional()
                                        , fieldWithPath("result.reword").description("적립 포인트").type(JsonFieldType.NUMBER).optional()
                                        , fieldWithPath("result.joinUserCount").description("참여 인원").type(JsonFieldType.NUMBER)
                                        , fieldWithPath("result.totalParticipationLimit").description("총 참여 가능 인원").type(JsonFieldType.NUMBER)
                                        , fieldWithPath("result.dayParticipationLimit").description("일별 참여 가능 인원").type(JsonFieldType.NUMBER)
                                        , fieldWithPath("result.joined").description("참여 여부").type(JsonFieldType.BOOLEAN)
                                        , fieldWithPath("result.landing.detail").description("상세 이미지").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.landing.answer").description("정답 호출 Endpoint").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.landing.hint_ad_pc").description("힌트 랜딩 페이지 (PC)").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.landing.hint_ad_mobile").description("힌트 랜딩 페이지 (Mobile)").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.landing.answer_ad_pc").description("정답 랜딩 페이지 (PC)").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.landing.answer_ad_mobile").description("정답 랜딩 페이지 (Mobile)").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.landing.reword").description("리워드 적립 내역 조회 URL").type(JsonFieldType.STRING)
                                )
                        )
                )
//                     swagger
                .andDo(
                        document("ads-details"
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("퀴즈 광고 상세")
                                                .summary("퀴즈 광고 상세 정보")
                                                .description("요청한 퀴즈 광고의 상세 정보를 제공 한다.")
                                                .pathParameters(
                                                        parameterWithName("request-id").description("광고 요청 코드"),
                                                        parameterWithName("ad-code").description("광고 지면 코드")
                                                )
                                                .queryParameters(
                                                        parameterWithName("user-key").description("파트너사 APP USER 식별키")
                                                )
                                                .responseSchema(
                                                        Schema.schema("QuizDetailRespVo")
                                                )
                                                .responseFields(
                                                        fieldWithPath("result.requestId").description("광고 요청 키").type(JsonFieldType.STRING)
                                                        , fieldWithPath("result.campaignName").description("광고 제목").type(JsonFieldType.STRING)
                                                        , fieldWithPath("result.campaignCode").description("광고 코드").type(JsonFieldType.STRING)
                                                        , fieldWithPath("result.quizTitle").description("퀴즈 문제").type(JsonFieldType.STRING)
                                                        , fieldWithPath("result.pointName").description("포인트 명").type(JsonFieldType.STRING).optional()
                                                        , fieldWithPath("result.reword").description("적립 포인트").type(JsonFieldType.NUMBER).optional()
                                                        , fieldWithPath("result.joinUserCount").description("참여 인원").type(JsonFieldType.NUMBER)
                                                        , fieldWithPath("result.totalParticipationLimit").description("총 참여 가능 인원").type(JsonFieldType.NUMBER)
                                                        , fieldWithPath("result.dayParticipationLimit").description("일별 참여 가능 인원").type(JsonFieldType.NUMBER)
                                                        , fieldWithPath("result.joined").description("참여 여부").type(JsonFieldType.BOOLEAN)
                                                        , fieldWithPath("result.landing.detail").description("상세 이미지").type(JsonFieldType.STRING)
                                                        , fieldWithPath("result.landing.answer").description("정답 호출 Endpoint").type(JsonFieldType.STRING)
                                                        , fieldWithPath("result.landing.hint_ad_pc").description("힌트 랜딩 페이지 (PC)").type(JsonFieldType.STRING)
                                                        , fieldWithPath("result.landing.hint_ad_mobile").description("힌트 랜딩 페이지 (Mobile)").type(JsonFieldType.STRING)
                                                        , fieldWithPath("result.landing.answer_ad_pc").description("정답 랜딩 페이지 (PC)").type(JsonFieldType.STRING)
                                                        , fieldWithPath("result.landing.answer_ad_mobile").description("정답 랜딩 페이지 (Mobile)").type(JsonFieldType.STRING)
                                                        , fieldWithPath("result.landing.reword").description("리워드 적립 내역 조회 URL").type(JsonFieldType.STRING)
                                                )
                                                .build()
                                )
                        )
                );
//
    }

    @Test
    @DisplayName("광고 정답")
    public void QuizAdsAnswer() throws Exception {


        String requestId = "test";
        String adCode = "YxGBfKJBsG";

        String userKey = RandomStringUtils.randomAlphabetic(10);
        String answer = "정답";


        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("user-key", userKey);
        info.add("answer", answer);

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/app/v1/ads/answer/{request-id}/{ad-code}", requestId, adCode)
                                .accept(MediaType.APPLICATION_JSON)
                                .queryParams(info)
                                .header("user-agent", "mock-mvc")
                )

                .andDo(print())
                .andExpect(status().isOk())
                // restdoc
                .andDo(
                        MockMvcRestDocumentationWrapper.document("ads-answer"
                                , preprocessResponse(prettyPrint())
                                , pathParameters(
                                        parameterWithName("request-id").description("광고 요청 코드"),
                                        parameterWithName("ad-code").description("광고 지면 코드")
                                )
                                ,queryParameters(
                                        parameterWithName("user-key").description("파트너사 APP USER 식별키"),
                                        parameterWithName("answer").description("정답")
                                )
                                ,responseFields(
                                        fieldWithPath("result.message").description("정답 결과").type(JsonFieldType.STRING),
                                        fieldWithPath("result.reword").description("적립 포인트").type(JsonFieldType.NUMBER).optional()
                                )
                        )
                )
//                     swagger
                .andDo(
                        document("ads-answer"
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("퀴즈 광고 상세")
                                                .summary("퀴즈 광고 상세 정보")
                                                .description("요청한 퀴즈 광고의 상세 정보를 제공 한다.")
                                                .pathParameters(
                                                        parameterWithName("request-id").description("광고 요청 코드"),
                                                        parameterWithName("ad-code").description("광고 지면 코드")
                                                )
                                                .queryParameters(
                                                        parameterWithName("user-key").description("파트너사 APP USER 식별키"),
                                                        parameterWithName("answer").description("정답")
                                                )
                                                .responseSchema(
                                                        Schema.schema("QuizAnswerRespVo")
                                                )
                                                .responseFields(
                                                        fieldWithPath("result.message").description("정답 결과").type(JsonFieldType.STRING),
                                                        fieldWithPath("result.reword").description("적립 포인트").type(JsonFieldType.NUMBER).optional()
                                                )
                                                .build()
                                )
                        )
                );
//
    }
}
