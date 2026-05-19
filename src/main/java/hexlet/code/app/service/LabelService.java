package hexlet.code.app.service;

import hexlet.code.app.model.Label;

import java.util.List;

public interface LabelService {
    List<Label> findAll();

    Label findByIdOrThrow(Long id);

    Label findByNameOrThrow(String name);

    Label create(Label label);

    Label update(Long id, Label toUpdate);

    void deleteById(Long id);
}
