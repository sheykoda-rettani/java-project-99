package hexlet.code.app.service;

import hexlet.code.app.exception.UnableToDeleteException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.dto.UserRequestDto;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

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
    private final UserMapper userMapper;
    /**
     * Для валидации частичных данных.
     */
    private final Validator validator;

    /**
     * Кодирование пароля.
     */
    private final PasswordEncoder passwordEncoder;

    public User create(final UserRequestDto userDto) {
        User user = userMapper.fromUserDto(userDto);
        user.setPassword(encodePassword(user.getPassword()));
        return userRepository.save(user);
    }

    public User update(final Long id, final UserRequestDto userDto) {
        User user = findByIdOrThrow(id);
        User userDetails = userMapper.fromUserDto(userDto);

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

        var violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Ошибка валидации данных запроса", violations);
        }

        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteById(final Long id) {
        findByIdOrThrow(id);
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new UnableToDeleteException("Невозможно удалить пользователя.", e);
        }
    }

    public User findByIdOrThrow(final Long id) {
        return userRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Пользователь c id %d не найден".formatted(id)));
    }

    @Named("userMapping")
    public User findByIdOrNull(final Long id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id).orElse(null);
    }

    public User findByIdOrNull(final long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    private String encodePassword(final String password) {
        if (password == null || password.isBlank()) {
            return null;
        }
        return passwordEncoder.encode(password);
    }
}
