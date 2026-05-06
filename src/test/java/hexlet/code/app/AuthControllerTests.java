package hexlet.code.app;

import hexlet.code.app.dto.LoginRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTests extends AbstractWebIntegrationTest {
    @Test
    void testLoginSuccess() throws Exception {
        final int jwtPartsCount = 3;
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("hexlet@example.com");
        loginRequest.setPassword("qwerty");

        var result = mockMvc.perform(
                        post("/api/login").contentType(MediaType.APPLICATION_JSON).
                                content(objectMapper.writeValueAsString(loginRequest))
                ).andExpect(status().isOk()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        assertThat(responseBody).isNotBlank();

        String[] parts = responseBody.split("\\.");
        assertThat(parts).hasSize(jwtPartsCount);
    }
}
