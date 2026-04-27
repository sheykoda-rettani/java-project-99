package hexlet.code.app;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
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
public final class TaskStatusControllerTests {
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
     * Контекст приложения.
     */
    @Autowired
    private WebApplicationContext wac;

    /**
     * Репозиторий с пользователями.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Репозиторий со статусами.
     */
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    /**
     * Токен для эмуляции.
     */
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @SuppressWarnings("checkstyle:indentation")
    @BeforeEach
    @SqlGroup({
            @Sql(scripts = "/sql/init-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/sql/init-statuses.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    })
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8).
                apply(springSecurity()).build();
        var testUser = userRepository.findAll().getFirst();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void testCreate() throws Exception {
        TaskStatus newStatusDto = new TaskStatus();
        newStatusDto.setName("В архиве");
        newStatusDto.setSlug("archived");

        var request = post("/api/task_statuses").
                with(token).
                contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(newStatusDto));

        mockMvc.perform(request).andExpect(status().isCreated());

        var status = taskStatusRepository.findBySlug("archived").orElse(null);
        assertNotNull(status);
        assertThat(status.getName()).isEqualTo("В архиве");
        assertThat(status.getSlug()).isEqualTo("archived");
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/task_statuses").with(token)).
                andExpect(status().isOk()).
                andReturn().getResponse();

        var body = response.getContentAsString();
        List<TaskStatus> actual = objectMapper.readValue(body, new TypeReference<>() { });

        var expected = taskStatusRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var testStatus = taskStatusRepository.findAll().getFirst();
        var request = get("/api/task_statuses/" + testStatus.getId()).with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        var body = result.getResponse().getContentAsString();
        TaskStatus actual = objectMapper.readValue(body, TaskStatus.class);
        assertThat(actual).isEqualTo(testStatus);
    }

    @Test
    public void testDuplicateProducesError() throws Exception {
        var existing = taskStatusRepository.findAll().getFirst();
        TaskStatus duplicateStatus = new TaskStatus();
        duplicateStatus.setName("Попытка дубля");
        duplicateStatus.setSlug(existing.getSlug());

        var request = post("/api/task_statuses").
                with(token).
                contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(duplicateStatus));

        mockMvc.perform(request).andExpect(status().is4xxClientError());
    }
}
