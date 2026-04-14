package hexlet.code.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
final class AppApplicationTests {

    /**
     * Мок-сервер.
     */
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testWelcomePage() throws Exception {
        mockMvc.perform(get("/welcome")).
                andExpect(status().isOk()).
                andExpect(content().string("Welcome to Spring"));
    }
}
