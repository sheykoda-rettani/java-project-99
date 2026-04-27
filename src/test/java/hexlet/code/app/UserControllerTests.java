package hexlet.code.app;

import hexlet.code.app.dto.UserRequestDto;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
final class UserControllerTests {
    /**
     * Мок-сервер.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Для преобразования в и из json.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Репозиторий с пользователями.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Контекст приложения.
     */
    @Autowired
    private WebApplicationContext wac;

    /**
     * Токен для эмуляции.
     */
    private JwtRequestPostProcessor token;

    /**
     * Пользователь для тестов.
     */
    private User testUser;

    @SuppressWarnings("checkstyle:indentation")
    @BeforeEach
    @SqlGroup({
            @Sql(scripts = "/sql/init-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    })
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8).
                apply(springSecurity()).build();
        testUser = userRepository.findAll().getFirst();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void testCreate() throws Exception {
        UserRequestDto newUserDto = new UserRequestDto();
        newUserDto.setEmail("newuser@mail.com");
        newUserDto.setPassword("secret");
        newUserDto.setFirstName("Иван");
        newUserDto.setLastName("Иванов");

        var request = post("/api/users").with(token).contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(newUserDto));
        mockMvc.perform(request).andExpect(status().isCreated());

        var user = userRepository.findByEmail(newUserDto.getEmail()).orElse(null);
        assertNotNull(user);
        assertThat(user.getEmail()).isEqualTo("newuser@mail.com");
        assertThat(user.getFirstName()).isEqualTo("Иван");
        assertThat(user.getLastName()).isEqualTo("Иванов");
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/users").with(token)).
                andExpect(status().isOk()).andReturn().getResponse();
        var body = response.getContentAsString();

        List<User> actual = objectMapper.readValue(body, new TypeReference<>() {
        });

        var expected = userRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/users/" + testUser.getId()).with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        User actual = objectMapper.readValue(body, User.class);
        assertThat(actual).isEqualTo(testUser);
    }
}
