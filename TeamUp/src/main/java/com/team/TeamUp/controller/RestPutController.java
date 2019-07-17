package com.team.TeamUp.controller;

import com.team.TeamUp.domain.Project;
import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.Team;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.dtos.UserDTO;
import com.team.TeamUp.persistance.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

//PUT methods - for updating


@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestPutController extends AbstractRestController{


    public RestPutController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository, ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository) {
        super(teamRepository, userRepository, taskRepository, projectRepository, commentRepository, postRepository);
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public ResponseEntity updateUser(@RequestBody UserDTO user){
        Optional<User> userOptional = userRepository.findById(user.getId());
        if(userOptional.isEmpty()){
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }else{
            User realUser = dtOsConverter.getUserFromDTO(user);
            userRepository.save(realUser);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/project", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProject(@RequestBody ProjectDTO projectDTO){ //TODO: check if owner or admin
        Optional<Project> projectOptional = projectRepository.findById(projectDTO.getId());
        if(projectOptional.isEmpty()){
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }else{
            Project project = dtOsConverter.getProjectFromDTO(projectDTO);
            projectRepository.save(project);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/task", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTask(@RequestBody TaskDTO taskDTO){
        Optional<Task> taskOptional = taskRepository.findById(taskDTO.getId());
        if(taskOptional.isEmpty()){
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }else{
            Task task = dtOsConverter.getTaskFromDTO(taskDTO);
            taskRepository.save(task);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/team", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTeam(@RequestBody Team team){ //TODO: teamDTO
        Optional<Team> teamOptional = teamRepository.findById(team.getId());
        if(teamOptional.isEmpty()){
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }else{
            teamRepository.save(team);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
    }


}
