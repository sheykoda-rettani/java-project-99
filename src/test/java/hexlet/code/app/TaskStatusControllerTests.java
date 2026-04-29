package hexlet.code.app;

import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import tools.jackson.core.type.TypeReference;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public final class TaskStatusControllerTests extends AbstractWebIntegrationTest {
    /**
     * Репозиторий со статусами.
     */
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    /**
     * Репозиторий задач.
     */
    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    @SqlGroup({
            @Sql(scripts = "/sql/init-statuses.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    })
    @Override
    @SuppressWarnings("checkstyle:indentation")
    void setUp() {
        super.setUp();
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

    @Test
    public void testCantDeleteStatusWithTask() throws Exception {
        addTask();
        TaskStatus toDelete = taskStatusRepository.findAll().getFirst();

        var request = delete("/api/task_statuses/" + toDelete.getId()).with(token);
        mockMvc.perform(request).andExpect(status().is4xxClientError());
        assertThat(taskStatusRepository.findById(toDelete.getId())).isPresent();
    }

    private void addTask() {
        List<TaskStatus> statuses = taskStatusRepository.findAll();
        Task task1 = new Task();
        task1.setName("Задача 1");
        task1.setDescription("Описание 1");
        task1.setTaskStatus(statuses.getFirst());
        taskRepository.save(task1);
    }
}
