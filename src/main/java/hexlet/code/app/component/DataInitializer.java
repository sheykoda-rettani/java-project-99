package hexlet.code.app.component;

import hexlet.code.app.dto.UserRequestDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public final class DataInitializer implements ApplicationRunner {
    /**
     * Сервис управления пользователями.
     */
    private final UserService userService;

    /**
     * Репозиторий статусов.
     */
    private final TaskStatusRepository taskStatusRepository;

    /**
     * Репозиторий меток.
     */
    private final LabelRepository labelRepository;

    /**
     * Имя/email администратора.
     */
    @Value("${admin.email:hexlet@example.com}")
    private String adminEmail;

    /**
     * Пароль администратора.
     */
    @Value("${admin.password:qwerty}")
    private String adminPassword;

    @Override
    public void run(final ApplicationArguments args) {
        initAdmin();
        initStatuses();
        initLabels();
    }

    private void initAdmin() {
        if (userService.findByEmail(adminEmail).isEmpty()) {
            UserRequestDto adminDto = new UserRequestDto();
            adminDto.setEmail(adminEmail);
            adminDto.setPassword(adminPassword);
            adminDto.setFirstName("Hexlet");
            adminDto.setLastName("Admin");

            userService.create(adminDto);
            log.info("Administrator with email '{}' created successfully.", adminEmail);
        } else {
            log.info("Administrator with email '{}' already exists.", adminEmail);
        }
    }

    private void initStatuses() {
        final List<String[]> defaultStatuses = List.of(
                new String[]{"Черновик", "draft"},
                new String[]{"На ревью", "to_review"},
                new String[]{"На исправление", "to_be_fixed"},
                new String[]{"К публикации", "to_publish"},
                new String[]{"Опубликовано", "published"}
        );

        for (String[] statusData : defaultStatuses) {
            String name = statusData[0];
            String slug = statusData[1];

            if (taskStatusRepository.findBySlug(slug).isEmpty()) {
                TaskStatus newStatus = new TaskStatus();
                newStatus.setName(name);
                newStatus.setSlug(slug);
                taskStatusRepository.save(newStatus);

                log.info("Status '{}' with slug '{}' created successfully.", name, slug);
            } else {
                log.info("Status '{}' with slug '{}' already exists.", name, slug);
            }
        }
    }

    private void initLabels() {
        final List<String> labelNames = List.of("feature", "bug");

        for (String name : labelNames) {
            if (labelRepository.findByName(name).isEmpty()) {
                labelRepository.save(new Label(name));
                log.info("Label '{}' created.", name);
            } else {
                log.info("Label '{}' already exists.", name);
            }
        }
    }
}
