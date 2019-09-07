package com.team.TeamUp.controller.get;

import com.team.TeamUp.domain.Project;
import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.domain.enums.TaskType;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.persistance.TaskRepository;
import com.team.TeamUp.persistance.UserRepository;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TaskUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RestGetTaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestGetTaskController.class);

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
    public ResponseEntity<?> getAllTasks(@RequestHeader Map<String, String> headers,
                                         @RequestParam(value = "page", required = false) Integer startPage,
                                         @RequestParam(value = "status", required = false) TaskStatus status,
                                         @RequestParam(value = "statuses", required = false) List<TaskStatus> statuses,
                                         @RequestParam(value = "search", required = false) String search,
                                         @RequestParam(value = "sort", required = false) String sort,
                                         @RequestParam(value = "desc", required = false, defaultValue = "false") Boolean desc,
                                         @RequestParam(value = "last", required = false) Long numberOfTasksSortedDesc) {
        LOGGER.info(String.format("Entering get all tasks method with headers: %s, startPage: %s, status: %s and number of last tasks %s", headers.toString(), startPage, status, numberOfTasksSortedDesc));
        List<TaskDTO> tasks = new ArrayList<>();
        //TODO
        //returning all tasks
        if(startPage == null && status == null && numberOfTasksSortedDesc == null) {
            LOGGER.info("Returning all tasks");
            tasks = taskUtils.getAllTasksConvertedToDTOs();
        }

        //returning all tasks from a page
        if (startPage != null && status == null && numberOfTasksSortedDesc == null){
            LOGGER.info(String.format("Returning all tasks from page %s", startPage));
            tasks = taskRepository.findAll(PageRequest.of(startPage, PAGE_SIZE))
                    .stream()
                    .map(dtOsConverter::getDTOFromTask)
                    .sorted((o1, o2) -> o2.getId() - o1.getId())
                    .collect(Collectors.toList());
        }

        //returning all tasks with status STATUS
        if(startPage == null && status != null && numberOfTasksSortedDesc == null) {
            LOGGER.info(String.format("Returning all tasks with status %s", status));
            tasks = taskRepository.findAllByTaskStatus(status)
                    .stream()
                    .map(dtOsConverter::getDTOFromTask)
                    .sorted((o1, o2) -> o2.getId() - o1.getId())
                    .collect(Collectors.toList());
        }

        // returning last LAST tasks
        if(startPage == null && status == null && numberOfTasksSortedDesc != null){
            LOGGER.info(String.format("Returning last %s tasks", numberOfTasksSortedDesc));
            tasks = taskUtils.getLastNTasks(numberOfTasksSortedDesc);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        }

        //returning tasks with status STATUS from page STARTPAGE
        if(status != null && startPage != null ){
            LOGGER.info(String.format("Returning page %s with tasks with status %s", startPage, status));
            tasks = taskRepository.findAllByTaskStatus(status, PageRequest.of(startPage, PAGE_SIZE))
                    .stream()
                    .map(dtOsConverter::getDTOFromTask)
                    .collect(Collectors.toList());
        }

        tasks = taskUtils.findTasksByParameters(startPage, statuses, search, sort, desc);

        LOGGER.info(String.format("Returning list of tasks: %s", tasks.toString()));
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @RequestMapping(value = "/task-status", method = GET)
    public ResponseEntity<?> getAllTaskStatus(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all task status types method with headers: %s", headers.toString()));
        List<TaskStatus> taskStatuses = Arrays.asList(TaskStatus.values());
        LOGGER.info(String.format("Returning current possible task statuses: %s", taskStatuses.toString()));
        return new ResponseEntity<>(taskStatuses, HttpStatus.OK);
    }

    @RequestMapping(value = "/task-types", method = GET)
    public ResponseEntity<?> getAllTaskTypes(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all possible task types method with headers: %s", headers.toString()));
        List<TaskType> taskTypes = Arrays.asList(TaskType.values());
        LOGGER.info(String.format("Returning current possible task types: %s", taskTypes.toString()));
        return new ResponseEntity<>(taskTypes, HttpStatus.OK);
    }

    @RequestMapping(value = "/tasks/{id}", method = GET)
    public ResponseEntity<?> getTaskById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get task by id method with taskId: %d /n and headers: %s", id, headers.toString()));
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            TaskDTO task = dtOsConverter.getDTOFromTask(taskOptional.get());
            LOGGER.info(String.format("Returning task: %s", task.toString()));
            return new ResponseEntity<>(task, HttpStatus.OK);
        } else {
            LOGGER.info(String.format("No task found with id %s", id));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "tasks/{id}/project", method = GET)
    public ResponseEntity<?> getTasksProject(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get task's project with task id %s and headers %s", id, headers));
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Project project = taskOptional.get().getProject();
            ProjectDTO projectDTO = dtOsConverter.getDTOFromProject(project);
            LOGGER.info(String.format("Exited with project %s", projectDTO));
            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } else {
            LOGGER.info(String.format("No task found with id %s", id));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/tasks/assigned", method = GET)
    public ResponseEntity<?> getAssignedTasks(@RequestHeader Map<String, String> headers,
                                              @RequestParam(value = "page") Integer startPage,
                                              @RequestParam(value = "search", required = false) String search,
                                              @RequestParam(value = "sort", required = false) String sort,
                                              @RequestParam(value = "desc", required = false) Boolean desc,
                                              @RequestParam(value = "statuses", required = false) List<TaskStatus> statuses){
        LOGGER.info(String.format("Entered method to get assigned tasks with parameters: \nheaders: %s \nstart page: %s\nstatuses: %s, search %s, sort %s, desc %s",
                headers, startPage, statuses, search, sort, desc));
        User user = userRepository.findByHashKey(headers.get("token")).orElseThrow();
        List<TaskDTO> taskDTOS = taskUtils.findAssignedTasksByParameters(user, startPage, statuses, search, sort, desc);
        LOGGER.info(String.format("Exiting with list of tasks: %s", taskDTOS));
        return new ResponseEntity<>(taskDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/tasks/reported", method = GET)
    public ResponseEntity<?> getReportedTasks(@RequestHeader Map<String, String> headers,
                                              @RequestParam(value = "page") Integer startPage,
                                              @RequestParam(value = "search", required = false) String search,
                                              @RequestParam(value = "status") TaskStatus status){
        LOGGER.info(String.format("Entered method to get reported tasks with parameters: \nheaders: %s \nstart page: %s\nstatus: %s \nsearch %s", headers, startPage, status, search));
        User user = userRepository.findByHashKey(headers.get("token")).orElseThrow();
        List<TaskDTO> taskDTOS = taskRepository.findAllByTaskStatusAndReporter(status,
                user,
                PageRequest.of(startPage, PAGE_SIZE))
                .stream().map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        LOGGER.info(String.format("Exiting with list of tasks: %s", taskDTOS));

        taskDTOS = taskUtils.filterTasks(search, taskDTOS);
        return new ResponseEntity<>(taskDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/tasks/assigned-reported", method = GET)
    public ResponseEntity<?> getAssignedAndReportedTasks(@RequestHeader Map<String, String> headers,
                                                         @RequestParam(value = "page") Integer startPage,
                                                         @RequestParam(value = "status", required = false) TaskStatus status,
                                                         @RequestParam(value = "search", required = false) String search,
                                                         @RequestParam(value = "statuses", required = false) TaskStatus[] statuses){

        LOGGER.info(String.format("Entered method to get assigned and reported tasks with parameters: \nheaders: %s \nstart page: %s\nstatus: %s \n statuses: %s, search %s", headers, startPage, status, Arrays.toString(statuses), search));
        User user = userRepository.findByHashKey(headers.get("token")).orElseThrow();
        List<TaskDTO> taskDTOS = new ArrayList<>();
        if(status != null){
            LOGGER.info("Filter type selected: by one single status");
            taskDTOS = taskRepository.findTasksWithStatusAssignedToOrReportedBy(
                    user.getId(),
                    status.ordinal(),
                    PageRequest.of(startPage, PAGE_SIZE))
                    .stream()
                    .map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        }else if (statuses != null){
            LOGGER.info("Filter type selected: by multiple statuses");
            taskDTOS = taskRepository.findTasksWithStatusesAssignedToOrReportedBy(
                    user.getId(),
                    Arrays.stream(statuses).map(Enum::ordinal).collect(Collectors.toList()),
                    PageRequest.of(startPage, PAGE_SIZE))
                    .stream()
                    .map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        }
        taskDTOS = taskUtils.filterTasks(search, taskDTOS);
        LOGGER.info(String.format("Exiting with list of tasks: %s", taskDTOS));
        return new ResponseEntity<>(taskDTOS, HttpStatus.OK);
    }

}
