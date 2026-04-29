package hexlet.code.app.service;

import hexlet.code.app.exception.UnableToDeleteException;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class LabelService {
    /**
     * Репозиторий меток.
     */
    private final LabelRepository labelRepository;

    public List<Label> findAll() {
        return labelRepository.findAll();
    }

    public Label findByIdOrThrow(final Long id) {
        return labelRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Метка с id %d не найдена".formatted(id)));
    }

    public Label findByNameOrThrow(final String name) {
        return labelRepository.findByName(name).
                orElseThrow(() -> new NotFoundException("Метка с именем '%s' не найдена".formatted(name)));
    }

    public Label create(final Label label) {
        if (labelRepository.findByName(label.getName()).isPresent()) {
            throw new DuplicateKeyException("Метка с именем '%s' уже существует".formatted(label.getName()));
        }
        return labelRepository.save(label);
    }

    public Label update(final Long id, final Label toUpdate) {
        Label found = findByIdOrThrow(id);
        Optional<Label> other = labelRepository.findByName(toUpdate.getName());
        if (other.isPresent() && !other.get().getId().equals(id)) {
            throw new DuplicateKeyException("Метка с именем '%s' уже существует".formatted(toUpdate.getName()));
        }
        found.setName(toUpdate.getName());
        return labelRepository.save(found);
    }

    public void deleteById(final Long id) {
        Label found = findByIdOrThrow(id);
        try {
            labelRepository.delete(found);
        } catch (DataIntegrityViolationException e) {
            throw new UnableToDeleteException("Невозможно удалить метку.", e);
        }
    }
}
