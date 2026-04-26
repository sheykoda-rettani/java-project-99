package hexlet.code.app.service;

import hexlet.code.app.converter.UserConverter;
import hexlet.code.app.dto.UserRequestDto;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class UserService {

    /**
     * Репозиторий с пользователями.
     */
    private final UserRepository userRepository;
    /**
     * Преобразование в ДТО и обратно.
     */
    private final UserConverter userConverter;
    /**
     * Для валидации частичных данных.
     */
    private final Validator validator;

    /**
     * Кодирование пароля.
     */
    private final PasswordEncoder passwordEncoder;

    public User create(final UserRequestDto userDto) {
        User user = userConverter.fromDto(userDto);
        user.setPassword(encodePassword(user.getPassword()));
        return userRepository.save(user);
    }

    public User update(final Long id, final UserRequestDto userDto) {
        User user = findById(id);
        User userDetails = userConverter.fromDto(userDto);

        if (userDetails.getFirstName() != null) {
            user.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            user.setLastName(userDetails.getLastName());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null) {
            user.setPassword(encodePassword(user.getPassword()));
        }

        Errors errors = new BeanPropertyBindingResult(user, "user");
        validator.validate(user, errors);

        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteById(final Long id) {
        findById(id);
        userRepository.deleteById(id);
    }

    public User findById(final Long id) {
        return userRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public Optional<User> findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    private String encodePassword(final String password) {
        if (password == null || password.isBlank()) {
            throw new RuntimeException("пароль не может быть пустым");
        }
        return passwordEncoder.encode(password);
    }
}
