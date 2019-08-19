package com.team.TeamUp.controller;

import com.team.TeamUp.domain.*;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.*;
import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TokenUtils;
import com.team.TeamUp.validation.UserValidation;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

//POST methods - for creating

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestPostController extends AbstractRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestGetController.class);

    public RestPostController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                              ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository,
                              UserValidation userValidation, DTOsConverter dtOsConverter) {
        super(teamRepository, userRepository, taskRepository, projectRepository, commentRepository, postRepository, userValidation, dtOsConverter);
        LOGGER.info("Creating RestPostController");
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<?> addUser(@RequestBody UserDTO user, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering method create user with user: %s and headers: %s", user, headers));
        if (userValidation.isValid(headers, UserStatus.ADMIN)) {
            User userToSave = dtOsConverter.getUserFromDTO(user, UserStatus.ADMIN);
            userRepository.save(userToSave);

            LOGGER.info("User has been successfully created and saved in database");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        LOGGER.error("User not eligible");
        return new ResponseEntity<>("Error: Forbidden", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/project", method = RequestMethod.POST)
    public ResponseEntity<?> addProject(@RequestBody ProjectDTO projectDTO, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering method create project with project: %s and headers: %s", projectDTO, headers));
        if (userValidation.isValid(headers, UserStatus.ADMIN)) {
            projectRepository.save(dtOsConverter.getProjectFromDTO(projectDTO));
            LOGGER.info("Project has been successfully created and saven in database");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        LOGGER.error("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/task", method = RequestMethod.POST)
    public ResponseEntity<?> addTask(@RequestBody TaskDTO taskDTO, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering method create task with task: %s and headers: %s", taskDTO, headers));
        if (userValidation.isValid(headers)) {
            Task task = dtOsConverter.getTaskFromDTO(taskDTO, headers.get("token"));
            taskRepository.save(task);
            Post post = new Post();
            post.setTask(task);
            postRepository.save(post);
            LOGGER.info("Task has been successfully created and saved to database");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        LOGGER.error("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }


    @RequestMapping(value = "/team", method = RequestMethod.POST)
    public ResponseEntity<?> addTeam(@RequestBody TeamDTO team, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering method create team with team: %s and headers: %s", team, headers));
        if (userValidation.isValid(headers, UserStatus.ADMIN)) {
            User user = userRepository.findByHashKey(headers.get("token")).orElseGet(User::new);
            Team newTeam = dtOsConverter.getTeamFromDTO(team, user.getStatus());
            teamRepository.save(newTeam);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        LOGGER.error("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public ResponseEntity<?> addComment(@RequestBody CommentDTO commentDTO, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering method add comment with comment: %s and headers: %s", commentDTO, headers));
        if (userValidation.isValid(headers)) {
            User user = userRepository.findByHashKey(headers.get("token")).orElseGet(User::new);
            commentDTO.setCreator(dtOsConverter.getDTOFromUser(user));
            Comment newComment = dtOsConverter.getCommentFromDTO(commentDTO);
            commentRepository.save(newComment);

            Post post = newComment.getPost();
            post.addComment(newComment);
            postRepository.save(post);

            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        LOGGER.error("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> getKeyForUser(HttpServletRequest request, @RequestParam Map<String, String> requestParameters) {
        LOGGER.info(String.format("Entering method to login with requested parameters: %s", requestParameters));
        String username = requestParameters.get("username");
        String password = requestParameters.get("password");

        password = new String(Base64.getDecoder().decode(password));

        LOGGER.info(String.format("Username: %s \n Password: %s", username, password));
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
                LOGGER.info("User's status has been saved to database as active");

                JSONObject answer = new JSONObject();
                answer.put("key", user.get().getHashKey());
                answer.put("isAdmin", isAdmin);
                answer.put("name", user.get().getFirstName() + " " + user.get().getLastName());
                LOGGER.info(String.format("User has been successfully logged in and key sent :%s", user.get().getHashKey()));
                return new ResponseEntity<>(answer.toString(), HttpStatus.OK);
            } else {
                LOGGER.info("User with specified credentials has not been found");
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        }
        LOGGER.error("User not eligible");
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }
}
