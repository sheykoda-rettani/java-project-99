package hexlet.code.app;

import hexlet.code.app.dto.LoginRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerTests {
    /**
     * Мок-сервер.
     */
    @Autowired
    protected MockMvc mockMvc;

    /**
     * Для преобразования в и из json.
     */
    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    @SqlGroup({
            @Sql(scripts = "/sql/init-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    })
    @SuppressWarnings("checkstyle:indentation")
    void setUp() {
    }

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
