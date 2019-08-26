package com.team.TeamUp.controller;

import com.team.TeamUp.domain.Project;
import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.Team;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.UserEventType;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.dtos.TeamDTO;
import com.team.TeamUp.dtos.UserDTO;
import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TokenUtils;
import com.team.TeamUp.utils.UserUtils;
import com.team.TeamUp.validation.UserValidation;
import org.apache.tomcat.util.security.MD5Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

//PUT methods - for updating


@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestPutController extends AbstractRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestGetController.class);
    @Autowired
    private UserUtils userUtils;

    public RestPutController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                             ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository,
                             UserValidation userValidation, DTOsConverter dtOsConverter, UserEventRepository userEventRepository) {
        super(teamRepository, userRepository, taskRepository, projectRepository, commentRepository, postRepository, userValidation, dtOsConverter, userEventRepository);
        LOGGER.info("Creating RestPutController");
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@RequestBody UserDTO user, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering update user method with user: %s and headers: %s", user, headers));

        Optional<User> userOptional = userRepository.findById(user.getId());
        if (userOptional.isEmpty()) {
            LOGGER.info(String.format("User with id %s has not been found to update", user.getId()));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        } else {
            userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                    String.format("Updated user \"%s %s\"", userOptional.get().getFirstName(), userOptional.get().getLastName()),
                    UserEventType.UPDATE);

            User realUser = dtOsConverter.getUserFromDTO(user, UserStatus.ADMIN);
            userRepository.save(realUser);
            LOGGER.info(String.format("User with id %s has been successfully updated in database", user.getId()));
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/project", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProject(@RequestBody ProjectDTO projectDTO, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering update project with project: %s and headers: %s", projectDTO, headers));


        if (userValidation.isValid(headers, UserStatus.ADMIN) || userValidation.isOwner(headers, projectDTO)) {
            Optional<Project> projectOptional = projectRepository.findById(projectDTO.getId());
            if (projectOptional.isEmpty()) {
                LOGGER.info(String.format("Project with id %s has not been found to update", projectDTO.getId()));
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            } else {
                userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                        String.format("Updated project \"%s\"", projectOptional.get().getName()),
                        UserEventType.UPDATE);
                Project project = dtOsConverter.getProjectFromDTO(projectDTO);
                projectRepository.save(project);
                LOGGER.info(String.format("Project with id %s has been successfully updated in database", project.getId()));
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
        }
        LOGGER.error("User not eligible");
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
        LOGGER.info(String.format("Entering update task with task: %s and headers: %s", taskDTO, headers));

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
                LOGGER.info(String.format("Task with id %s has been successfully updated in database", task.getId()));
                return new ResponseEntity<>("OK", HttpStatus.OK);
            } else {
                LOGGER.error("User not eligible");
                return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
        } else {
            LOGGER.info(String.format("Task with id %s has not been found to update", taskDTO.getId()));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping(value = "/team", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTeam(@RequestBody TeamDTO team, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering update team with team: %s and headers: %s", team, headers));

        User user = userRepository.findByHashKey(headers.get("token")).get();
        if (team.getLeaderID() == user.getId() || user.getStatus().equals(UserStatus.ADMIN)) { //only the leader or admin can change AND only the admin can change the leader

            Optional<Team> teamOptional = teamRepository.findById(team.getId());
            if (teamOptional.isEmpty()) {
                LOGGER.info(String.format("Team with id %s has not been found to update", team.getId()));
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            } else {
                userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                        String.format("Updated team \"%s\" from department \"%s\"", team.getName(), teamOptional.get().getDepartment()),
                        UserEventType.UPDATE);
                Team newTeam = dtOsConverter.getTeamFromDTO(team, user.getStatus());
                teamRepository.save(newTeam);
                LOGGER.info(String.format("Team with id %s has been successfully updated in database", newTeam.getId()));
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
        }
        LOGGER.error("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/user/{id}/password", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUserPassword(@PathVariable int id,
                                                @RequestParam Map<String, String> parameters,
                                                @RequestHeader Map<String, String> headers){
        LOGGER.info(String.format("Entered changing password with id %s, parameters %s and headers %s", id, parameters, headers));

        User requester = userRepository.findByHashKey(headers.get("token")).orElseThrow();
        User userToChange = userRepository.findById(id).orElseThrow();

        if(parameters.get("newPassword").length() < 6) {
            LOGGER.info("New password is too short");
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        String oldPassword = TokenUtils.getMD5Token(parameters.get("oldPassword")).toLowerCase();
        String newPassword = TokenUtils.getMD5Token(parameters.get("newPassword")).toLowerCase();

        if(requester.getId() == userToChange.getId() || requester.getStatus() == UserStatus.ADMIN){

            if(userToChange.getPassword().toLowerCase().equals(oldPassword)){
                userToChange.setPassword(newPassword);
                LOGGER.info("Password successfully changed");
                if(parameters.get("logout").equals("true")){
                    userToChange.setActive(false);
                    LOGGER.info("User has been logged out");
                }
                userRepository.save(userToChange);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            LOGGER.info("Passwords don't match");
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }else{
            LOGGER.info("User not eligible");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
