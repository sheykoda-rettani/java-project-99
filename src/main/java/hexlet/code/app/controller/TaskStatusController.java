package hexlet.code.app.controller;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/task_statuses")
@RequiredArgsConstructor
public final class TaskStatusController {
    /**
     * Сервис статусов.
     */
    private final TaskStatusService taskStatusService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaskStatus>> getAllStatuses() {
        var statuses = taskStatusService.findAll();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(statuses.size())).body(statuses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskStatus getStatusById(@PathVariable final Long id) {
        return taskStatusService.findById(id);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createStatus(@Valid @RequestBody final TaskStatus taskStatus) {
        return taskStatusService.create(taskStatus);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TaskStatus updateStatus(
            @PathVariable final Long id,
            @RequestBody final TaskStatus taskStatusDetails) {
        return taskStatusService.update(id, taskStatusDetails);
    }
}
