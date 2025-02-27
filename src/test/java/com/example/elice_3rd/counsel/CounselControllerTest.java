package com.example.elice_3rd.counsel;

import com.example.elice_3rd.counsel.dto.CounselRequestDto;
import com.example.elice_3rd.counsel.service.CounselService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@Transactional
public class CounselControllerTest {
    private static final Logger log = LoggerFactory.getLogger(CounselControllerTest.class);
    @Autowired
    private CounselService counselService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // @Test
    // void create() throws Exception {
    //     //given
    //     String email = "qq@q11q";
    //     CounselRequestDto requestDto = CounselRequestDto.builder()
    //             .title("test title")
    //             .content("test content")
    //             .build();

    //     counselService.create(email, requestDto);
    // }

    // @Test
    // void deleteCounsel() throws Exception {
    //     //given
    //     ResultActions resultActions = mockMvc.perform(delete(URI.create("api/v1/counsels"))
    //             .principal(() -> "qq@qq")
    //             .param("id", "5"));

    //     resultActions.andExpect(result -> {
    //         log.info(result.getResponse().getHeader("Location"));
    //     });
    // }
}
