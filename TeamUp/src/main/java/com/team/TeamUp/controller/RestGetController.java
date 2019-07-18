package com.team.TeamUp.controller;

import com.team.TeamUp.domain.*;
import com.team.TeamUp.domain.enums.Department;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.domain.enums.TaskType;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.dtos.TeamDTO;
import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.UserValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestGetController extends AbstractRestController {

    public RestGetController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                             ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository,
                             UserValidationUtils userValidationUtils) {
        super(teamRepository, userRepository, taskRepository, projectRepository, commentRepository, postRepository, userValidationUtils);
    }

    //GET methods - for selecting

    @RequestMapping(value = "/users", method = GET)
    public ResponseEntity<?> getAllUsers(@RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            return new ResponseEntity<>(userRepository.findAll().stream().map(user -> dtOsConverter.getDTOFromUser(user)).collect(Collectors.toList()), HttpStatus.OK);
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/teams", method = GET)
    public ResponseEntity<?> getAllTeams(@RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            return new ResponseEntity<>(teamRepository.findAll().stream().map(team -> dtOsConverter.getDTOFromTeam(team)).collect(Collectors.toList()), HttpStatus.OK);
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/tasks", method = GET)
    public ResponseEntity<?> getAllTasks(@RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            return new ResponseEntity<>(taskRepository.findAll().stream().map(task -> dtOsConverter.getDTOFromTask(task)).collect(Collectors.toList()), HttpStatus.OK);
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/projects", method = GET)
    public ResponseEntity<?> getAllProjects(@RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            return new ResponseEntity<>(projectRepository.findAll(), HttpStatus.OK);//.stream().map(project -> dtOsConverter.getDTOFromProject(project)).collect(Collectors.toList());
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/posts", method = GET)
    public ResponseEntity<?> getAllPosts(@RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            return new ResponseEntity<>(postRepository.findAll(), HttpStatus.OK);//.stream().map(project -> dtOsConverter.getDTOFromProject(project)).collect(Collectors.toList());
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/comments", method = GET)
    public ResponseEntity<?> getAllComments(@RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            return new ResponseEntity<>(commentRepository.findAll(), HttpStatus.OK);//.stream().map(project -> dtOsConverter.getDTOFromProject(project)).collect(Collectors.toList());
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/departments", method = GET)
    public ResponseEntity<?> getAllDepartments(@RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            return new ResponseEntity<>(Arrays.asList(Department.values()), HttpStatus.OK);
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/task-status", method = GET)
    public ResponseEntity<?> getAllTaskStatus(@RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            return new ResponseEntity<>(Arrays.asList(TaskStatus.values()), HttpStatus.OK);
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/task-types", method = GET)
    public ResponseEntity<?> getAllTaskTypes(@RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            return new ResponseEntity<>(Arrays.asList(TaskType.values()), HttpStatus.OK);
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/user-status", method = GET)
    public ResponseEntity<?> getAllUserStatus(@RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            return new ResponseEntity<>(Arrays.asList(UserStatus.values()), HttpStatus.OK);
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/post/{postid}/comments", method = GET)
    public ResponseEntity<?> getAllPostComments(@PathVariable int postid, @RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            Optional<Post> post = postRepository.findById(postid);
            if (post.isPresent()) {
                return new ResponseEntity<>(post.get().getComments(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    ///Getter based on ids
    @RequestMapping(value = "/user/{id}", method = GET)
    public ResponseEntity<?> getUserById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                return new ResponseEntity<>(dtOsConverter.getDTOFromUser(userOptional.get()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/team/{id}", method = GET)
    public ResponseEntity<?> getTeamById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            Optional<Team> teamOptional = teamRepository.findById(id);
            if (teamOptional.isPresent()) {
                return new ResponseEntity<>(dtOsConverter.getDTOFromTeam(teamOptional.get()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/task/{id}", method = GET)
    public ResponseEntity<?> getTaskById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers)) {
            Optional<Task> taskOptional = taskRepository.findById(id);
            if (taskOptional.isPresent()) {
                return new ResponseEntity<>(taskOptional, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/project/{id}", method = GET)
    public ResponseEntity<?> getProjectById(@PathVariable int id, @RequestHeader Map<String, String>headers) {
        if (userValidationUtils.isValid(headers)) {
            Optional<Project> projectOptional = projectRepository.findById(id);
            if (projectOptional.isPresent()) {
                return new ResponseEntity<>(dtOsConverter.getDTOFromProject(projectOptional.get()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/post/taskid={id}", method = GET)
    public ResponseEntity<?> getPostByTaskId(@PathVariable int id, @RequestHeader Map<String, String > headers) {
        if (userValidationUtils.isValid(headers)) {
            Optional<Task> taskOptional = taskRepository.findById(id);

            if (taskOptional.isPresent()) {
                Optional<Post> postOptional = postRepository.findByTask(taskOptional.get());
                if (postOptional.isPresent()) {
                    return new ResponseEntity<>(postOptional.get(), HttpStatus.OK);
                } else {
                    Post post = new Post();
                    post.setTask(taskOptional.get());

                    post = postRepository.save(post);

                    return new ResponseEntity<>(post, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/logout", method = GET)
    public ResponseEntity<?> logout(@RequestHeader Map<String, String> headers) {
        String key = headers.get("key");
        Optional<User> userOptional = userRepository.findByHashKey(key);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(false);
            user.setLastActive(LocalDateTime.now());

            userRepository.save(user);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }
}

