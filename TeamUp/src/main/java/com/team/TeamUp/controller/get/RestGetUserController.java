package com.team.TeamUp.controller.get;

import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.UserEvent;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.dtos.TeamDTO;
import com.team.TeamUp.dtos.UserDTO;
import com.team.TeamUp.service.TaskService;
import com.team.TeamUp.service.UserService;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TaskUtils;
import com.team.TeamUp.validation.UserValidation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Slf4j
public class RestGetUserController {


    private TaskService taskService;
    private UserService userService;
    private UserValidation userValidation;

    private DTOsConverter dtOsConverter;
    private TaskUtils taskUtils;

    @Autowired
    public RestGetUserController(TaskService taskService,
                                 DTOsConverter dtOsConverter,
                                 TaskUtils taskUtils,
                                 UserValidation userValidation,
                                 UserService userService){
        this.userValidation = userValidation;
        this.taskService = taskService;
        this.dtOsConverter = dtOsConverter;
        this.taskUtils = taskUtils;
        this.userService = userService;
    }

    /**
     *
     * @param ids list of ids of the users, optional parameter if you want to get specific users
     * @param page page of the repository
     * @param sort user's field to be sorted by
     * @param filter sequence of characters to be found in their first name or last name
     * @param headers headers of the requester
     * @return list of users depending on the parameters
     */
    @RequestMapping(value = "/users", method = GET)
    public ResponseEntity<?> getAllUsers(@RequestParam(name = "ids", required = false) List<Integer> ids,
                                         @RequestParam(name = "page", required = false, defaultValue = "-1") Integer page,
                                         @RequestParam(name = "sort", required = false, defaultValue = "id") String sort,
                                         @RequestParam(name = "filter", required = false) String filter,
                                         @RequestHeader Map<String, String> headers) {
        if(ids != null) {
            log.info("Entering get users by ids method with userIds: {}, page {}, filter {} and sort {}", ids, page, filter, sort);
            List<UserDTO> response = userService.getUsersByIds(ids);
            log.info(String.format("Returning users: %s", response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else {
            log.info("Entering get all users method with headers: {}", headers.toString());
            List<UserDTO> users = userService.getSortedAndFilteredUsers(filter, sort, page, headers.get("token"));
            log.info(String.format("Returning list: %s", users));
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    /**
     *
     * @param headers headers containing the token of the user
     * @return list of available user statuses
     */
    @RequestMapping(value = "/user-status", method = GET)
    public ResponseEntity<?> getAllUserStatus(@RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get all possible user statuses method with headers: %s", headers.toString()));
        List<UserStatus> userStatuses = Arrays.asList(UserStatus.values());
        log.info(String.format("Returning current user statuses: %s", userStatuses.toString()));
        return new ResponseEntity<>(userStatuses, HttpStatus.OK);
    }

    /**
     * Method used to get reported tasks of the same user without making another request containing the id
     * @param key token of the user
     * @param headers headers of the requester
     * @return list of tasks the user is assigned to
     */
    @Deprecated
    @RequestMapping(value = "/users/{key}/assigned-tasks", method = GET)
    public ResponseEntity<?> getAssignedTasksForUser(@PathVariable String key, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get user's assigned tasks with key %s and headers %s", key, headers));

        User user = userService.getUserByHashKey(key);
        log.info(String.format("Acquiring tasks assigned to user %s ", user));

        List<TaskDTO> filteredTasks = userService.getAssignedTasks(user);
        log.info(String.format("Returning list of tasks: %s", filteredTasks));
        return new ResponseEntity<>(filteredTasks, HttpStatus.OK);
    }

    /**
     * Method used to get reported tasks of the same user without making another request containing the id
     * @param key token of the user
     * @param headers headers of the requester
     * @return list of reported tasks of the user
     */
    @Deprecated
    @RequestMapping(value = "/users/{key}/reported-tasks", method = GET)
    public ResponseEntity<?> getReportedTasksByUser(@PathVariable String key, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get reported tasks by user with key %s and headers %s", key, headers));
        User user = userService.getUserByHashKey(key);
        log.info(String.format("Acquiring tasks reported by user %s ", user));

        List<TaskDTO> filteredTasks = userService.getReportedTasks(user);
        log.info(String.format("Returning list of tasks: %s", filteredTasks));
        return new ResponseEntity<>(filteredTasks, HttpStatus.OK);
    }

    /**
     *
     * @param id user's id
     * @param headers headers list of the requester
     * @return details about the user with that id
     */
    @RequestMapping(value = "/users/{id}", method = GET)
    public ResponseEntity<?> getUserById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get user by id method with userId: %d /n and headers: %s", id, headers.toString()));
        if (userValidation.exists(id)) {
            UserDTO user = userService.getUserDTOByID(id);
            log.info(String.format("Returning user: %s", user));
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            log.info(String.format("No user found with id %s", id));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * @param headers headers containing the token of the requester
     * @return ok if the user is eligible to logout or forbidden if the key is not valid
     */
    @RequestMapping(value = "/logout", method = GET)
    public ResponseEntity<?> logout(@RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering logout method with headers: %s", headers.toString()));
        String key = headers.get("token");
        boolean loggedOut = userService.logout(key);

        return new ResponseEntity<>(loggedOut ? HttpStatus.OK : HttpStatus.FORBIDDEN);
    }

    /**
     *
     * @param id user's id
     * @param headers requester's headers
     * @return user's photo if he has one or the default photo
     * @throws IOException if there is a file error
     */
    @RequestMapping(value = "/users/{id}/photo", method = GET, produces = "image/jpg")
    public ResponseEntity<?> getFile(@PathVariable("id") Integer id, @RequestHeader Map<String, String> headers) throws IOException {
        log.info(String.format("Entering get photo for user method with id: %s and headers: %s", id, headers));
        return new ResponseEntity<>(userService.getUsersPhoto(id), HttpStatus.OK);
    }

    /**
     *
     * @param id user's id
     * @param type String with 'assignedto' or 'assignedby' or empty string
     * @param term search term
     * @param statuses list of task statuses
     * @param page number of page from the repository
     * @param headers headers of the requester
     * @return list of tasks in which the user with id ID is the reporter, assignee or any that contain term in summary or description,
     * from that page in the repository.
     */
    @RequestMapping(value = "users/{id}/tasks", method = GET)
    public ResponseEntity<?> getTasksFor(@PathVariable int id,
                                         @RequestParam(value = "type", required = false) String type,
                                         @RequestParam(value = "search", required = false) String term,
                                         @RequestParam(value = "statuses", required = false) List<TaskStatus> statuses,
                                         @RequestParam(value = "page") Integer page,
                                         @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get user's tasks with user id %s, type value %s, term value %s, page %s, statuses %s and headers %s", id, type, term, page, statuses, headers));
        User user = userService.getUserByID(id);
        if(statuses == null || statuses.size() == 0) {
            //if no statuses are selected, all tatuses are being taken into consideration
            statuses = Arrays.asList(TaskStatus.values());
        }
        if (type == null) {

            JSONObject arrays = new JSONObject();
            arrays.put("reported", new JSONArray(taskUtils.getFilteredTasksByType(user, term, "assignedby", page, statuses)));
            arrays.put("assigned", new JSONArray(taskUtils.getFilteredTasksByType(user, term, "assignedto", page, statuses)));

            log.info(String.format("Exited with list of tasks assigned to and by user %s: %s", user, arrays.toString()));
            return new ResponseEntity<>(arrays, HttpStatus.OK);
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

    /**
     *
     * @param id user's id
     * @param page repository's page number
     * @return list of UserEvents of the user with ID from page PAGE in repository
     */
    @RequestMapping(value = "/users/{id}/history", method = GET)
    public ResponseEntity<?> getUsersHistory(@PathVariable int id,
                                             @RequestParam(name = "page", required = false) Integer page){
        log.info(String.format("Entered method to get user's history with user id: %s and page %s", id, page));
        List<UserEvent> events;
        if(page != null){
            log.info("Getting all user s history");
            events = userService.getUsersHistoryByPage(id, page);
        }else {
            log.info(String.format("Getting user's history from page %s", page));
            events = userService.getUsersHistory(id);
        }
        log.info(String.format("Exiting with history: %s", events));
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /**
     * Endpoint to return statistics from tasks that are assigned to user with id and are changed within lastDays
     * @param id id of the user that is assigned to tasks
     * @param lastDays optional, number of days within the tasks are changed
     * @param reported boolean, if true take into consideration the tasks that he had reported
     * @return list of integers representing the number of tasks in the 4 main categories: to_do, in_progress, under_review, done
     */
    @RequestMapping(value = "/users/{id}/statistics", method = GET)
    public ResponseEntity<?> getUsersStatistics(@PathVariable int id,
                                                @RequestParam(name = "lastDays", required = false) Integer lastDays,
                                                @RequestParam(name = "reported", required = false) Boolean reported){
        log.info(String.format("Entered get user's statistics with id %s, reported %s and number of last days: %s", id, reported, lastDays));
        User user = userService.getUserByID(id);
        // todo include reported or remove the parameter
        List<Task> statistics = taskService.getTasksWhereUserIsAssigned(user, lastDays);
        List<Integer> counts = taskUtils.createStatisticsFromListOfTasks(statistics);
        log.info(String.format("Exiting with statistics: %s", counts));
        return new ResponseEntity<>(counts, HttpStatus.OK);
    }

    /**
     *
     * @return list of users that can be team leaders
     */
    @RequestMapping(value = "/users/high-rank", method = GET)
    public ResponseEntity<?> getHigherRankUsers(){
        log.info("Entergin method to get high rank users");
        List<UserDTO> dtos = userService.getHighRankUsers();
        log.info(String.format("Returning list of users %s", dtos));
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     *
     * @param id user's id
     * @return team that the user is in or not found if he doesn't belong to any
     */
    @RequestMapping(value = "/users/{id}/team", method = GET)
    public ResponseEntity<?> getUsersTeam(@PathVariable int id){
        log.info("Entering get team that contains the user method with user id {}", id);
        if (userValidation.exists(id)){
            TeamDTO teamDTO = dtOsConverter.getDTOFromTeam(userService.getUserByID(id).getTeam());
            log.info("Exiting method with teamDTO {}", teamDTO);
            return new ResponseEntity<>(teamDTO, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to get teams that have leader the user with the specified id
     * @param id user's id
     * @return list of teams with that user as leader
     */
    @RequestMapping(value = "/users/{id}/leading", method = GET)
    public ResponseEntity<?> getUsersTeams(@PathVariable int id){
        log.info("Entered method to get teams that user with id {} leads", id);
        List<TeamDTO> teams = userService.getUsersLeadingTeams(id);
        log.info("Exiting with teams {}", teams);
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }

    /**
     * Endppoint for getting user's owned projects
     * @param id user's id
     * @return list of projects that have him as owner
     */
    @RequestMapping(value = "/users/{id}/projects", method = GET)
    public ResponseEntity<?> getOwnedProjects (@PathVariable int id){
        log.info("Enteing method to get user's owned project with user id {}", id);
        List<ProjectDTO> projects = userService.getUsersOwnedProjects(id);
        log.info("Exiting with projects {}", projects);
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }
}
