package hexlet.code.app.service;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

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

    public TaskStatus findById(final Long id) {
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
        TaskStatus status = findById(id);

        if (details.getName() != null) {
            status.setName(details.getName());
        }
        if (details.getSlug() != null) {
            if (taskStatusRepository.findBySlug(details.getSlug()).isPresent()) {
                throw new DuplicateKeyException("Статус со слагом '%s' уже существует".formatted(details.getSlug()));
            }
            status.setSlug(details.getSlug());
        }
        Errors errors = new BeanPropertyBindingResult(status, "status");
        validator.validate(status, errors);
        return taskStatusRepository.save(status);
    }
}
