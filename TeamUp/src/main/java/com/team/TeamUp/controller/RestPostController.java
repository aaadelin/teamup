package com.team.TeamUp.controller;

import com.team.TeamUp.domain.*;
import com.team.TeamUp.domain.enums.UserEventType;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.*;
import com.team.TeamUp.persistence.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.ImageCompressor;
import com.team.TeamUp.utils.TokenUtils;
import com.team.TeamUp.utils.UserUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

//POST methods - for creating

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestPostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestPostController.class);

    private UserUtils userUtils;
    private TeamRepository teamRepository;
    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private DTOsConverter dtOsConverter;

    @Autowired
    public RestPostController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                              ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository,
                              DTOsConverter dtOsConverter, UserUtils userUtils) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.dtOsConverter = dtOsConverter;
        this.userUtils = userUtils;

        LOGGER.info("Creating RestPostController");
    }

    @RequestMapping(value = "/user", method = POST)
    public ResponseEntity<?> addUser(@RequestBody UserDTO user, @RequestHeader Map<String, String> headers) {
        userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                            String.format("Created user \"%s %s\"", user.getFirstName(), user.getLastName()),
                            UserEventType.CREATE);
        LOGGER.info(String.format("Entering method create user with user: %s and headers: %s", user, headers));
        User userToSave = dtOsConverter.getUserFromDTO(user, UserStatus.ADMIN);
        userRepository.save(userToSave);

        LOGGER.info("User has been successfully created and saved in database");
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = "/project", method = POST)
    public ResponseEntity<?> addProject(@RequestBody ProjectDTO projectDTO, @RequestHeader Map<String, String> headers) {
        userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                String.format("Created project \"%s\"", projectDTO.getName()),
                UserEventType.CREATE);
        LOGGER.info(String.format("Entering method create project with project: %s and headers: %s", projectDTO, headers));
        projectRepository.save(dtOsConverter.getProjectFromDTO(projectDTO));
        LOGGER.info("Project has been successfully created and saven in database");
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = "/task", method = POST)
    public ResponseEntity<?> addTask(@RequestBody TaskDTO taskDTO, @RequestHeader Map<String, String> headers) {
        userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                String.format("Created task \"%s\"", taskDTO.getSummary()),
                UserEventType.CREATE);
        LOGGER.info(String.format("Entering method create task with task: %s and headers: %s", taskDTO, headers));Task task = dtOsConverter.getTaskFromDTO(taskDTO, headers.get("token"));
        taskRepository.save(task);
        Post post = new Post();
        post.setTask(task);
        postRepository.save(post);
        LOGGER.info("Task has been successfully created and saved to database");
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    @RequestMapping(value = "/team", method = POST)
    public ResponseEntity<?> addTeam(@RequestBody TeamDTO team, @RequestHeader Map<String, String> headers) {
        userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                String.format("Created team \"%s\" on department \"%s\"", team.getName(), team.getDepartment()),
                UserEventType.CREATE);
        LOGGER.info(String.format("Entering method create team with team: %s and headers: %s", team, headers));
        User user = userRepository.findByHashKey(headers.get("token")).orElseThrow();
        Team newTeam = dtOsConverter.getTeamFromDTO(team, user.getStatus());
        teamRepository.save(newTeam);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = "/comment", method = POST)
    public ResponseEntity<?> addComment(@RequestBody CommentDTO commentDTO, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering method add comment with comment: %s and headers: %s", commentDTO, headers));
        if(commentDTO.getTitle() == null || commentDTO.getTitle().equals("")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                String.format("Added comment \"%s\" at task \"%s\"", commentDTO.getTitle().substring(0, Math.min(commentDTO.getTitle().length(), 30)),
                        postRepository.findById(commentDTO.getPostId())
                                .orElseThrow()
                                .getTask()
                                .getSummary()),
                UserEventType.CREATE);
        User user = userRepository.findByHashKey(headers.get("token")).orElseGet(User::new);
        commentDTO.setCreator(dtOsConverter.getDTOFromUser(user));
        Comment newComment = dtOsConverter.getCommentFromDTO(commentDTO);
        commentRepository.save(newComment);

        Post post = newComment.getPost();
        post.addComment(newComment);
        postRepository.save(post);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = "/login", method = POST)
    public ResponseEntity<?> getKeyForUser(@RequestParam Map<String, String> requestParameters) {
        LOGGER.info(String.format("Entering method to login with requested parameters: %s", requestParameters));
        String username = requestParameters.get("username");
        String password = requestParameters.get("password");

        password = new String(Base64.getDecoder().decode(password));

        LOGGER.info(String.format("Username: %s \n Password: %s", username, password));
        if (username != null) {
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

    @RequestMapping(value = "/user/{id}/photo", method = POST)
    public ResponseEntity<?> uploadPhoto(@PathVariable int id,
                                         @RequestHeader Map<String, String> headers,
                                         @RequestParam(name = "photo") MultipartFile photo
    ) throws IOException {

        if (userRepository.findById(id).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<User> userOptional = userRepository.findByHashKey(headers.get("token"));
        if(userOptional.isPresent() && (userOptional.get().getId() == id || userOptional.get().getStatus() == UserStatus.ADMIN)){
            userOptional.get().setPhoto(String.valueOf(userOptional.get().getId()));
            userRepository.save(userOptional.get());
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        LOGGER.info(String.format("Uploading photo entered with headers: %s and user id: %s", headers, id));

        String pathname_tmp = new ClassPathResource("/static/img").getFile().getAbsolutePath() + "\\" +  id + "_1";
        String pathname = new ClassPathResource("/static/img").getFile().getAbsolutePath() + "\\" +  id;
        LOGGER.info(String.format("Uploading to %s", pathname_tmp));
        File file = new File(pathname_tmp);
        try(FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(photo.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageCompressor.compressAndSave(pathname_tmp, pathname);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
