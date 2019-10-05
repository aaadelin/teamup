package com.team.TeamUp.service;

import com.team.TeamUp.domain.Project;
import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.persistence.TaskRepository;
import com.team.TeamUp.persistence.UserRepository;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    private TaskRepository taskRepository;
    private UserRepository userRepository;

    private DTOsConverter dtOsConverter;
    private TaskUtils taskUtils;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       DTOsConverter dtOsConverter,
                       TaskUtils taskUtils){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.dtOsConverter = dtOsConverter;
        this.taskUtils = taskUtils;
    }

    /**
     *
     * @param startPage repository page to get tasks
     * @param statuses list of statuses tasks should have, if empty, all are taken into consideration
     * @param search search tem
     * @param sort task sort propery
     * @param desc is true, sort will be descenting
     * @return list of taskDTOs filtered and sorted by parameters
     */
    public List<TaskDTO> getTasksByParameters(Integer startPage, List<TaskStatus> statuses, String search, String sort, Boolean desc){
        return taskUtils.findTasksByParameters(startPage, statuses, search, sort, desc);
    }

    /**
     *
     * @param user user that is assigned to tasks
     * @param lastDays optional integer representing last number of days that a task has been updated
     * @return list of tasks that are assigned to that user and are modified in lastDays
     */
    public List<Task> getTasksWhereUserIsAssigned(User user, Integer lastDays){
        if(lastDays == null){
            log.info("Getting statistics from all time");
            return taskRepository.findAllByAssigneesContaining(user);
        }

        log.info(String.format("Getting statistics from last %s days", lastDays));
        LocalDate nowDate = LocalDate.now().minusDays(lastDays);
        Date sqlDate = Date.valueOf(nowDate);
        return taskRepository.findAllByAssigneesContainingAndLastChangedAfter(user, sqlDate);
    }

    /**
     *
     * @param ID task's id
     * @return taskDTO from task with that id
     * @throws NoSuchElementException if there is no such element
     */
    public TaskDTO getTaskDTOById(int ID){
        return dtOsConverter.getDTOFromTask(getByID(ID));
    }


    /**
     *
     * @param ID task's id
     * @return task with that id
     * @throws NoSuchElementException if there is no such element
     */
    public Task getByID(int ID){
        return taskRepository.findById(ID).orElseThrow(NoSuchElementException::new);
    }

    /**
     *
     * @param taskID task's id
     * @return project that contains that task
     * @throws NoSuchElementException if there is no task with that id
     */
    public ProjectDTO getTasksProjectDTO(int taskID){
        Task task = getByID(taskID);
        Project project = task.getProject();
        return dtOsConverter.getDTOFromProject(project);
    }

    /**
     *
     * @param startPage page from repository
     * @param status task status
     * @param search search term
     * @param statuses list of task statuses
     * @return list or assigned and reported tasks filtered by parameters
     */
    public List<TaskDTO> getTasksByStatusesfromPageWithSearchAssignedToUser(TaskStatus status, List<TaskStatus> statuses, String search, int startPage, User user){
        List<TaskDTO> taskDTOS = new ArrayList<>();
        if(status != null){
            log.info("Filter type selected: by one single status");
            taskDTOS = taskRepository.findTasksWithStatusAssignedToOrReportedBy(
                    user.getId(),
                    status.ordinal(),
                    PageRequest.of(startPage, PAGE_SIZE))
                    .stream()
                    .map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        }else if (statuses != null){
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
     *
     * @param status task status
     * @param user task's reporter
     * @param startPage repository page
     * @return tasks reported by user with status from page
     */
    public List<TaskDTO> getReportedTasksByStatus(TaskStatus status, User user, int startPage){
        return taskRepository.findAllByTaskStatusAndReporter(status,
                user,
                PageRequest.of(startPage, PAGE_SIZE))
                .stream().map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
    }

    /**
     *
     * @param assignees list of assignees that bust be in tasks
     * @return list of tasks that contain at least one assignee
     */
    public List<Task> getTasksByAssignees(List<User> assignees){
        return taskRepository.findDistinctByAssigneesIn(assignees);
    }

    /**
     *
     * @param project project to get tasks from
     * @param page repository page
     * @return list of tasks of that project
     */
    public List<Task> getAllByProject(Project project, int page){
        if(page < 0){
            page = (page * -1) -1;
            return taskRepository.findAllByProjectOrderByIdDesc(project, PageRequest.of(page, PAGE_SIZE));
        }else{
            return taskRepository.findAllByProject(project, PageRequest.of(page, PAGE_SIZE));
        }
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }
}
