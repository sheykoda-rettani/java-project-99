package hexlet.code.app.specification;

import hexlet.code.app.dto.TaskFilterDto;
import hexlet.code.app.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public final class TaskSpecification {
    public Specification<Task> build(final TaskFilterDto params) {
        return withTitleContains(params.getTitleCont()).
                and(withAssigneeId(params.getAssigneeId())).
                and(withStatusSlug(params.getStatus())).
                and(withLabelId(params.getLabelId()));
    }

    private Specification<Task> withTitleContains(final String titlePart) {
        return (root, query, cb) -> titlePart == null || titlePart.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + titlePart.toLowerCase() + "%");
    }

    private Specification<Task> withAssigneeId(final Long assigneeId) {
        return (root, query, cb) -> assigneeId == null
                ? cb.conjunction()
                : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatusSlug(final String slug) {
        return (root, query, cb) -> slug == null || slug.isBlank()
                ? cb.conjunction()
                : cb.equal(root.join("taskStatus").get("slug"), slug);
    }

    private Specification<Task> withLabelId(final Long labelId) {
        return (root, query, cb) -> labelId == null
                ? cb.conjunction()
                : root.joinSet("labels").get("id").in(labelId);
    }
}
