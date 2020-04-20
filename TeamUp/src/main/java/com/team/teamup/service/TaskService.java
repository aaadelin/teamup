package com.team.teamup.service;

import com.team.teamup.domain.Project;
import com.team.teamup.domain.Task;
import com.team.teamup.domain.User;
import com.team.teamup.domain.enums.TaskStatus;
import com.team.teamup.dtos.ProjectDTO;
import com.team.teamup.dtos.TaskDTO;
import com.team.teamup.persistence.TaskRepository;
import com.team.teamup.persistence.UserRepository;
import com.team.teamup.utils.DTOsConverter;
import com.team.teamup.utils.query.AbstractLanguageParser;
import com.team.teamup.utils.query.QueryLanguageParser;
import com.team.teamup.utils.TaskUtils;
import com.team.teamup.utils.query.ReflectionQueryLanguageParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TaskService {

    private static final int PAGE_SIZE = 10;

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private final DTOsConverter dtOsConverter;
    private final TaskUtils taskUtils;
    private final ReflectionQueryLanguageParser<Task, Integer> queryLanguageParser;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       DTOsConverter dtOsConverter,
                       TaskUtils taskUtils) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.dtOsConverter = dtOsConverter;
        this.taskUtils = taskUtils;
        this.queryLanguageParser = new ReflectionQueryLanguageParser<>(taskRepository);
        queryLanguageParser.setClazz(Task.class);
    }

    /**
     * @param startPage repository page to get tasks
     * @param statuses  list of statuses tasks should have, if empty, all are taken into consideration
     * @param search    search tem
     * @param sort      task sort propery
     * @param desc      is true, sort will be descenting
     * @return list of taskDTOs filtered and sorted by parameters
     */
    public List<TaskDTO> getTasksByParameters(Integer startPage, List<TaskStatus> statuses, String search, String sort, Boolean desc) {
        return taskUtils.findTasksByParameters(startPage, statuses, search, sort, desc);
    }

    /**
     * @param user     user that is assigned to tasks
     * @param lastDays optional integer representing last number of days that a task has been updated
     * @return list of tasks that are assigned to that user and are modified in lastDays
     */
    public List<Task> getTasksWhereUserIsAssigned(User user, Integer lastDays) {
        if (lastDays == null) {
            log.info("Getting statistics from all time");
            return taskRepository.findAllByAssigneesContaining(user);
        }

        log.info(String.format("Getting statistics from last %s days", lastDays));
        LocalDate nowDate = LocalDate.now().minusDays(lastDays);
        Date sqlDate = Date.valueOf(nowDate);
        return taskRepository.findAllByAssigneesContainingAndLastChangedAfter(user, sqlDate);
    }

    /**
     * @param id task's id
     * @return taskDTO from task with that id
     * @throws NoSuchElementException if there is no such element
     */
    public TaskDTO getTaskDTOById(int id) {
        return dtOsConverter.getDTOFromTask(getByID(id));
    }


    /**
     * @param id task's id
     * @return task with that id
     * @throws NoSuchElementException if there is no such element
     */
    public Task getByID(int id) {
        return taskRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     * @param taskID task's id
     * @return project that contains that task
     * @throws NoSuchElementException if there is no task with that id
     */
    public ProjectDTO getTasksProjectDTO(int taskID) {
        Task task = getByID(taskID);
        Project project = task.getProject();
        return dtOsConverter.getDTOFromProject(project);
    }

    /**
     * @param startPage page from repository
     * @param status    task status
     * @param search    search term
     * @param statuses  list of task statuses
     * @return list or assigned and reported tasks filtered by parameters
     */
    public List<TaskDTO> getTasksByStatusesFromPageWithSearchAssignedToUser(TaskStatus status, List<TaskStatus> statuses, String search, int startPage, User user) {
        List<TaskDTO> taskDTOS = new ArrayList<>();
        if (status != null) {
            log.info("Filter type selected: by one single status");
            taskDTOS = taskRepository.findTasksWithStatusAssignedToOrReportedBy(
                    user.getId(),
                    status.ordinal(),
                    PageRequest.of(startPage, PAGE_SIZE))
                    .stream()
                    .map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        } else if (statuses != null) {
            log.info("Filter type selected: by multiple statuses");
            taskDTOS = taskRepository.findTasksWithStatusesAssignedToOrReportedBy(
                    user.getId(),
                    statuses.stream().map(Enum::ordinal).collect(Collectors.toList()),
                    PageRequest.of(startPage, PAGE_SIZE))
                    .stream()
                    .map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        }
        return taskUtils.filterTasks(search, taskDTOS);
    }

    /**
     * @param status    task status
     * @param user      task's reporter
     * @param startPage repository page
     * @return tasks reported by user with status from page
     */
    public List<TaskDTO> getReportedTasksByStatus(TaskStatus status, User user, int startPage) {
        return convertListToDTOS(
                taskRepository.findAllByTaskStatusAndReporter(status,
                        user,
                        PageRequest.of(startPage, PAGE_SIZE)));
    }

    /**
     * @param assignees list of assignees that bust be in tasks
     * @return list of tasks that contain at least one assignee
     */
    public List<Task> getTasksByAssignees(List<User> assignees) {
        return taskRepository.findDistinctByAssigneesIn(assignees);
    }

    /**
     * @param assignees list of assignees that bust be in tasks
     * @param page      repository page
     * @return list of tasks that contain at least one assignee
     */
    public List<TaskDTO> getTasksByAssignees(List<User> assignees, int page) {
        return convertListToDTOS(taskRepository.findDistinctByAssigneesIn(assignees, PageRequest.of(page, PAGE_SIZE)));
    }

    /**
     * @param project project to get tasks from
     * @param page    repository page
     * @return list of tasks of that project
     */
    public List<TaskDTO> getAllByProject(Project project, int page) {
        if (page < 0) {
            page = (page * -1) - 1;
            return convertListToDTOS(taskRepository.findAllByProjectOrderByIdDesc(project, PageRequest.of(page, PAGE_SIZE)));
        } else {
            return convertListToDTOS(taskRepository.findAllByProject(project, PageRequest.of(page, PAGE_SIZE)));
        }
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public List<TaskDTO> getAllDTO() {
        return convertListToDTOS(taskRepository.findAll());
    }

    public TaskDTO save(Task task) {
        return dtOsConverter.getDTOFromTask(taskRepository.save(task));
    }

    public List<TaskDTO> getAllByQuery(String query) {
        return convertListToDTOS(queryLanguageParser.getAllByQuery(query));
    }

    public List<TaskDTO> getTasksByAssigneesWithStatus(List<User> members, List<String> statuses, Integer page) {
        return convertListToDTOS(
                taskRepository.findDistinctByAssigneesInAndTaskStatusIn(members,
                        statuses.stream().map(status -> TaskStatus.valueOf(status.toUpperCase()))
                                .collect(Collectors.toList()),
                        PageRequest.of(page, PAGE_SIZE))
        );
    }

    private List<TaskDTO> convertListToDTOS(List<Task> tasks) {
        return tasks.stream().map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
    }

    public List<TaskDTO> getAllByProjectAndStatuses(Project project, List<String> statuses, Integer page) {
        if (page < 0) {
            page = (page * -1) - 1;
            return convertListToDTOS(taskRepository.findAllByProjectOrderByIdDesc(project, PageRequest.of(page, PAGE_SIZE)));
        } else {
            return convertListToDTOS(taskRepository.findAllByProjectAndTaskStatusIn(project,
                    statuses.stream().map(TaskStatus::valueOf).collect(Collectors.toList()),
                    PageRequest.of(page, PAGE_SIZE)));
        }
    }
}
