package hexlet.code.app.dto;

import lombok.Data;

@Data
public final class TaskFilterDto {
    /**
     * Подстрока в названии.
     */
    private String titleCont;
    /**
     * ID исполнителя.
     */
    private Long assigneeId;
    /**
     * Слаг статуса.
     */
    private String status;
    /**
     * ID метки.
     */
    private Long labelId;
}
