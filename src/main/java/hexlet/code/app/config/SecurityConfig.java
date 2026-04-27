package hexlet.code.app.config;

import hexlet.code.app.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:DesignForExtension")
public class SecurityConfig {
    /**
     * Кодирование jwt.
     */
    private final JwtDecoder jwtDecoder;

    /**
     * Сервер пользователей.
     */
    private final CustomUserDetailsService userService;

    /**
     * Кодирование пароля.
     */
    private final PasswordEncoder passwordEncoder;


    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) {
        return http.csrf(AbstractHttpConfigurer::disable).
                authorizeHttpRequests(auth -> auth.
                        requestMatchers("/").permitAll().
                        requestMatchers("/index.html").permitAll().
                        requestMatchers("/assets/**").permitAll().
                        requestMatchers("/h2-console/**").permitAll().
                        requestMatchers("/api/login").permitAll().
                        requestMatchers("/api/**").authenticated()).
                sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).
                oauth2ResourceServer(rs ->
                        rs.jwt(jwt -> jwt.decoder(jwtDecoder))).
                httpBasic(Customizer.withDefaults()).
                build();
    }

    @Bean
    public AuthenticationProvider daoAuthProvider(final AuthenticationManagerBuilder auth) {
        var provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
