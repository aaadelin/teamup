package com.team.TeamUp.controller;

import com.team.TeamUp.domain.Project;
import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.Team;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.dtos.TeamDTO;
import com.team.TeamUp.dtos.UserDTO;
import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.UserValidationUtils;
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


    public RestPutController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                             ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository,
                             UserValidationUtils userValidationUtils) {
        super(teamRepository, userRepository, taskRepository, projectRepository, commentRepository, postRepository, userValidationUtils);
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@RequestBody UserDTO user, @RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers, UserStatus.ADMIN)) {
            Optional<User> userOptional = userRepository.findById(user.getId());
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            } else {
                User realUser = dtOsConverter.getUserFromDTO(user);
                userRepository.save(realUser);
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("FORBIIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/project", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProject(@RequestBody ProjectDTO projectDTO, @RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers, UserStatus.ADMIN) || userValidationUtils.isOwner(headers, projectDTO)) {
            Optional<Project> projectOptional = projectRepository.findById(projectDTO.getId());
            if (projectOptional.isEmpty()) {
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            } else {
                Project project = dtOsConverter.getProjectFromDTO(projectDTO);
                projectRepository.save(project);
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    /**
     *
     * only the admin, reporter or assignee can change a task
     *
     * @param taskDTO TaskDTO instance send from user
     * @param headers request headers containing token
     * @return response ok, not_found, Forbidden
     */
    @RequestMapping(value = "/task", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTask(@RequestBody TaskDTO taskDTO, @RequestHeader Map<String, String> headers) {
        if(userValidationUtils.isValid(headers)){
            Optional<User> userOptional = userRepository.findByHashKey(headers.get("token"));
            if(taskDTO.getAssignees().contains(userOptional.get().getId()) ||
                taskDTO.getReporterID() == userOptional.get().getId()){

                Optional<Task> taskOptional = taskRepository.findById(taskDTO.getId());
                if (taskOptional.isEmpty()) {
                    return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
                } else {
                    Task task = dtOsConverter.getTaskFromDTO(taskDTO);
                    taskRepository.save(task);
                    return new ResponseEntity<>("OK", HttpStatus.OK);
                }

            }
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/team", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTeam(@RequestBody TeamDTO team, @RequestHeader Map<String, String> headers) {
        if(userValidationUtils.isValid(headers)){
            User user = userRepository.findByHashKey(headers.get("token")).get();
            if (team.getLeaderID() == user.getId() || user.getStatus().equals(UserStatus.ADMIN)){ //only the leader or admin can change AND only the admin can change the leader

                Optional<Team> teamOptional = teamRepository.findById(team.getId());
                if (teamOptional.isEmpty()) {
                    return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
                } else {
                    Team newTeam = dtOsConverter.getTeamFromDTO(team, user.getStatus());
                    teamRepository.save(newTeam);
                    return new ResponseEntity<>("OK", HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

}
