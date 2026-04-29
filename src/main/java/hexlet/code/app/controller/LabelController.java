package hexlet.code.app.controller;

import hexlet.code.app.model.Label;
import hexlet.code.app.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public final class LabelController {
    /**
     * Сервис меток.
     */
    private final LabelService labelService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Label>> getAllLabels() {
        List<Label> labels = labelService.findAll();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(labels.size())).body(labels);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Label getLabelById(@PathVariable final Long id) {
        return labelService.findByIdOrThrow(id);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public Label createLabel(@Valid @RequestBody final Label label) {
        return labelService.create(label);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Label updateLabel(
            @PathVariable final Long id,
            @Valid @RequestBody final Label labelDetails) {
        return labelService.update(id, labelDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteLabel(@PathVariable final Long id) {
        labelService.deleteById(id);
    }
}
