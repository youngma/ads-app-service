package com.ads.main;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
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
@SpringBootTest(properties = { "spring.profiles.active", "local=" })
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
        info.add("page", "1");
        info.add("size", "10");

            String groupCode = "FMqJafxBzL";
            this.mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/v1/ads/search/{group-code}", groupCode)
                    .queryParams(info)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    // restdoc
                    .andDo(
                            MockMvcRestDocumentationWrapper.document("ads-search"
                                    , preprocessResponse(prettyPrint())
                                    , pathParameters(
                                        parameterWithName("group-code").description("파트너 광고 그룹 코드")
                                    )
                                    ,queryParameters(
                                            parameterWithName("page").description("페이지"),
                                            parameterWithName("size").description("페이지당 광고 ")
                                    )
                                    ,responseFields(
                                        fieldWithPath("result.content.[].requestId").description("광고 요청 키").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.content.[].campaignName").description("광고 제목").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.content.[].campaignCode").description("광고 코드").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.content.[].quizTitle").description("퀴즈 문제").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.content.[].totalParticipationLimit").description("총 참여 가능 인원").type(JsonFieldType.NUMBER)
                                        , fieldWithPath("result.content.[].dayParticipationLimit").description("일별 참여 가능 인원").type(JsonFieldType.NUMBER)
                                        , fieldWithPath("result.content.[].landing.thumb").description("광고 진행 여부").type(JsonFieldType.STRING)
                                        , fieldWithPath("result.content.[].landing.detail_page").description("광고 진행 여부").type(JsonFieldType.STRING)
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
                                                , fieldWithPath("result.content.[].totalParticipationLimit").description("총 참여 가능 인원").type(JsonFieldType.NUMBER)
                                                , fieldWithPath("result.content.[].dayParticipationLimit").description("일별 참여 가능 인원").type(JsonFieldType.NUMBER)
                                                , fieldWithPath("result.content.[].landing.thumb").description("광고 진행 여부").type(JsonFieldType.STRING)
                                                , fieldWithPath("result.content.[].landing.detail_page").description("광고 진행 여부").type(JsonFieldType.STRING)
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

}