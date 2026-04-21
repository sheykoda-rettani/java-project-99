package hexlet.code.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode
public final class User {
    /**
     * ID поле.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя.
     */
    @Size(min = 2, message = "Имя должно содержать минимум 2 символа")
    @Column(nullable = false)
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @Size(min = 2, message = "Имя должно содержать минимум 2 символа")
    @Column(nullable = false)
    private String lastName;

    /**
     * Адрес электронной почты.
     */
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email обязателен")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Пароль.
     */
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 3, message = "Пароль должен содержать минимум 3 символа")
    @JsonIgnore
    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    private String password;

    /**
     * Время создания записи.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private LocalDateTime createdAt;

    /**
     * Время изменения записи.
     */
    @Column(name = "updated_at", nullable = false)
    @EqualsAndHashCode.Exclude
    private LocalDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
