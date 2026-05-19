package hexlet.code.app.service.impl;

import hexlet.code.app.dto.TaskFilterDto;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.service.TaskService;
import hexlet.code.app.specification.TaskSpecification;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class TaskServiceImpl implements TaskService {
    /**
     * Репозиторий задач.
     */
    private final TaskRepository taskRepository;

    /**
     * Преобразование между DTO и сущностями задач.
     */
    private final TaskMapper taskMapper;
    /**
     * Для валидации частичных данных.
     */
    private final Validator validator;

    /**
     * Спецификация для поиска.
     */
    private final TaskSpecification taskSpecification;

    @Override
    public List<TaskDto> findAll(final TaskFilterDto filter) {
        Specification<Task> spec = taskSpecification.build(filter);
        List<Task> resultEntities = taskRepository.findAll(spec);
        List<TaskDto> result = resultEntities.stream().map(taskMapper::toTaskDto).toList();
        return result;
    }

    private Task findByIdOrThrow(final Long id) {
        return taskRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Задача с id %d не найдена".formatted(id)));
    }

    @Override
    public TaskDto findById(final Long id) {
        Task found = findByIdOrThrow(id);
        return taskMapper.toTaskDto(found);
    }

    @Override
    public TaskDto create(final TaskDto taskDto) {
        Task task = taskMapper.toNewTaskEntity(taskDto);
        taskRepository.save(task);
        taskDto.setId(task.getId());
        return taskDto;
    }

    @Override
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

    @Override
    public void deleteById(final Long id) {
        Task task = findByIdOrThrow(id);
        taskRepository.delete(task);
    }
}
