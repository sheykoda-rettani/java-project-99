package hexlet.code.app;

import hexlet.code.app.dto.UserRequestDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import tools.jackson.core.type.TypeReference;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public final class UserControllerTests extends AbstractWebIntegrationTest {
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
    @Override
    @SuppressWarnings("checkstyle:indentation")
    void setUp() {
        super.setUp();
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
    public void testUpdate() throws Exception {
        final String lastName = "Тестов";
        UserRequestDto updateUserDto = new UserRequestDto();
        updateUserDto.setLastName(lastName);

        var request = put("/api/users/" + testUser.getId()).
                with(token).contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(updateUserDto));
        mockMvc.perform(request).andExpect(status().isOk());

        User toCheck = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(toCheck);
        assertThat(toCheck.getLastName()).isEqualTo(lastName);
    }

    @Test
    public void testUpdateValidationFailsWithWrongData() throws Exception {
        final String lastName = "А";
        final int expectedCode = 400;
        final int expectedSize = 1;
        UserRequestDto updateUserDto = new UserRequestDto();
        updateUserDto.setLastName(lastName);

        var request = put("/api/users/" + testUser.getId()).
                with(token).contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(updateUserDto));

        mockMvc.perform(request).
                andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.code").value(expectedCode)).
                andExpect(jsonPath("$.details", hasSize(expectedSize)));
    }

    @Test
    public void testRequestValidationFailsWithWrongData() throws Exception {
        final int expectedCode = 400;
        final int expectedSize = 2;
        UserRequestDto newUserDto = new UserRequestDto();
        var request = post("/api/users").with(token).contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(newUserDto));

        mockMvc.perform(request).
                andExpect(status().isBadRequest()).
                andExpect(jsonPath("$.code").value(expectedCode)).
                andExpect(jsonPath("$.details", hasSize(expectedSize)));
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

    @Test
    public void testCantDeleteUserWithTask() throws Exception {
        User toDelete = new User();
        toDelete.setEmail("delete@mail.com");
        toDelete.setPassword("superSecret");
        toDelete.setFirstName("Удаль");
        toDelete.setLastName("Удали");
        userRepository.save(toDelete);
        List<TaskStatus> statuses = taskStatusRepository.findAll();
        Task task1 = new Task();
        task1.setName("Задача 1");
        task1.setDescription("Описание 1");
        task1.setTaskStatus(statuses.getFirst());
        task1.setAssignee(toDelete);
        taskRepository.save(task1);

        var request = delete("/api/users/" + toDelete.getId()).with(token);
        mockMvc.perform(request).andExpect(status().is4xxClientError());
        assertThat(userRepository.findById(toDelete.getId())).isPresent();
    }
}
