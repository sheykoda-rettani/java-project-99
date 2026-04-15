package hexlet.code.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
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
    @Column(nullable = false)
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @Column(nullable = false)
    private String lastName;

    /**
     * Адрес электронной почты.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Пароль.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Время создания записи.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Время изменения записи.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
