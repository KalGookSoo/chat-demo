package kr.me.seesaw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles({"test"})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = HomeController.class)
class HomeControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("\"/\"로 요청 시 스웨거 인덱스 뷰로 리다이렉트한다.")
    void homeShouldReturnIs3xxRedirection() throws Exception {
        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/swagger-ui/index.html"));
    }

}