package hexlet.code.app.service;

import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class TaskService {
    /**
     * Репозиторий задач.
     */
    private final TaskRepository taskRepository;
    /**
     * Сервис пользователей.
     */
    private final UserRepository userRepository;
    /**
     * Сервис статусов задач.
     */
    private final TaskStatusService taskStatusService;
    /**
     * Преобразование между DTO и сущностями задач.
     */
    private final TaskMapper taskMapper;
    /**
     * Для валидации частичных данных.
     */
    private final Validator validator;

    public List<TaskDto> findAll() {
        List<TaskDto> result = taskRepository.findAll().stream().map(taskMapper::toTaskDto).toList();
        return result;
    }

    private Task findByIdOrThrow(final Long id) {
        return taskRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Задача с id %d не найдена".formatted(id)));
    }

    public TaskDto findById(final Long id) {
        Task found = findByIdOrThrow(id);
        return taskMapper.toTaskDto(found);
    }

    public TaskDto create(final TaskDto taskDto) {
        Task task = taskMapper.toNewTaskEntity(taskDto);
        taskRepository.save(task);
        taskDto.setId(task.getId());
        return taskDto;
    }

    public TaskDto update(final Long id, final TaskDto taskDto) {
        Task task = findByIdOrThrow(id);
        taskMapper.updateFromDto(taskDto, task);
        TaskDto toValidate = taskMapper.toTaskDto(task);

        var violations = validator.validate(toValidate);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Ошибка валидации данных запроса", violations);
        }

        taskRepository.save(task);
        return taskMapper.toTaskDto(task);
    }

    public void deleteById(final Long id) {
        Task task = findByIdOrThrow(id);
        taskRepository.delete(task);
    }
}
