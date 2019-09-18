package com.team.TeamUp.controller;

import com.team.TeamUp.domain.*;
import com.team.TeamUp.domain.enums.UserEventType;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.*;
import com.team.TeamUp.persistence.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.MailUtils;
import com.team.TeamUp.utils.TokenUtils;
import com.team.TeamUp.utils.UserUtils;
import com.team.TeamUp.validation.UserValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

//PUT methods - for updating


@RestController
@RequestMapping("/api")
@CrossOrigin
@Slf4j
public class RestPutController{

    private UserUtils userUtils;
    private UserValidation userValidation;

    private TeamRepository teamRepository;
    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private ResetRequestRepository resetRequestRepository;
    private DTOsConverter dtOsConverter;
    private MailUtils mailUtils;


    @Autowired
    public RestPutController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                             ProjectRepository projectRepository, UserValidation userValidation, DTOsConverter dtOsConverter,
                             ResetRequestRepository resetRequestRepository, UserUtils userUtils, MailUtils mailUtils) {

        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.dtOsConverter = dtOsConverter;
        this.resetRequestRepository = resetRequestRepository;
        this.userValidation = userValidation;
        this.mailUtils = mailUtils;
        this.userUtils = userUtils;
        log.info("Creating RestPutController");
    }

    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@RequestBody UserDTO user, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering update user method with user: %s and headers: %s", user, headers));

        Optional<User> userOptional = userRepository.findById(user.getId());
        if (userOptional.isEmpty()) {
            log.info(String.format("User with id %s has not been found to update", user.getId()));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        } else {
            userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                    String.format("Updated user \"%s %s\"", userOptional.get().getFirstName(), userOptional.get().getLastName()),
                    UserEventType.UPDATE);

            User realUser = dtOsConverter.getUserFromDTO(user, UserStatus.ADMIN);
            userRepository.save(realUser);
            log.info(String.format("User with id %s has been successfully updated in database", user.getId()));
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/project", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProject(@RequestBody ProjectDTO projectDTO, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering update project with project: %s and headers: %s", projectDTO, headers));


        if (userValidation.isValid(headers, UserStatus.ADMIN) || userValidation.isOwner(headers, projectDTO)) {
            Optional<Project> projectOptional = projectRepository.findById(projectDTO.getId());
            if (projectOptional.isEmpty()) {
                log.info(String.format("Project with id %s has not been found to update", projectDTO.getId()));
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            } else {
                userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                        String.format("Updated project \"%s\"", projectOptional.get().getName()),
                        UserEventType.UPDATE);
                Project project = dtOsConverter.getProjectFromDTO(projectDTO);
                projectRepository.save(project);
                log.info(String.format("Project with id %s has been successfully updated in database", project.getId()));
                return new ResponseEntity<>("OK", HttpStatus.OK);
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
    @RequestMapping(value = "/task", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTask(@RequestBody TaskDTO taskDTO, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering update task with task: %s and headers: %s", taskDTO, headers));

        Optional<Task> taskOptional = taskRepository.findById(taskDTO.getId());
        if (taskOptional.isPresent()) {
            userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                    String.format("Updated task \"%s\"", taskOptional.get().getSummary()),
                    UserEventType.UPDATE);

            Optional<User> userOptional = userRepository.findByHashKey(headers.get("token"));
            if (userOptional.isPresent() && (
                    taskOptional.get().getAssignees().contains(userOptional.get()) ||
                            taskOptional.get().getReporter().getId() == userOptional.get().getId())) {

                Task task = dtOsConverter.getTaskFromDTOForUpdate(taskDTO, userOptional.get());
                taskRepository.save(task);
                log.info(String.format("Task with id %s has been successfully updated in database", task.getId()));
                return new ResponseEntity<>("OK", HttpStatus.OK);
            } else {
                log.error("User not eligible");
                return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
        } else {
            log.info(String.format("Task with id %s has not been found to update", taskDTO.getId()));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping(value = "/team", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTeam(@RequestBody TeamDTO team, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering update team with team: %s and headers: %s", team, headers));

        User user = userRepository.findByHashKey(headers.get("token")).orElseThrow();
        if (team.getLeaderID() == user.getId() || user.getStatus().equals(UserStatus.ADMIN)) { //only the leader or admin can change AND only the admin can change the leader

            Optional<Team> teamOptional = teamRepository.findById(team.getId());
            if (teamOptional.isEmpty()) {
                log.info(String.format("Team with id %s has not been found to update", team.getId()));
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            } else {
                userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                        String.format("Updated team \"%s\" from department \"%s\"", team.getName(), teamOptional.get().getDepartment()),
                        UserEventType.UPDATE);
                Team newTeam = dtOsConverter.getTeamFromDTO(team, user.getStatus());
                teamRepository.save(newTeam);
                log.info(String.format("Team with id %s has been successfully updated in database", newTeam.getId()));
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
        }
        log.error("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/user/{id}/password", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUserPassword(@PathVariable int id,
                                                @RequestParam Map<String, String> parameters,
                                                @RequestHeader Map<String, String> headers){
        log.info(String.format("Entered changing password with id %s, parameters %s and headers %s", id, parameters, headers));

        User requester = userRepository.findByHashKey(headers.get("token")).orElseThrow();
        User userToChange = userRepository.findById(id).orElseThrow();

        if(parameters.get("newPassword").length() < 6) {
            log.info("New password is too short");
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        String oldPassword = TokenUtils.getMD5Token(parameters.get("oldPassword")).toLowerCase();
        String newPassword = TokenUtils.getMD5Token(parameters.get("newPassword")).toLowerCase();

        if(requester.getId() == userToChange.getId() || requester.getStatus() == UserStatus.ADMIN){

            if(userToChange.getPassword().toLowerCase().equals(oldPassword)){
                userToChange.setPassword(newPassword);
                log.info("Password successfully changed");
                if(parameters.get("logout").equals("true")){
                    userToChange.setActive(false);
                    log.info("User has been logged out");
                }
                userRepository.save(userToChange);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            log.info("Passwords don't match");
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }else{
            log.info("User not eligible");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    @RequestMapping(value = "/requests", method = PUT)
    public ResponseEntity<?> updateRequest(@RequestBody ResetRequestDTO resetRequestDTO){
        log.info(String.format("Entered method to save new request with requestBody %s", resetRequestDTO));

        ResetRequest resetRequest = dtOsConverter.getResetRequestFromDTO(resetRequestDTO);

        if(LocalDateTime.now().isBefore(resetRequest.getCreatedAt().plusMinutes(ResetRequest.MAX_MINUTES)) && //if now if before expiration time
                resetRequestDTO.getNewPassword().length() >= 5 && resetRequestDTO.getUsername().equals(resetRequest.getUser().getUsername())) {

            User user = resetRequest.getUser();
            user.setPassword(TokenUtils.getMD5Token(resetRequestDTO.getNewPassword()));
            userRepository.save(user);

            resetRequest.setCreatedAt(resetRequest.getCreatedAt().minusMinutes(60));
            resetRequestRepository.save(resetRequest);
        }

        log.info("Exiting method to create a new request with status OK");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
