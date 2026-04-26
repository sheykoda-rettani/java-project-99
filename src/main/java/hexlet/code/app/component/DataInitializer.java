package hexlet.code.app.component;

import hexlet.code.app.dto.UserRequestDto;
import hexlet.code.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
