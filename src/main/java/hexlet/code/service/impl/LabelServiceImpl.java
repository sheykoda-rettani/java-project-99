package hexlet.code.service.impl;

import hexlet.code.exception.UnableToDeleteException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class LabelServiceImpl implements LabelService {
    /**
     * Репозиторий меток.
     */
    private final LabelRepository labelRepository;

    @Override
    public List<Label> findAll() {
        return labelRepository.findAll();
    }

    @Override
    public Label findByIdOrThrow(final Long id) {
        return labelRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Метка с id %d не найдена".formatted(id)));
    }

    @Override
    public Label findByNameOrThrow(final String name) {
        return labelRepository.findByName(name).
                orElseThrow(() -> new NotFoundException("Метка с именем '%s' не найдена".formatted(name)));
    }

    @Override
    public Label create(final Label label) {
        return labelRepository.save(label);
    }

    @Override
    public Label update(final Long id, final Label toUpdate) {
        Label found = findByIdOrThrow(id);
        found.setName(toUpdate.getName());
        return labelRepository.save(found);
    }

    @Override
    public void deleteById(final Long id) {
        Label found = findByIdOrThrow(id);
        try {
            labelRepository.delete(found);
        } catch (DataIntegrityViolationException e) {
            throw new UnableToDeleteException("Невозможно удалить метку.", e);
        }
    }
}
