package hexlet.code.service;

import hexlet.code.dto.UserRequestDto;
import hexlet.code.model.User;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User create(UserRequestDto userDto);

    User update(Long id, UserRequestDto userDto);

    List<User> findAll();

    void deleteById(Long id);

    User findByIdOrThrow(Long id);

    @Named("userMapping")
    User findByIdOrNull(Long id);

    User findByIdOrNull(long id);

    boolean isCurrentUser(long id);

    Optional<User> findByEmail(String email);
}
