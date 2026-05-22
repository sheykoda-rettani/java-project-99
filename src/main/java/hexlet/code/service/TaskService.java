package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskFilterDto;

import java.util.List;

public interface TaskService {
    List<TaskDto> findAll(TaskFilterDto filter);

    TaskDto findById(Long id);

    TaskDto create(TaskDto taskDto);

    TaskDto update(Long id, TaskDto taskDto);

    void deleteById(Long id);
}
