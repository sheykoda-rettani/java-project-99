package hexlet.code.app.controller;

import hexlet.code.app.dto.LoginRequestDto;
import hexlet.code.app.util.JWTUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public final class AuthController {
    /**
     * Работа с jwt.
     */
    private final JWTUtils jwtUtils;

    /**
     * Аутентификация.
     */
    private final AuthenticationManager authenticationManager;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String create(@RequestBody @Valid final LoginRequestDto loginRequest) {
        var authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword());

        authenticationManager.authenticate(authentication);
        return jwtUtils.generateToken(loginRequest.getUsername());
    }
}
