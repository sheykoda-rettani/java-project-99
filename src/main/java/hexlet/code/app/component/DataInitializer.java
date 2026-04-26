package hexlet.code.app.component;

import hexlet.code.app.dto.UserRequestDto;
import hexlet.code.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public final class DataInitializer implements ApplicationRunner {
    /**
     * Сервис управления пользователями.
     */
    private final UserService userService;

    @Override
    public void run(final ApplicationArguments args) {
        final String adminEmail = "hexlet@example.com";
        final String adminPassword = "qwerty";

        if (userService.findByEmail(adminEmail).isEmpty()) {
            UserRequestDto adminDto = new UserRequestDto();
            adminDto.setEmail(adminEmail);
            adminDto.setPassword(adminPassword);
            adminDto.setFirstName("Hexlet");
            adminDto.setLastName("Admin");

            userService.create(adminDto);
            log.info("Администратор c email '{}' успешно создан.", adminEmail);
        } else {
            log.info("Администратор уже существует.");
        }
    }
}
