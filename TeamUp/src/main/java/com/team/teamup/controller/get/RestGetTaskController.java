package com.team.teamup.controller.get;

import com.team.teamup.domain.User;
import com.team.teamup.domain.TaskStatus;
import com.team.teamup.domain.enums.TaskType;
import com.team.teamup.dtos.ProjectDTO;
import com.team.teamup.dtos.TaskDTO;
import com.team.teamup.service.TaskService;
import com.team.teamup.service.UserService;
import com.team.teamup.utils.Pair;
import com.team.teamup.utils.TaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Slf4j
public class RestGetTaskController {

    private UserService userService;
    private TaskService taskService;
    private TaskUtils taskUtils;

    @Autowired
    public RestGetTaskController(TaskUtils taskUtils,
                                 TaskService taskService,
                                 UserService userService) {
        this.taskService = taskService;
        this.taskUtils = taskUtils;
        this.userService = userService;
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
    @RequestMapping(value = "/tasks", method = GET)
    public ResponseEntity<?> getAllTasks(@RequestParam(value = "page", required = false) Integer startPage,
                                         @RequestParam(value = "statuses", required = false) List<String> statuses,
                                         @RequestParam(value = "search", required = false) String search,
                                         @RequestParam(value = "sort", required = false) String sort,
                                         @RequestParam(value = "desc", required = false, defaultValue = "false") Boolean desc) {
        log.info("Entering get all tasks method with startPage: {}, statuses {}, search {}, sort {}, descending {}", startPage, statuses, search, sort, desc);
        List<TaskDTO> tasks;
        List<TaskStatus> taskStatuses = taskUtils.getTaskStatusesFromStrings(statuses);
        tasks = taskService.getTasksByParameters(startPage, taskStatuses, search, sort, desc);
        log.info(String.format("Returning list of tasks: %s", tasks.toString()));
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     *
     * @return possible task statuses
     */
    @RequestMapping(value = "/task-status", method = GET)
    public ResponseEntity<?> getAllTaskStatus() {
        log.info("Entering get all task status types method");
        List<String> taskStatuses = taskService.getAllTaskStatuses().stream().map(TaskStatus::getStatus).collect(Collectors.toList());
        log.info(String.format("Returning current possible task statuses: %s", taskStatuses.toString()));
        return new ResponseEntity<>(taskStatuses, HttpStatus.OK);
    }

    /**
     *
     * @return possible task statuses
     */
    @RequestMapping(value = "/task-status/detailed", method = GET)
    public ResponseEntity<?> getAllTaskStatusDetailed() {
        log.info("Entering get all task status with details");
        List<Pair<String, Integer>> taskStatuses = taskService.getAllTaskStatuses()
                .stream().map(ts -> new Pair<String, Integer>(ts.getStatus(), ts.getOrder())).collect(Collectors.toList());
        log.info(String.format("Returning current possible task statuses: %s", taskStatuses.toString()));
        return new ResponseEntity<>(taskStatuses, HttpStatus.OK);
    }

    /**
     *
     * @return possible task types
     */
    @RequestMapping(value = "/task-types", method = GET)
    public ResponseEntity<?> getAllTaskTypes() {
        log.info("Entering get all possible task types method");
        List<TaskType> taskTypes = Arrays.asList(TaskType.values());
        log.info(String.format("Returning current possible task types: %s", taskTypes.toString()));
        return new ResponseEntity<>(taskTypes, HttpStatus.OK);
    }

    /**
     *
     * @param id task's id
     * @return task with that id or 404 if not fount
     */
    @RequestMapping(value = "/tasks/{id}", method = GET)
    public ResponseEntity<?> getTaskById(@PathVariable int id) {
        log.info("Entering get task by id method with taskId: {}", id);
        try{
            TaskDTO task = taskService.getTaskDTOById(id);
            log.info("Exited with task {}", task);
            return new ResponseEntity<>(task, HttpStatus.OK);
        }catch (NoSuchElementException ignore){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    /**
     *
     * @param id taks's id
     * @return project that contains that task or 404
     */
    @RequestMapping(value = "tasks/{id}/project", method = GET)
    public ResponseEntity<?> getTasksProject(@PathVariable int id) {
        log.info("Entering get task's project with task id {}", id);
        try{
            ProjectDTO project = taskService.getTasksProjectDTO(id);
            log.info("Exited with project {}", project);
            return new ResponseEntity<>(project, HttpStatus.OK);
        }catch (NoSuchElementException ignore){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * @param headers client's headers containing token
     * @param startPage pare from repository
     * @param search search term
     * @param sort task property
     * @param desc descending or ascending
     * @param statuses list of statuses
     * @return assigned tasks filtered and sorted
     */
    @RequestMapping(value = "/tasks/assigned", method = GET)
    public ResponseEntity<?> getAssignedTasks(@RequestHeader Map<String, String> headers,
                                              @RequestParam(value = "page") Integer startPage,
                                              @RequestParam(value = "search", required = false, defaultValue = "") String search,
                                              @RequestParam(value = "sort", required = false, defaultValue = "") String sort,
                                              @RequestParam(value = "desc", required = false, defaultValue = "false") Boolean desc,
                                              @RequestParam(value = "statuses", required = false, defaultValue = "") List<String> statuses){
        log.info("Entered method to get assigned tasks with parameters: \nstart page: {}\nstatuses: {}, search {}, sort {}, desc {}, headers: {}",
                startPage, statuses, search, sort, desc, headers);
        User user = userService.getByHashKey(headers.get("token"));
        List<TaskStatus> taskStatuses = taskUtils.getTaskStatusesFromStrings(statuses);
        List<TaskDTO> taskDTOS = taskUtils.findAssignedTasksByParameters(user, startPage, taskStatuses, search, sort, desc);
        log.info(String.format("Exiting with list of tasks: %s", taskDTOS));
        return new ResponseEntity<>(taskDTOS, HttpStatus.OK);
    }

    /**
     *
     * @param headers client's headers containing token
     * @param startPage page for repository
     * @param search search string
     * @param status task status
     * @return reported tasks filtered by parameters
     */
    @RequestMapping(value = "/tasks/reported", method = GET)
    public ResponseEntity<?> getReportedTasks(@RequestHeader Map<String, String> headers,
                                              @RequestParam(value = "page") Integer startPage,
                                              @RequestParam(value = "search", required = false) String search,
                                              @RequestParam(value = "status", required = false, defaultValue = "") String status){
        log.info(String.format("Entered method to get reported tasks with parameters: \nheaders: %s \nstart page: %s\nstatus: %s \nsearch %s", headers, startPage, status, search));
        User user = userService.getByHashKey(headers.get("token"));
        TaskStatus taskStatus = taskUtils.getTaskStatusFromString(status);
        List<TaskDTO> taskDTOS = taskService.getReportedTasksByStatus(taskStatus, user, startPage);
        taskDTOS = taskUtils.filterTasks(search, taskDTOS);

        log.info(String.format("Exiting with list of tasks: %s", taskDTOS));
        return new ResponseEntity<>(taskDTOS, HttpStatus.OK);
    }

    /**
     *
     * @param headers client's headers containing token
     * @param startPage page from repository
     * @param status task status
     * @param search search term
     * @param statuses list of task statuses
     * @return list or assigned and reported tasks filtered by parameters
     */
    @RequestMapping(value = "/tasks/assigned-reported", method = GET)
    public ResponseEntity<?> getAssignedAndReportedTasks(@RequestHeader Map<String, String> headers,
                                                         @RequestParam(value = "page") Integer startPage,
                                                         @RequestParam(value = "status", required = false) String status,
                                                         @RequestParam(value = "search", required = false) String search,
                                                         @RequestParam(value = "statuses", required = false) List<String> statuses){

        log.info("Entered method to get assigned and reported tasks with parameters: headers: {} \nstart page: {}\nstatus: {} \n statuses: {}, search {}", headers, startPage, status, statuses, search);
        User user = userService.getByHashKey(headers.get("token"));
        TaskStatus taskStatus = taskUtils.getTaskStatusFromString(status);
        List<TaskStatus> taskStatuses = taskUtils.getTaskStatusesFromStrings(statuses);
        List<TaskDTO> taskDTOS = taskService.getTasksByStatusesFromPageWithSearchAssignedToUser(taskStatus, taskStatuses, search, startPage, user);
        log.info(String.format("Exiting with list of tasks: %s", taskDTOS));
        return new ResponseEntity<>(taskDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/tasks/query", method = GET)
    public ResponseEntity<?> getTasksUsingQuery(@RequestParam(value = "search-query") String query){
        return new ResponseEntity<>(taskService.getAllByQuery(query), HttpStatus.OK);
    }
}
