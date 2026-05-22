package hexlet.code.service;

import hexlet.code.model.TaskStatus;
import jakarta.validation.Valid;
import org.mapstruct.Named;

import java.util.List;

public interface TaskStatusService {
    List<TaskStatus> findAll();

    TaskStatus findByIdOrThrow(Long id);

    TaskStatus create(@Valid TaskStatus taskStatus);

    TaskStatus update(Long id, TaskStatus details);

    @Named("statusMapping")
    TaskStatus findBySlugOrNull(String slug);

    void deleteStatus(Long id);
}
