package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.service.TaskStatusService;
import hexlet.code.app.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserService.class, TaskStatusService.class}
)
public interface TaskMapper {
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "userMapping")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "statusMapping")
    Task toTaskEntity(TaskDto dto);

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "title", source = "name")
    TaskDto toTaskDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "userMapping")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "statusMapping")
    void updateFromDto(TaskDto dto, @MappingTarget Task task);

    List<TaskDto> toDtoList(List<Task> tasks);
}
