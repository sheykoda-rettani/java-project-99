package hexlet.code.app;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import tools.jackson.core.type.TypeReference;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class TaskControllerTests extends AbstractWebIntegrationTest {
    /**
     * Репозиторий задач.
     */
    @Autowired
    private TaskRepository taskRepository;

    /**
     * Репозиторий со статусами.
     */
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    /**
     * Преобразование между DTO и сущностями задач.
     */
    @Autowired
    private TaskMapper mapper;

    @BeforeEach
    @SqlGroup({
            @Sql(scripts = "/sql/init-statuses.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/sql/init-labels.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    })
    @Override
    @SuppressWarnings("checkstyle:indentation")
    void setUp() {
        super.setUp();
        initDb();
    }

    @Test
    public void testCreate() throws Exception {
        final int index = 10;
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Тест названия");
        taskDto.setIndex(index);
        taskDto.setContent("Описание");
        TaskStatus status = taskStatusRepository.findAll().getFirst();
        taskDto.setStatus(status.getSlug());
        User assignee = userRepository.findAll().getFirst();
        taskDto.setAssigneeId(assignee.getId());
        var request = post("/api/tasks").
                with(token).
                contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(taskDto));

        mockMvc.perform(request).andExpect(status().isCreated());

        Task task = taskRepository.findByIndex(index).orElse(null);
        assertNotNull(task);
        assertThat(task.getIndex()).isEqualTo(index);
        assertThat(task.getTaskStatus()).isEqualTo(status);
        assertThat(task.getAssignee()).isEqualTo(assignee);
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/tasks").with(token)).
                andExpect(status().isOk()).
                andReturn().getResponse();

        var body = response.getContentAsString();
        List<TaskDto> actualDtos = objectMapper.readValue(body, new TypeReference<>() { });
        List<Task> actual = actualDtos.stream().map(mapper::toTaskEntity).toList();
        var expected = taskRepository.findAll();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var task = taskRepository.findAll().getFirst();
        var request = get("/api/tasks/" + task.getId()).with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        var body = result.getResponse().getContentAsString();
        TaskDto dto = objectMapper.readValue(body, TaskDto.class);
        Task actual = mapper.toTaskEntity(dto);
        assertThat(actual).isEqualTo(task);
    }

    @Test
    public void testFilterWorks() throws Exception {
        String statusSlug = taskStatusRepository.findAll().getLast().getSlug();
        String filter = "?titleCont=Зад&status=" + statusSlug;
        final int expectedSize = 1;
        var request = get("/api/tasks" + filter).with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        List<TaskDto> actual = objectMapper.readValue(body, new TypeReference<>() { });
        assertThat(actual.size()).isEqualTo(expectedSize);
        TaskDto dto = actual.getFirst();
        assertThat(dto.getStatus()).isEqualTo(statusSlug);
    }

    private void initDb() {
        taskRepository.deleteAll();
        final int index1 = 1;
        final int index2 = 2;
        User assignee = userRepository.findAll().getFirst();
        List<TaskStatus> statuses = taskStatusRepository.findAll();

        Task task1 = new Task();
        task1.setName("Задача 1");
        task1.setIndex(index1);
        task1.setDescription("Описание 1");
        task1.setAssignee(assignee);
        task1.setTaskStatus(statuses.getFirst());
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setName("Задача 2");
        task2.setIndex(index2);
        task2.setDescription("Описание 2");
        task2.setTaskStatus(statuses.getLast());
        taskRepository.save(task2);
    }
}
