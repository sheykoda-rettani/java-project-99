package hexlet.code.app.service.impl;

import hexlet.code.app.exception.UnableToDeleteException;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.TaskStatusService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class TaskStatusServiceImpl implements TaskStatusService {
    /**
     * Репозиторий статусов.
     */
    private final TaskStatusRepository taskStatusRepository;
    /**
     * Для валидации частичных данных.
     */
    private final Validator validator;

    @Override
    public List<TaskStatus> findAll() {
        return taskStatusRepository.findAll();
    }

    @Override
    public TaskStatus findByIdOrThrow(final Long id) {
        return taskStatusRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Статус c id %d не найден".formatted(id)));
    }

    @Override
    public TaskStatus create(@Valid final TaskStatus taskStatus) {
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public TaskStatus update(final Long id, final TaskStatus details) {
        TaskStatus status = findByIdOrThrow(id);

        if (details.getName() != null) {
            status.setName(details.getName());
        }
        if (details.getSlug() != null) {
            status.setSlug(details.getSlug());
        }
        var violations = validator.validate(status);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Ошибка валидации данных запроса", violations);
        }
        return taskStatusRepository.save(status);
    }

    @Named("statusMapping")
    @Override
    public TaskStatus findBySlugOrNull(final String slug) {
        if (slug == null || slug.isBlank()) {
            return null;
        }
        return taskStatusRepository.findBySlug(slug).orElse(null);
    }

    @Override
    public void deleteStatus(final Long id) {
        TaskStatus status = findByIdOrThrow(id);
        try {
            taskStatusRepository.delete(status);
        } catch (DataIntegrityViolationException e) {
            throw new UnableToDeleteException("Невозможно удалить статус.", e);
        }
    }
}
