package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDto {
    /**
     * Id задачи.
     */
    private Long id;

    /**
     * Наименование задачи.
     */
    @NotBlank(message = "Название задачи обязательно")
    private String title;

    /**
     * Номер задачи.
     */
    private Integer index;

    /**
     * Описание задачи.
     */
    private String content;

    /**
     * Дата создания задачи.
     */
    private LocalDateTime createdAt;

    /**
     * Id пользователя, на которого назначена задача.
     */
    private Long assigneeId;

    /**
     * Статус задачи.
     */
    @NotNull(message = "Статус задачи обязателен")
    private String status;
}
