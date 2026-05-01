package hexlet.code.app;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
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
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class LabelControllerTests extends AbstractWebIntegrationTest {
    /**
     * Репозиторий меток.
     */
    @Autowired
    private LabelRepository labelRepository;

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

    @BeforeEach
    @SqlGroup({
            @Sql(scripts = "/sql/init-statuses.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/sql/init-labels.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    })
    @Override
    @SuppressWarnings("checkstyle:indentation")
    void setUp() {
        super.setUp();
    }

    @Test
    public void testCreate() throws Exception {
        final String labelName = "Тестовый ярлык";
        Label label = new Label(labelName);

        var request = post("/api/labels").
                with(token).
                contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(label));

        mockMvc.perform(request).andExpect(status().isCreated());
        assertThat(labelRepository.findByName(labelName)).isPresent();
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/labels").with(token)).
                andExpect(status().isOk()).
                andReturn().getResponse();

        var body = response.getContentAsString();
        List<Label> actual = objectMapper.readValue(body, new TypeReference<>() { });
        var expected = labelRepository.findAll();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        Label label = labelRepository.findAll().getFirst();
        var response = mockMvc.perform(get("/api/labels/" + label.getId()).with(token)).
                andExpect(status().isOk()).
                andReturn().getResponse();

        var body = response.getContentAsString();
        Label actual = objectMapper.readValue(body, Label.class);
        assertThat(actual).isEqualTo(label);
    }

    @Test
    public void testUpdate() throws Exception {
        final String expectedLabel = "updated_label";
        Label label = labelRepository.findAll().getFirst();
        label.setName("updated_label");

        var request = put("/api/labels/" + label.getId()).
                with(token).
                contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(label));
        mockMvc.perform(request).andExpect(status().isOk());

        assertThat(labelRepository.findByName(expectedLabel)).isPresent();
    }

    @Test
    public void testCantDeleteLabelWithTask() throws Exception {
        initDb();
        Label label = labelRepository.findAll().getFirst();
        Task task = taskRepository.findAll().getFirst();
        task.setLabels(Set.of(label));
        taskRepository.save(task);
        var request = delete("/api/labels/" + label.getId()).with(token);
        mockMvc.perform(request).andExpect(status().isBadRequest());
        assertThat(labelRepository.findById(label.getId())).isPresent();
    }

    private void initDb() {
        taskRepository.deleteAll();
        final int index1 = 1;
        User assignee = userRepository.findAll().getFirst();
        List<TaskStatus> statuses = taskStatusRepository.findAll();

        Task task1 = new Task();
        task1.setName("Задача 1");
        task1.setIndex(index1);
        task1.setDescription("Описание 1");
        task1.setAssignee(assignee);
        task1.setTaskStatus(statuses.getFirst());
        taskRepository.save(task1);
    }
}
