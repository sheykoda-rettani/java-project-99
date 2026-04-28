package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public final class TaskController {
    /**
     * Сервис задач.
     */
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.findAll();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(tasks.size())).body(tasks);
    }

    @GetMapping("/{id}")
    public TaskDto getTaskById(@PathVariable final Long id) {
        return taskService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@Valid @RequestBody final TaskDto task) {
        return taskService.create(task);
    }

    @PutMapping("/{id}")
    public TaskDto updateTask(
            @PathVariable final Long id,
            @RequestBody final TaskDto taskDetails) {
        return taskService.update(id, taskDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable final Long id) {
        taskService.deleteById(id);
    }
}
