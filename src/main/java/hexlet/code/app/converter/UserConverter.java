package hexlet.code.app.converter;

import hexlet.code.app.dto.UserRequestDto;
import hexlet.code.app.model.User;
import org.springframework.stereotype.Component;

@Component
public final class UserConverter {
    public User fromDto(final UserRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        User result = new User();
        result.setFirstName(requestDto.getFirstName());
        result.setLastName(requestDto.getLastName());
        result.setEmail(requestDto.getEmail());
        result.setPassword(requestDto.getPassword());

        return result;
    }
}
