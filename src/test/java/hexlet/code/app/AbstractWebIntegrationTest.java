package hexlet.code.app;

import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractWebIntegrationTest {
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

    /**
     * Контекст приложения.
     */
    @Autowired
    protected WebApplicationContext wac;

    /**
     * Токен для эмуляции.
     */
    protected SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    /**
     * Репозиторий с пользователями.
     */
    @Autowired
    protected UserRepository userRepository;

    /**
     * Начальная инициализация общая для всех контроллеров. В дочерних - не забывать включать его в BeforeEach
     */
    @BeforeEach
    @SqlGroup({
            @Sql(scripts = "/sql/init-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    })
    @SuppressWarnings("checkstyle:indentation")
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8).
                apply(springSecurity()).build();
        var testUser = userRepository.findAll().getFirst();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }
}
