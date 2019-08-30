package com.team.TeamUp.controller.get;

import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.UserEvent;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.dtos.UserDTO;
import com.team.TeamUp.persistance.TaskRepository;
import com.team.TeamUp.persistance.UserEventRepository;
import com.team.TeamUp.persistance.UserRepository;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TaskUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestGetUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestGetUserController.class);
    private static final int PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 10000;

    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private UserEventRepository eventRepository;

    private DTOsConverter dtOsConverter;
    private TaskUtils taskUtils;

    @Autowired
    public RestGetUserController(UserRepository userRepository, TaskRepository taskRepository, UserEventRepository eventRepository, DTOsConverter dtOsConverter, TaskUtils taskUtils){
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
        this.dtOsConverter = dtOsConverter;
        this.taskUtils = taskUtils;
    }

    @RequestMapping(value = "/users", method = GET)
    public ResponseEntity<?> getAllUsers(@RequestParam(name = "ids", required = false) List<Integer> ids,
                                         @RequestHeader Map<String, String> headers) {
        if(ids != null) {
            LOGGER.info(String.format("Entering get users by ids method with userIds: %s /n and headers: %s", ids, headers.toString()));
            List<UserDTO> users = new ArrayList<>();
            for (int id : ids) {
                Optional<User> userOptional = userRepository.findById(id);
                if (userOptional.isPresent()) {
                    LOGGER.info(String.format("Adding user: %s", userOptional.get()));
                    users.add(dtOsConverter.getDTOFromUser(userOptional.get()));
                } else {
                    LOGGER.info(String.format("No user found with id %s", id));
                    return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
                }
            }
            LOGGER.info(String.format("Returning users: %s", users));
            return new ResponseEntity<>(users, HttpStatus.OK);
        }else {
            LOGGER.info(String.format("Entering get all users method with headers: %s", headers.toString()));
            List<UserDTO> users;
            if(userRepository.findByHashKey(headers.get("token")).orElseThrow().getStatus() == UserStatus.ADMIN){
                //if is admin, return all users
                users = userRepository.findAll().stream().map(dtOsConverter::getDTOFromUser).collect(Collectors.toList());
            }else{
                users = userRepository.findAll().stream().filter(user -> user.getStatus() != UserStatus.ADMIN).map(dtOsConverter::getDTOFromUser).collect(Collectors.toList());
            }
            LOGGER.info(String.format("Returning list: %s", users.toString()));
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/user-status", method = GET)
    public ResponseEntity<?> getAllUserStatus(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all possible user statuses method with headers: %s", headers.toString()));
        List<UserStatus> userStatuses = Arrays.asList(UserStatus.values());
        LOGGER.info(String.format("Returning current user statuses: %s", userStatuses.toString()));
        return new ResponseEntity<>(userStatuses, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{key}/assigned-tasks", method = GET)
    public ResponseEntity<?> getAssignedTasksForUser(@PathVariable String key, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get user's assigned tasks with key %s and headers %s", key, headers));
        User user = userRepository.findByHashKey(key).orElseThrow();

        LOGGER.info(String.format("Acquiring tasks assigned to user %s ", user));
        List<Task> allTasks = taskRepository.findAll();

        List<TaskDTO> filteredTasks = allTasks.stream()
                .filter(task -> task.getAssignees().contains(user))
                .map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        LOGGER.info(String.format("Returning list of tasks: %s", filteredTasks));
        return new ResponseEntity<>(filteredTasks, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{key}/reported-tasks", method = GET)
    public ResponseEntity<?> getReportedTasksByUser(@PathVariable String key, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get reported tasks by user with key %s and headers %s", key, headers));
        User user = userRepository.findByHashKey(key).orElseThrow();
        LOGGER.info(String.format("Acquiring tasks reported by user %s ", user));
        List<Task> allTasks = taskRepository.findAll();

        List<TaskDTO> filteredTasks = allTasks.stream()
                .filter(task -> task.getReporter().equals(user))
                .map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        LOGGER.info(String.format("Returning list of tasks: %s", filteredTasks));
        return new ResponseEntity<>(filteredTasks, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{id}", method = GET)
    public ResponseEntity<?> getUserById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get user by id method with userId: %d /n and headers: %s", id, headers.toString()));
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            LOGGER.info(String.format("Returning user: %s", userOptional.get().toString()));
            return new ResponseEntity<>(dtOsConverter.getDTOFromUser(userOptional.get()), HttpStatus.OK);
        } else {
            LOGGER.info(String.format("No user found with id %s", id));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/logout", method = GET)
    public ResponseEntity<?> logout(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering logout method with headers: %s", headers.toString()));
        String key = headers.get("token");
        Optional<User> userOptional = userRepository.findByHashKey(key);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(false);
            user.setLastActive(LocalDateTime.now());

            userRepository.save(user);
            LOGGER.info("User status saved in database");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/users/{id}/photo", method = GET, produces = "image/jpg")
    public ResponseEntity<?> getFile(@PathVariable("id") Integer id, @RequestHeader Map<String, String> headers) throws IOException {
        LOGGER.info(String.format("Entering get photo for user method with id: %s and headers: %s", id, headers));
        User user = userRepository.findById(id).orElseThrow();

        if (user.getPhoto() == null) {
            File file = new ClassPathResource("static/img/avatar.png").getFile();
            byte[] bytes = Files.readAllBytes(file.toPath());
            String encodedString = Base64.getEncoder().encodeToString(bytes);
            LOGGER.info("Exited with default image");
            return new ResponseEntity<>(encodedString, HttpStatus.OK);
        } else {
            File file = new ClassPathResource("static/img/" + user.getPhoto()).getFile();
            byte[] bytes = Files.readAllBytes(file.toPath());
            LOGGER.info(String.format("Exited with photo with path %s", user.getPhoto()));
            String encodedString = Base64.getEncoder().encodeToString(bytes);
            return new ResponseEntity<>(encodedString, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "users/{id}/tasks", method = GET)
    public ResponseEntity<?> getTasksFor(@PathVariable int id,
                                         @RequestParam(value = "type", required = false) String type,
                                         @RequestParam(value = "search", required = false) String term,
                                         @RequestParam(value = "statuses", required = false) List<TaskStatus> statuses,
                                         @RequestParam(value = "page") Integer page,
                                         @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get user's tasks with user id %s, type value %s, term value %s, page %s, statuses %s and headers %s", id, type, term, page, statuses, headers));
        User user = userRepository.findById(id).orElseThrow();
        if(statuses == null || statuses.size() == 0) {
            statuses = Arrays.asList(TaskStatus.values());
        }
        if (type == null) {

            JSONObject arrays = new JSONObject();
            arrays.put("reported", new JSONArray(taskUtils.getFilteredTasksByType(user, term, "assignedby", page, statuses)));
            arrays.put("assigned", new JSONArray(taskUtils.getFilteredTasksByType(user, term, "assignedto", page, statuses)));

            LOGGER.info(String.format("Exited with list of tasks assigned to and by user %s: %s", user, arrays.toString()));
            return new ResponseEntity<>(arrays.toString(), HttpStatus.OK);
        } else {
            JSONObject arrays = new JSONObject();

            if (type.toLowerCase().equals("assignedto")) {
                arrays.put("reported", new JSONArray());
                arrays.put("assigned", new JSONArray(taskUtils.getFilteredTasksByType(user, term, type, page, statuses)));
            } else if (type.toLowerCase().equals("assignedby")) {
                arrays.put("reported", new JSONArray(taskUtils.getFilteredTasksByType(user, term, type, page, statuses)));
                arrays.put("assigned", new ArrayList<>());
            } else {
                LOGGER.info(String.format("Type option not eligible. Send type: %s", type));
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            LOGGER.info(String.format("Exited with list of tasks assigned to user %s: %s", user, arrays.toString()));
            return new ResponseEntity<>(arrays.toString(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/users/{id}/history", method = GET)
    public ResponseEntity<?> getUsersHistory(@PathVariable int id,
                                             @RequestParam(name = "page", required = false) Integer page){
        LOGGER.info(String.format("Entered method to get user's history with user id: %s and page %s", id, page));
        User user = userRepository.findById(id).orElseThrow();
        List<UserEvent> events;
        if(page != null){
            LOGGER.info("Getting all user s history");
            events = eventRepository.findAllByCreatorOrderByTimeDesc(user, PageRequest.of(page, PAGE_SIZE));
        }else {
            LOGGER.info(String.format("Getting user's history from page %s", page));
            events = eventRepository.findAllByCreatorOrderByTimeDesc(user);
        }
        LOGGER.info(String.format("Exiting with history: %s", events));
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{id}/statistics", method = GET)
    public ResponseEntity<?> getUsersStatistics(@PathVariable int id,
                                                @RequestParam(name = "lastDays", required = false) Integer lastDays,
                                                @RequestParam(name = "reported", required = false) Boolean reported){
        LOGGER.info(String.format("Entered get user's statistics with id %s, reported %s and number of last days: %s", id, reported, lastDays));
        User user = userRepository.findById(id).orElseThrow();
        List<Task> statistics;
        if(lastDays == null){
            LOGGER.info("Getting statistics from all time");
            statistics = taskRepository.findAllByTaskStatusInAndAssigneesContaining(Arrays.asList(TaskStatus.values()), user, PageRequest.of(0, MAX_PAGE_SIZE));
        }else {
            // todo
            LOGGER.info(String.format("Getting statistics from last %s days", lastDays));
            statistics = taskRepository.findAllByTaskStatusInAndAssigneesContaining(Arrays.asList(TaskStatus.values()), user, PageRequest.of(0, MAX_PAGE_SIZE));
//          statistics = taskRepository.findAllByTaskStatusInAndAssigneesContaining(id, List.of(0, 1, 2, 3, 4), PageRequest.of(0, 10000));
        }

        Map<TaskStatus, Integer> statusCount = new HashMap<>();
        for(Task task: statistics){
            TaskStatus currentStatus = task.getTaskStatus();
            if(statusCount.containsKey(currentStatus)){
                statusCount.put(currentStatus, statusCount.get(currentStatus) + 1);
            } else {
                statusCount.put(currentStatus, 1);
            }
        }
        List<Integer> counts = List.of(statusCount.getOrDefault(TaskStatus.OPEN, 0) + statusCount.getOrDefault(TaskStatus.REOPENED, 0),
                statusCount.getOrDefault(TaskStatus.IN_PROGRESS, 0),
                statusCount.getOrDefault(TaskStatus.UNDER_REVIEW, 0),
                statusCount.getOrDefault(TaskStatus.APPROVED, 0));
        LOGGER.info(String.format("Exiting with statistics: %s", counts));
        return new ResponseEntity<>(counts, HttpStatus.OK);
    }

}
