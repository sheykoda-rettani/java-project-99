package hexlet.code.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_statuses")
@Data
@SuppressWarnings("checkstyle:AnnotationUseStyle")
public final class TaskStatus {
    /**
     * Поле ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название статуса.
     */
    @NotBlank(message = "Название статуса обязательно")
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Слаг.
     */
    @NotBlank(message = "Слаг обязателен")
    @Column(nullable = false, unique = true)
    private String slug;

    /**
     * Дата создания.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
