package hexlet.code.app.service;

import hexlet.code.app.exception.UnableToDeleteException;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class TaskStatusService {
    /**
     * Репозиторий статусов.
     */
    private final TaskStatusRepository taskStatusRepository;
    /**
     * Для валидации частичных данных.
     */
    private final Validator validator;

    public List<TaskStatus> findAll() {
        return taskStatusRepository.findAll();
    }

    public TaskStatus findByIdOrThrow(final Long id) {
        return taskStatusRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Статус c id %d не найден".formatted(id)));
    }

    public TaskStatus create(@Valid final TaskStatus taskStatus) {
        if (taskStatusRepository.findBySlug(taskStatus.getSlug()).isPresent()) {
            throw new DuplicateKeyException("Статус со слагом '%s' уже существует".formatted(taskStatus.getSlug()));
        }
        return taskStatusRepository.save(taskStatus);
    }

    public TaskStatus update(final Long id, final TaskStatus details) {
        TaskStatus status = findByIdOrThrow(id);

        if (details.getName() != null) {
            status.setName(details.getName());
        }
        if (details.getSlug() != null) {
            if (taskStatusRepository.findBySlug(details.getSlug()).isPresent()) {
                throw new DuplicateKeyException("Статус со слагом '%s' уже существует".formatted(details.getSlug()));
            }
            status.setSlug(details.getSlug());
        }
        var violations = validator.validate(status);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Ошибка валидации данных запроса", violations);
        }
        return taskStatusRepository.save(status);
    }

    @Named("statusMapping")
    public TaskStatus findBySlugOrNull(final String slug) {
        if (slug == null || slug.isBlank()) {
            return null;
        }
        return taskStatusRepository.findBySlug(slug).orElse(null);
    }

    public void deleteStatus(final Long id) {
        TaskStatus status = findByIdOrThrow(id);
        try {
            taskStatusRepository.delete(status);
        } catch (DataIntegrityViolationException e) {
            throw new UnableToDeleteException("Невозможно удалить статус.", e);
        }
    }
}
