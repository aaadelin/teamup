package com.team.TeamUp.controller.get;

import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.UserEvent;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.dtos.TeamDTO;
import com.team.TeamUp.dtos.UserDTO;
import com.team.TeamUp.persistence.TaskRepository;
import com.team.TeamUp.persistence.UserEventRepository;
import com.team.TeamUp.persistence.UserRepository;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TaskUtils;
import com.team.TeamUp.utils.UserUtils;
import com.team.TeamUp.validation.UserValidation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
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
@Slf4j
public class RestGetUserController {

    private static final int PAGE_SIZE = 10;

    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private UserEventRepository eventRepository;
    private UserValidation userValidation;

    private DTOsConverter dtOsConverter;
    private TaskUtils taskUtils;
    private UserUtils userUtils;

    @Autowired
    public RestGetUserController(UserRepository userRepository,
                                 TaskRepository taskRepository,
                                 UserEventRepository eventRepository,
                                 DTOsConverter dtOsConverter,
                                 TaskUtils taskUtils,
                                 UserValidation userValidation,
                                 UserUtils userUtils){
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
        this.userValidation = userValidation;
        this.dtOsConverter = dtOsConverter;
        this.taskUtils = taskUtils;
        this.userUtils = userUtils;
    }

    @RequestMapping(value = "/users", method = GET)
    public ResponseEntity<?> getAllUsers(@RequestParam(name = "ids", required = false) List<Integer> ids,
                                         @RequestParam(name = "page", required = false, defaultValue = "-1") Integer page,
                                         @RequestParam(name = "sort", required = false) String sort,
                                         @RequestParam(name = "filter", required = false) String filter,
                                         @RequestHeader Map<String, String> headers) {
        if(ids != null) {
            log.info("Entering get users by ids method with userIds: {}, page {}, filter {} and sort {}", ids, page, filter, sort);
            ResponseEntity response = userUtils.getUsersByIds(ids);
            log.info(String.format("Returning users: %s", response.getBody()));
            return response;
        }else {
            log.info("Entering get all users method with headers: {}", headers.toString());
            List<UserDTO> users;
            if(filter == null || filter.equals("")){
                Boolean isAdmin = userValidation.isValid(headers.get("token"), UserStatus.ADMIN);
                users = userUtils.sortUsers(page, sort, isAdmin, PAGE_SIZE);
            } else {
                users = userUtils.filterUsers(filter);
            }

            log.info(String.format("Returning list: %s", users.toString()));
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/user-status", method = GET)
    public ResponseEntity<?> getAllUserStatus(@RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get all possible user statuses method with headers: %s", headers.toString()));
        List<UserStatus> userStatuses = Arrays.asList(UserStatus.values());
        log.info(String.format("Returning current user statuses: %s", userStatuses.toString()));
        return new ResponseEntity<>(userStatuses, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{key}/assigned-tasks", method = GET)
    public ResponseEntity<?> getAssignedTasksForUser(@PathVariable String key, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get user's assigned tasks with key %s and headers %s", key, headers));
        User user = userRepository.findByHashKey(key).orElseThrow();

        log.info(String.format("Acquiring tasks assigned to user %s ", user));
        List<Task> allTasks = taskRepository.findAll();

        List<TaskDTO> filteredTasks = allTasks.stream()
                .filter(task -> task.getAssignees().contains(user))
                .map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        log.info(String.format("Returning list of tasks: %s", filteredTasks));
        return new ResponseEntity<>(filteredTasks, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{key}/reported-tasks", method = GET)
    public ResponseEntity<?> getReportedTasksByUser(@PathVariable String key, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get reported tasks by user with key %s and headers %s", key, headers));
        User user = userRepository.findByHashKey(key).orElseThrow();
        log.info(String.format("Acquiring tasks reported by user %s ", user));
        List<Task> allTasks = taskRepository.findAll();

        List<TaskDTO> filteredTasks = allTasks.stream()
                .filter(task -> task.getReporter().equals(user))
                .map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        log.info(String.format("Returning list of tasks: %s", filteredTasks));
        return new ResponseEntity<>(filteredTasks, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{id}", method = GET)
    public ResponseEntity<?> getUserById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get user by id method with userId: %d /n and headers: %s", id, headers.toString()));
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            log.info(String.format("Returning user: %s", userOptional.get().toString()));
            return new ResponseEntity<>(dtOsConverter.getDTOFromUser(userOptional.get()), HttpStatus.OK);
        } else {
            log.info(String.format("No user found with id %s", id));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/logout", method = GET)
    public ResponseEntity<?> logout(@RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering logout method with headers: %s", headers.toString()));
        String key = headers.get("token");
        Optional<User> userOptional = userRepository.findByHashKey(key);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(false);
            user.setLastActive(LocalDateTime.now());

            userRepository.save(user);
            log.info("User status saved in database");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        log.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/users/{id}/photo", method = GET, produces = "image/jpg")
    public ResponseEntity<?> getFile(@PathVariable("id") Integer id, @RequestHeader Map<String, String> headers) throws IOException {
        log.info(String.format("Entering get photo for user method with id: %s and headers: %s", id, headers));
        User user = userRepository.findById(id).orElseThrow();

        if (user.getPhoto() == null) {
            File file = new ClassPathResource("static/img/avatar.png").getFile();
            byte[] bytes = Files.readAllBytes(file.toPath());
            String encodedString = Base64.getEncoder().encodeToString(bytes);
            log.info("Exited with default image");
            return new ResponseEntity<>(encodedString, HttpStatus.OK);
        } else {
            File file = new ClassPathResource("static/img/" + user.getPhoto()).getFile();
            byte[] bytes = Files.readAllBytes(file.toPath());
            log.info(String.format("Exited with photo with path %s", user.getPhoto()));
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
        log.info(String.format("Entering get user's tasks with user id %s, type value %s, term value %s, page %s, statuses %s and headers %s", id, type, term, page, statuses, headers));
        User user = userRepository.findById(id).orElseThrow();
        if(statuses == null || statuses.size() == 0) {
            statuses = Arrays.asList(TaskStatus.values());
        }
        if (type == null) {

            JSONObject arrays = new JSONObject();
            arrays.put("reported", new JSONArray(taskUtils.getFilteredTasksByType(user, term, "assignedby", page, statuses)));
            arrays.put("assigned", new JSONArray(taskUtils.getFilteredTasksByType(user, term, "assignedto", page, statuses)));

            log.info(String.format("Exited with list of tasks assigned to and by user %s: %s", user, arrays.toString()));
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
                log.info(String.format("Type option not eligible. Send type: %s", type));
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            log.info(String.format("Exited with list of tasks assigned to user %s: %s", user, arrays.toString()));
            return new ResponseEntity<>(arrays.toString(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/users/{id}/history", method = GET)
    public ResponseEntity<?> getUsersHistory(@PathVariable int id,
                                             @RequestParam(name = "page", required = false) Integer page){
        log.info(String.format("Entered method to get user's history with user id: %s and page %s", id, page));
        User user = userRepository.findById(id).orElseThrow();
        List<UserEvent> events;
        if(page != null){
            log.info("Getting all user s history");
            events = eventRepository.findAllByCreatorOrderByTimeDesc(user, PageRequest.of(page, PAGE_SIZE));
        }else {
            log.info(String.format("Getting user's history from page %s", page));
            events = eventRepository.findAllByCreatorOrderByTimeDesc(user);
        }
        log.info(String.format("Exiting with history: %s", events));
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{id}/statistics", method = GET)
    public ResponseEntity<?> getUsersStatistics(@PathVariable int id,
                                                @RequestParam(name = "lastDays", required = false) Integer lastDays,
                                                @RequestParam(name = "reported", required = false) Boolean reported){
        log.info(String.format("Entered get user's statistics with id %s, reported %s and number of last days: %s", id, reported, lastDays));
        User user = userRepository.findById(id).orElseThrow();
        List<Task> statistics;
        if(lastDays == null){
            log.info("Getting statistics from all time");
            statistics = taskRepository.findAllByTaskStatusInAndAssigneesContaining(Arrays.asList(TaskStatus.values()), user);
        }else {
            // todo
            log.info(String.format("Getting statistics from last %s days", lastDays));
            statistics = taskRepository.findAllByTaskStatusInAndAssigneesContaining(Arrays.asList(TaskStatus.values()), user);
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
        log.info(String.format("Exiting with statistics: %s", counts));
        return new ResponseEntity<>(counts, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/high-rank", method = GET)
    public ResponseEntity<?> getHigherRankUsers(){
        log.info("Entergin method to get high rank users");
        List<User> users = userRepository.findAll();
        List<UserDTO> dtos = users.stream().filter(user -> user.getStatus().ordinal() >= 3 && user.getStatus() != UserStatus.ADMIN)
                .map(dtOsConverter::getDTOFromUser).collect(Collectors.toList());
        log.info(String.format("Returning list of users %s", dtos));
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{id}/team", method = GET)
    public ResponseEntity<?> getUsersTeam(@PathVariable int id){
        log.info("Entering get team that contains the user method with user id {}", id);
        User user = userRepository.findById(id).orElseThrow();
        if (user.getTeam() != null){
            TeamDTO teamDTO = dtOsConverter.getDTOFromTeam(user.getTeam());
            log.info("Exiting method with teamDTO {}", teamDTO);
            return new ResponseEntity<>(teamDTO, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
