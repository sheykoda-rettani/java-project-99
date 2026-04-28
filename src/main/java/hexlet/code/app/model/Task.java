package hexlet.code.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
public final class Task {
    /**
     * Id задачи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Наименование задачи.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Номер задачи.
     */
    @Column(name = "task_index")
    private Integer index;

    /**
     * Описание задачи.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Дата создания задачи.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private LocalDateTime createdAt;

    /**
     * Статус задачи.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_status_id", nullable = false)
    private TaskStatus taskStatus;

    /**
     * Пользователь, на которого назначена задача.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
