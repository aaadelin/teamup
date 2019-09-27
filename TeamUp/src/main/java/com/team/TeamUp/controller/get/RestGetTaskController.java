package com.team.TeamUp.controller.get;

import com.team.TeamUp.domain.Project;
import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.domain.enums.TaskType;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.persistence.TaskRepository;
import com.team.TeamUp.persistence.UserRepository;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Slf4j
public class RestGetTaskController {
    
    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private DTOsConverter dtOsConverter;
    private TaskUtils taskUtils;

    @Autowired
    public RestGetTaskController(UserRepository userRepository, TaskRepository taskRepository, DTOsConverter dtOsConverter, TaskUtils taskUtils) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.dtOsConverter = dtOsConverter;
        this.taskUtils = taskUtils;
    }

    private static final int PAGE_SIZE = 10;

    @RequestMapping(value = "/tasks", method = GET)
    public ResponseEntity<?> getAllTasks(@RequestParam(value = "page", required = false) Integer startPage,
                                         @RequestParam(value = "statuses", required = false) List<TaskStatus> statuses,
                                         @RequestParam(value = "search", required = false) String search,
                                         @RequestParam(value = "sort", required = false) String sort,
                                         @RequestParam(value = "desc", required = false, defaultValue = "false") Boolean desc) {
        log.info("Entering get all tasks method with startPage: {}, statuses {}, search {}, sort {}, descending {}", startPage, statuses, search, sort, desc);
        List<TaskDTO> tasks;

        tasks = taskUtils.findTasksByParameters(startPage, statuses, search, sort, desc);

        log.info(String.format("Returning list of tasks: %s", tasks.toString()));
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @RequestMapping(value = "/task-status", method = GET)
    public ResponseEntity<?> getAllTaskStatus() {
        log.info("Entering get all task status types method");
        List<TaskStatus> taskStatuses = Arrays.asList(TaskStatus.values());
        log.info(String.format("Returning current possible task statuses: %s", taskStatuses.toString()));
        return new ResponseEntity<>(taskStatuses, HttpStatus.OK);
    }

    @RequestMapping(value = "/task-types", method = GET)
    public ResponseEntity<?> getAllTaskTypes() {
        log.info("Entering get all possible task types method");
        List<TaskType> taskTypes = Arrays.asList(TaskType.values());
        log.info(String.format("Returning current possible task types: %s", taskTypes.toString()));
        return new ResponseEntity<>(taskTypes, HttpStatus.OK);
    }

    @RequestMapping(value = "/tasks/{id}", method = GET)
    public ResponseEntity<?> getTaskById(@PathVariable int id) {
        log.info("Entering get task by id method with taskId: {}", id);
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            TaskDTO task = dtOsConverter.getDTOFromTask(taskOptional.get());
            log.info("Returning task: {}", task.toString());
            return new ResponseEntity<>(task, HttpStatus.OK);
        } else {
            log.info("No task found with id {}", id);
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "tasks/{id}/project", method = GET)
    public ResponseEntity<?> getTasksProject(@PathVariable int id) {
        log.info("Entering get task's project with task id {}", id);
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Project project = taskOptional.get().getProject();
            ProjectDTO projectDTO = dtOsConverter.getDTOFromProject(project);
            log.info("Exited with project {}", projectDTO);
            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } else {
            log.info("No task found with id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/tasks/assigned", method = GET)
    public ResponseEntity<?> getAssignedTasks(@RequestHeader Map<String, String> headers,
                                              @RequestParam(value = "page") Integer startPage,
                                              @RequestParam(value = "search", required = false, defaultValue = "") String search,
                                              @RequestParam(value = "sort", required = false, defaultValue = "") String sort,
                                              @RequestParam(value = "desc", required = false, defaultValue = "false") Boolean desc,
                                              @RequestParam(value = "statuses", required = false, defaultValue = "") List<TaskStatus> statuses){
        log.info("Entered method to get assigned tasks with parameters: \nstart page: {}\nstatuses: {}, search {}, sort {}, desc {}, headers: {}",
                startPage, statuses, search, sort, desc, headers);
        User user = userRepository.findByHashKey(headers.get("token")).orElseThrow();
        List<TaskDTO> taskDTOS = taskUtils.findAssignedTasksByParameters(user, startPage, statuses, search, sort, desc);
        log.info(String.format("Exiting with list of tasks: %s", taskDTOS));
        return new ResponseEntity<>(taskDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/tasks/reported", method = GET)
    public ResponseEntity<?> getReportedTasks(@RequestHeader Map<String, String> headers,
                                              @RequestParam(value = "page") Integer startPage,
                                              @RequestParam(value = "search", required = false) String search,
                                              @RequestParam(value = "status", required = false, defaultValue = "OPEN") TaskStatus status){
        log.info(String.format("Entered method to get reported tasks with parameters: \nheaders: %s \nstart page: %s\nstatus: %s \nsearch %s", headers, startPage, status, search));
        User user = userRepository.findByHashKey(headers.get("token")).orElseThrow();
        List<TaskDTO> taskDTOS = taskRepository.findAllByTaskStatusAndReporter(status,
                user,
                PageRequest.of(startPage, PAGE_SIZE))
                .stream().map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        taskDTOS = taskUtils.filterTasks(search, taskDTOS);

        log.info(String.format("Exiting with list of tasks: %s", taskDTOS));
        return new ResponseEntity<>(taskDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/tasks/assigned-reported", method = GET)
    public ResponseEntity<?> getAssignedAndReportedTasks(@RequestHeader Map<String, String> headers,
                                                         @RequestParam(value = "page") Integer startPage,
                                                         @RequestParam(value = "status", required = false) TaskStatus status,
                                                         @RequestParam(value = "search", required = false) String search,
                                                         @RequestParam(value = "statuses", required = false) TaskStatus[] statuses){

        log.info(String.format("Entered method to get assigned and reported tasks with parameters: \nheaders: %s \nstart page: %s\nstatus: %s \n statuses: %s, search %s", headers, startPage, status, Arrays.toString(statuses), search));
        User user = userRepository.findByHashKey(headers.get("token")).orElseThrow();
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
                    Arrays.stream(statuses).map(Enum::ordinal).collect(Collectors.toList()),
                    PageRequest.of(startPage, PAGE_SIZE))
                    .stream()
                    .map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        }
        taskDTOS = taskUtils.filterTasks(search, taskDTOS);
        log.info(String.format("Exiting with list of tasks: %s", taskDTOS));
        return new ResponseEntity<>(taskDTOS, HttpStatus.OK);
    }

}
