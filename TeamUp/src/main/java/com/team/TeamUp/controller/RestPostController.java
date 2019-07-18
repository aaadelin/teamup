package com.team.TeamUp.controller;

import com.team.TeamUp.domain.Team;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.dtos.TeamDTO;
import com.team.TeamUp.dtos.UserDTO;
import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.TokenUtils;
import com.team.TeamUp.utils.UserValidationUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

//POST methods - for creating

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestPostController extends AbstractRestController {

    public RestPostController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                              ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository,
                              UserValidationUtils userValidationUtils) {
        super(teamRepository, userRepository, taskRepository, projectRepository, commentRepository, postRepository, userValidationUtils);
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<?> addUser(@RequestBody UserDTO user, @RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers, UserStatus.ADMIN)) {
            User userToSave = dtOsConverter.getUserFromDTO(user);
            userRepository.save(userToSave);

            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error: Forbidden", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/project", method = RequestMethod.POST)
    public ResponseEntity<?> addProject(@RequestBody ProjectDTO projectDTO, @RequestHeader Map<String, String> headers) { //TODO: check if admin
        if (userValidationUtils.isValid(headers, UserStatus.ADMIN)) {
            projectRepository.save(dtOsConverter.getProjectFromDTO(projectDTO));
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/task", method = RequestMethod.POST)
    public ResponseEntity<?> addTask(@RequestBody TaskDTO taskDTO, @RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            taskRepository.save(dtOsConverter.getTaskFromDTO(taskDTO));
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }


    @RequestMapping(value = "/team", method = RequestMethod.POST)
    public ResponseEntity<?> addTeam(@RequestBody TeamDTO team, @RequestHeader Map<String, String> headers) {

        if (userValidationUtils.isValid(headers, UserStatus.ADMIN)) {
            Team newTeam = dtOsConverter.getTeamFromDTO(team);
            teamRepository.save(newTeam);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> getKeyForUser(HttpServletRequest request, @RequestParam Map<String, String> requestParameters) {
        String username = requestParameters.get("username");
        String password = requestParameters.get("password");

        if (username != null && password != null) {
            password = TokenUtils.getMD5Token(password);

            Optional<User> user = userRepository.findByUsernameAndPassword(username, password);
            if (user.isPresent()) {
                boolean isAdmin = false;

                User realUser = user.get();
                realUser.setActive(true);
                realUser.setLastActive(LocalDateTime.now());

                if (user.get().getStatus().equals(UserStatus.ADMIN)) {
                    isAdmin = true;
                }

                userRepository.save(realUser);

                JSONObject answer = new JSONObject();
                answer.put("key", user.get().getHashKey());
                answer.put("isAdmin", isAdmin);

                return new ResponseEntity<>(answer.toString(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }
}
