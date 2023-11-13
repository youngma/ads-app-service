package com.ads.main;

import com.ads.main.vo.inquiry.req.AdInquiryReqVo;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class}) // When using JUnit5
@SpringBootTest(properties = { "test" })
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class AdsAppApiTest {

    private MockMvc mockMvc;


    String groupCode = "brBJZBMxDe";
    String adCode = "YxGBfKJBsG";

    String requestId = "test-request";
    String userKey = "test-user";

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
        info.add("user-key", userKey);
        info.add("join", "all");
        info.add("page", "1");
        info.add("size", "10");


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


        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("user-key", userKey);

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
                                        parameterWithName("ad-code").description("광고 코드")
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
                                                .tag("퀴즈 광고 정답 확인")
                                                .summary("퀴즈 광고 정답 확인")
                                                .description("요청한 퀴즈 광고의 정답 확인 후 리워드 금액을 반환한다.")
                                                .pathParameters(
                                                        parameterWithName("request-id").description("광고 요청 코드"),
                                                        parameterWithName("ad-code").description("광고 코드")
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


    @Test
    @DisplayName("광고 적립금 내역")
    public void QuizAdsReword() throws Exception {

        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("page", "1");
        info.add("size", "10");

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/app/v1/ads/reword/{group-code}/{ad-code}", groupCode, adCode)
                                .accept(MediaType.APPLICATION_JSON)
                                .queryParams(info)
                                .header("user-agent", "mock-mvc")
                )

                .andDo(print())
                .andExpect(status().isOk())
                // restdoc
                .andDo(
                        MockMvcRestDocumentationWrapper.document("ads-reword"
                                , preprocessResponse(prettyPrint())
                                , pathParameters(
                                        parameterWithName("group-code").description("지면 코드"),
                                        parameterWithName("ad-code").description("광고 코드")
                                )
                                ,queryParameters(
                                        parameterWithName("page").description("페이지"),
                                        parameterWithName("size").description("페이지당 광고 ")
                                )
                                ,responseFields(
                                        fieldWithPath("result.content.[].user").description("정답 결과").type(JsonFieldType.STRING),
                                        fieldWithPath("result.content.[].reword").description("적립 포인트").type(JsonFieldType.NUMBER),
                                        fieldWithPath("result.totalPages").description("현제 페이지 수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("result.totalElements").description("총 광고 수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("result.size").description("페이지 당 광고수").type(JsonFieldType.NUMBER)
                                )
                        )
                )
//                     swagger
                .andDo(
                        document("ads-reword"
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("퀴즈 광고 리워드 내역")
                                                .summary("퀴즈 광고 리워드 내역")
                                                .description("요청한 퀴즈 광고 적립 유저의 현황을 제공 한다.")
                                                .pathParameters(
                                                        parameterWithName("group-code").description("지면 코드"),
                                                        parameterWithName("ad-code").description("광고 코드")
                                                )
                                                .queryParameters(
                                                        parameterWithName("page").description("페이지"),
                                                        parameterWithName("size").description("페이지당 광고 ")
                                                )
                                                .responseSchema(
                                                        Schema.schema("QuizRespRespVo")
                                                )
                                                .responseFields(
                                                        fieldWithPath("result.content.[].user").description("정답 결과").type(JsonFieldType.STRING),
                                                        fieldWithPath("result.content.[].reword").description("적립 포인트").type(JsonFieldType.NUMBER),
                                                        fieldWithPath("result.totalPages").description("현제 페이지 수").type(JsonFieldType.NUMBER),
                                                        fieldWithPath("result.totalElements").description("총 광고 수").type(JsonFieldType.NUMBER),
                                                        fieldWithPath("result.size").description("페이지 당 광고수").type(JsonFieldType.NUMBER)
                                                )
                                                .build()
                                )
                        )
                );
//
    }


    @Test
    @DisplayName("광고 문의 하기 ( 리스팅 )")
    public void QuizAdsInquiryByListing() throws Exception {


        ObjectMapper objectMapper = new ObjectMapper();

        AdInquiryReqVo adInquiryReqVo = new AdInquiryReqVo();
        adInquiryReqVo.setQuizTitle("Quiz-title");
        adInquiryReqVo.setTitle("Title");
        adInquiryReqVo.setUser("userKey");
        adInquiryReqVo.setPhone("010-0000-0000");


        this.mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/app/v1/ads/inquiry/{ad-group}", groupCode)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(adInquiryReqVo))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("user-agent", "mock-mvc")
                )

                .andDo(print())
                .andExpect(status().isOk())
                // restdoc
                .andDo(
                        MockMvcRestDocumentationWrapper.document("ads-inquiry-listing"
                                , preprocessResponse(prettyPrint())
                                , pathParameters(
                                        parameterWithName("ad-group").description("광고 지면 코드")
                                )
                                , requestFields (
                                    fieldWithPath("quizTitle").description("퀴즈 제목"),
                                    fieldWithPath("title").description("문의하기 내용"),
                                    fieldWithPath("user").description("파트너사 APP USER 식별키"),
                                    fieldWithPath("phone").description("전화번호")
                                )
                                ,responseFields(
                                        fieldWithPath("message").description("정답 결과").type(JsonFieldType.STRING)
                                )
                        )
                )
//                     swagger
                .andDo(
                        document("ads-inquiry-listing"
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("퀴즈 광고 문의 하기(리스팅)")
                                                .summary("퀴즈 광고 문의 하기(리스팅)")
                                                .description("퀴즈 광고 문의 하기(리스팅)")
                                                .pathParameters(
                                                        parameterWithName("ad-group").description("광고 지면 코드")
                                                )
                                                .requestFields (
                                                        fieldWithPath("quizTitle").description("퀴즈 제목"),
                                                        fieldWithPath("title").description("문의하기 내용"),
                                                        fieldWithPath("user").description("파트너사 APP USER 식별키"),
                                                        fieldWithPath("phone").description("전화번호")
                                                )
                                                .responseSchema(
                                                        Schema.schema("QuizAnswerRespVo")
                                                )
                                                .responseFields(
                                                        fieldWithPath("message").description("문의 사항 처리 결과").type(JsonFieldType.STRING)
                                                )
                                                .build()
                                )
                        )
                );
//
    }

    @Test
    @DisplayName("광고 문의 하기 ( 상세 )")
    public void QuizAdsInquiryByDetail() throws Exception {


        ObjectMapper objectMapper = new ObjectMapper();

        AdInquiryReqVo adInquiryReqVo = new AdInquiryReqVo();
        adInquiryReqVo.setQuizTitle("Quiz-title");
        adInquiryReqVo.setTitle("Title");
        adInquiryReqVo.setUser("userKey");
        adInquiryReqVo.setPhone("010-0000-0000");


        this.mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/app/v1/ads/inquiry/{ad-group}/{ad-code}", groupCode, adCode)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(adInquiryReqVo))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("user-agent", "mock-mvc")
                )

                .andDo(print())
                .andExpect(status().isOk())
                // restdoc
                .andDo(
                        MockMvcRestDocumentationWrapper.document("ads-inquiry-detail"
                                , preprocessResponse(prettyPrint())
                                , pathParameters(
                                        parameterWithName("ad-group").description("광고 지면 코드"),
                                        parameterWithName("ad-code").description("광고 코드")
                                )
                                , requestFields (
                                        fieldWithPath("quizTitle").description("퀴즈 제목"),
                                        fieldWithPath("title").description("문의하기 내용"),
                                        fieldWithPath("user").description("파트너사 APP USER 식별키"),
                                        fieldWithPath("phone").description("전화번호")

                                )
                                ,responseFields(
                                        fieldWithPath("message").description("정답 결과").type(JsonFieldType.STRING)
                                )
                        )
                )
//                     swagger
                .andDo(
                        document("ads-inquiry-detail"
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("퀴즈 광고 문의 하기(리스팅)")
                                                .summary("퀴즈 광고 문의 하기(리스팅)")
                                                .description("퀴즈 광고 문의 하기(리스팅)")
                                                .pathParameters(
                                                        parameterWithName("ad-group").description("광고 지면 코드"),
                                                        parameterWithName("ad-code").description("광고 코드")
                                                )
                                                .requestFields (
                                                        fieldWithPath("quizTitle").description("퀴즈 제목"),
                                                        fieldWithPath("title").description("문의하기 내용"),
                                                        fieldWithPath("user").description("파트너사 APP USER 식별키"),
                                                        fieldWithPath("phone").description("전화번호")
                                                )
                                                .responseSchema(
                                                        Schema.schema("QuizAnswerRespVo")
                                                )
                                                .responseFields(
                                                        fieldWithPath("message").description("문의 사항 처리 결과").type(JsonFieldType.STRING)
                                                )
                                                .build()
                                )
                        )
                );
//
    }
}
