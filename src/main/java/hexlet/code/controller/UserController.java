package hexlet.code.controller;

import hexlet.code.dto.UserRequestDto;
import hexlet.code.model.User;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    /**
     * Сервис операций с пользователями.
     */
    private final UserService userService;

    /**
     * Получение списка всех пользователей.
     * @return список пользователей
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getAllUsers() {
        var users = userService.findAll();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(users.size())).body(users);
    }

    /**
     * Получение одного пользователя по его id.
     * @param id id пользователя для получения
     * @return найденный пользователь
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUserById(@PathVariable final Long id) {
        return userService.findByIdOrThrow(id);
    }

    /**
     * Создание нового пользователя.
     * @param user параметры запроса на создание пользователя
     * @return данные по созданному пользователю
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody @Valid final UserRequestDto user) {
        return userService.create(user);
    }

    /**
     * Обновление данных пользователя.
     * @param id id пользователя для обновления
     * @param userDetails данные пользователя, которые надо обновить
     * @return полностью обновленный пользователь
     */
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public User updateUser(@PathVariable final Long id, @RequestBody final UserRequestDto userDetails) {
        return userService.update(id, userDetails);
    }

    /**
     * Удаление пользователя.
     * @param id id пользователя для удаления
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@userServiceImpl.isCurrentUser(#id)")
    public void deleteUser(@PathVariable final Long id) {
        userService.deleteById(id);
    }
}
