package com.team.teamup.controller;

import com.team.teamup.domain.*;
import com.team.teamup.domain.enums.UserEventType;
import com.team.teamup.domain.enums.UserStatus;
import com.team.teamup.dtos.*;
import com.team.teamup.service.*;
import com.team.teamup.utils.DTOsConverter;
import com.team.teamup.utils.MailUtils;
import com.team.teamup.utils.TokenUtils;
import com.team.teamup.utils.UserUtils;
import com.team.teamup.validation.UserValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

//PUT methods - for updating


@RestController
@RequestMapping("/api")
@CrossOrigin
@Slf4j
public class RestPutController {

    private UserUtils userUtils;
    private UserValidation userValidation;

    private TeamService teamService;
    private UserService userService;
    private TaskService taskService;
    private ProjectService projectService;
    private ResetRequestService resetRequestService;
    private LocationService locationService;

    private DTOsConverter dtOsConverter;
    private MailUtils mailUtils;


    @Autowired
    public RestPutController(TeamService teamService, UserService userService, TaskService taskService,
                             ProjectService projectService, UserValidation userValidation, DTOsConverter dtOsConverter,
                             LocationService locationService,
                             ResetRequestService resetRequestService, UserUtils userUtils, MailUtils mailUtils) {

        this.teamService = teamService;
        this.userService = userService;
        this.taskService = taskService;
        this.projectService = projectService;
        this.locationService = locationService;
        this.dtOsConverter = dtOsConverter;
        this.resetRequestService = resetRequestService;
        this.userValidation = userValidation;
        this.mailUtils = mailUtils;
        this.userUtils = userUtils;
        log.info("Creating RestPutController");
    }

    /**
     * Put method for updating the user.
     *
     * @param user    User object to pe updated in the database
     * @param headers headers map of the client that initiated the update
     * @return OK if the user was found and updated, NOT FOUND if the user does not exist in the database
     */
    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@RequestBody UserDTO user, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering update user method with user: %s and headers: %s", user, headers));

        try {
            User updatingUser = userService.getByHashKey(headers.get("token"));
            User userServiceByID = userService.getByID(user.getId());
            if (updatingUser.getStatus().equals(UserStatus.ADMIN)) {
                if (user.getId() == updatingUser.getId() && !user.getStatus().equals(updatingUser.getStatus())) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
                userUtils.createEvent(updatingUser,
                        String.format("Updated user \"%s %s\"", userServiceByID.getFirstName(), userServiceByID.getLastName()),
                        UserEventType.UPDATE);

                User realUser = dtOsConverter.getUserFromDTO(user, UserStatus.ADMIN);
                userService.save(realUser);
                log.info(String.format("User with id %s has been successfully updated in database", user.getId()));
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (
                NoSuchElementException ignore) {
            log.info(String.format("User with id %s has not been found to update", user.getId()));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Put method for updating the project
     *
     * @param projectDTO Project object to be updated in the database
     * @param headers    headers map of the client that initiated the request
     * @return OK if the project was successfully updated or NOT FOUND if the project is not in the DB
     */
    @RequestMapping(value = "/project", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProject(@RequestBody ProjectDTO projectDTO, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering update project with project: %s and headers: %s", projectDTO, headers));


        if (userValidation.isValid(headers, UserStatus.ADMIN) || userValidation.isOwner(headers, projectDTO)) {
            try {
                Project projectOptional = projectService.getByID(projectDTO.getId());
                userUtils.createEvent(userService.getByHashKey(headers.get("token")),
                        String.format("Updated project \"%s\"", projectOptional.getName()),
                        UserEventType.UPDATE);
                Project project = dtOsConverter.getProjectFromDTO(projectDTO);
                projectService.save(project);
                log.info(String.format("Project with id %s has been successfully updated in database", project.getId()));
                return new ResponseEntity<>("OK", HttpStatus.OK);
            } catch (NoSuchElementException ignore) {
                log.info(String.format("Project with id %s has not been found to update", projectDTO.getId()));
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }

        }
        log.error("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    /**
     * only the admin, reporter or assignee can change a task
     *
     * @param taskDTO TaskDTO instance send from user
     * @param headers request headers containing token
     * @return response ok, not_found, Forbidden
     */
    @RequestMapping(value = "/tasks", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTask(@RequestBody TaskDTO taskDTO, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering update task with task: %s and headers: %s", taskDTO, headers));

        try {
            Task taskByID = taskService.getByID(taskDTO.getId());
            userUtils.createEvent(userService.getByHashKey(headers.get("token")),
                    String.format("Updated task \"%s\"", taskByID.getSummary()),
                    UserEventType.UPDATE);

            User user = userService.getByHashKey(headers.get("token"));
            if (taskByID.getAssignees().contains(user) ||
                    taskByID.getReporter().getId() == user.getId() ||
                    user.getStatus().equals(UserStatus.ADMIN)) {

                Task task = dtOsConverter.getTaskFromDTOForUpdate(taskDTO, user);
                taskService.save(task);
                log.info(String.format("Task with id %s has been successfully updated in database", task.getId()));
                return new ResponseEntity<>("OK", HttpStatus.OK);
            } else {
                log.error("User not eligible");
                return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
        } catch (NoSuchElementException ignore) {
            log.info(String.format("Task with id %s has not been found to update", taskDTO.getId()));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Put method for updating the team entity
     *
     * @param team    Team instance to be updated in the database
     * @param headers request headers containing the token
     * @return OK if the team is in the database, the requester is admin or team lead and the team has been successfully updated in the DB,
     * NOT FOUND if the team is not found in the database, and FORBIDDEN if the requester is not admin or team leader
     */
    @RequestMapping(value = "/teams", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTeam(@RequestBody TeamDTO team, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering update team with team: %s and headers: %s", team, headers));

        User user = userService.getByHashKey(headers.get("token"));
        if (team.getLeaderID() == user.getId() || user.getStatus().equals(UserStatus.ADMIN)) { //only the leader or admin can change AND only the admin can change the leader

            Team teamByID = teamService.getTeamByID(team.getId());
            try {
                userUtils.createEvent(userService.getByHashKey(headers.get("token")),
                        String.format("Updated team \"%s\" from department \"%s\"", team.getName(), teamByID.getDepartment()),
                        UserEventType.UPDATE);
                Team newTeam = dtOsConverter.getTeamFromDTO(team, user.getStatus());
                teamService.save(newTeam);
                log.info(String.format("Team with id %s has been successfully updated in database", newTeam.getId()));
                return new ResponseEntity<>("OK", HttpStatus.OK);
            } catch (NoSuchElementException ignore) {
                log.info(String.format("Team with id %s has not been found to update", team.getId()));
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        log.error("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    /**
     * Put method for updating user's password
     *
     * @param id         id of the user of which the password has to change
     * @param parameters map of parameters containing the old password and the new password
     * @param headers    headers of the requester containing the token
     * @return NOT ACCEPTABLE if the actual password and the old password don't match, or the new password is too short
     * FORBIDDEN if the user is not eligible, and OK if the password has been successfully changed
     */
    @RequestMapping(value = "/user/{id}/password", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUserPassword(@PathVariable int id,
                                                @RequestParam Map<String, String> parameters,
                                                @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entered changing password with id %s, parameters %s and headers %s", id, parameters, headers));

        User requester = userService.getByHashKey(headers.get("token"));
        User userToChange = userService.getByID(id);

        if (parameters.get("newPassword").length() < 6) {
            log.info("New password is too short");
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        String oldPassword = TokenUtils.getMD5Token(parameters.get("oldPassword")).toLowerCase();
        String newPassword = TokenUtils.getMD5Token(parameters.get("newPassword")).toLowerCase();

        if (requester.getId() == userToChange.getId() || requester.getStatus() == UserStatus.ADMIN) {

            if (userToChange.getPassword().toLowerCase().equals(oldPassword)) {
                userToChange.setPassword(newPassword);
                log.info("Password successfully changed");
                if (parameters.get("logout").equals("true")) {
                    userToChange.setActive(false);
                    log.info("User has been logged out");
                }
                userService.save(userToChange);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            log.info("Passwords don't match");
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } else {
            log.info("User not eligible");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    /**
     * Put method for updating a request, gets tha password sent from the user
     * If the password is valid and the request is still valid (within the max number of minutes),
     * the new password will become user's password
     *
     * @param resetRequestDTO ResetRequestDTO object containing the new password from the user
     * @return OK if the request is successfully processed
     */
    @RequestMapping(value = "/requests", method = PUT)
    public ResponseEntity<?> updateRequest(@RequestBody ResetRequestDTO resetRequestDTO) {
        log.info(String.format("Entered method to save new request with requestBody %s", resetRequestDTO));

        ResetRequest resetRequest = dtOsConverter.getResetRequestFromDTO(resetRequestDTO);

        if (LocalDateTime.now().isBefore(resetRequest.getCreatedAt().plusMinutes(ResetRequest.MAX_MINUTES)) && //if now if before expiration time
                resetRequestDTO.getNewPassword().length() >= 5 && resetRequestDTO.getUsername().equals(resetRequest.getUser().getUsername())) {

            User user = resetRequest.getUser();
            user.setPassword(TokenUtils.getMD5Token(resetRequestDTO.getNewPassword()));
            userService.save(user);

            resetRequest.setCreatedAt(resetRequest.getCreatedAt().minusMinutes(60));
            resetRequestService.save(resetRequest);
            log.info("Exiting method to create a new request with status OK");
            return new ResponseEntity<>(HttpStatus.OK);
        }

        log.info("Exiting with status NOT ACCEPTABLE");
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    @RequestMapping(value = "/locations", method = PUT)
    public ResponseEntity<?> updateLocation(@RequestBody LocationDTO locationDTO, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Enter method to update new location with requestBody %s", locationDTO));
        User user = userService.getByHashKey(headers.get("token"));
        if (user.getStatus() == UserStatus.ADMIN) {
            Optional<Location> locationOptional = locationService.findById(locationDTO.getId());
            if (locationOptional.isPresent()) {
                userUtils.createEvent(userService.getByHashKey(headers.get("token")),
                        String.format("Updated location \"%s %s\"", locationDTO.getCountry(), locationDTO.getCity()),
                        UserEventType.UPDATE);
                locationService.save(dtOsConverter.getLocationFromDTO(locationDTO));
                log.info("Location has been successfully updated and saved in database");
                return new ResponseEntity<>("OK", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
