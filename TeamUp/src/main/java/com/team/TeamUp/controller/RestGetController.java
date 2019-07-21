package com.team.TeamUp.controller;

import com.team.TeamUp.domain.*;
import com.team.TeamUp.domain.enums.Department;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.domain.enums.TaskType;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.*;
import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.UserValidationUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestGetController extends AbstractRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestGetController.class);

    public RestGetController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                             ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository,
                             UserValidationUtils userValidationUtils) {
        super(teamRepository, userRepository, taskRepository, projectRepository, commentRepository, postRepository, userValidationUtils);
        LOGGER.info("Creating RestGetController");
    }

    //GET methods - for selecting

    @RequestMapping(value = "/users", method = GET)
    public ResponseEntity<?> getAllUsers(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all users method with headers: %s", headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            List<UserDTO> users = userRepository.findAll().stream().map(user -> dtOsConverter.getDTOFromUser(user)).collect(Collectors.toList());
            LOGGER.info(String.format("Returning list: %s", users.toString()));
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        LOGGER.error("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/teams", method = GET)
    public ResponseEntity<?> getAllTeams(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all teams method with headers: %s", headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            List<TeamDTO> teams = teamRepository.findAll().stream().map(team -> dtOsConverter.getDTOFromTeam(team)).collect(Collectors.toList());
            LOGGER.info(String.format("Returning list of teams: %s", teams.toString()));
            return new ResponseEntity<>(teams, HttpStatus.OK);
        }
        LOGGER.error("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/tasks", method = GET)
    public ResponseEntity<?> getAllTasks(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all tasks method with headers: %s", headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            List<TaskDTO> tasks = taskRepository.findAll().stream().map(task -> dtOsConverter.getDTOFromTask(task)).collect(Collectors.toList());
            LOGGER.info(String.format("Returning list of tasks: %s", tasks.toString()));
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        }
        LOGGER.error("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/projects", method = GET)
    public ResponseEntity<?> getAllProjects(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all projects method with headers: %s", headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            List<ProjectDTO> projects = projectRepository.findAll().stream().map(project -> dtOsConverter.getDTOFromProject(project)).collect(Collectors.toList());
            LOGGER.info(String.format("Returning list of projects: %s", projects));
            return new ResponseEntity<>(projects, HttpStatus.OK);
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/posts", method = GET)
    public ResponseEntity<?> getAllPosts(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all posts method with headers: %s", headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            List<PostDTO> posts = postRepository.findAll().stream().map(post -> dtOsConverter.getDTOFromPost(post)).collect(Collectors.toList());
            LOGGER.info(String.format("Returning list of posts: %s", posts.toString()));
            return new ResponseEntity<>(posts, HttpStatus.OK);
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/comments", method = GET)
    public ResponseEntity<?> getAllComments(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all comments method with headers: %s", headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            List<Comment> comments = commentRepository.findAll(); //.stream().map(project -> dtOsConverter.getDTOFromProject(project)).collect(Collectors.toList());
            LOGGER.info(String.format("Returning list of comments: %s", comments.toString()));
            return new ResponseEntity<>(comments, HttpStatus.OK);
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/departments", method = GET)
    public ResponseEntity<?> getAllDepartments(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all departments method with headers: %s", headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            List<Department> departments = Arrays.asList(Department.values());
            LOGGER.info(String.format("Returning current department types: %s", departments.toString()));
            return new ResponseEntity<>(departments, HttpStatus.OK);
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/task-status", method = GET)
    public ResponseEntity<?> getAllTaskStatus(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all task status types method with headers: %s", headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            List<TaskStatus> taskStatuses = Arrays.asList(TaskStatus.values());
            LOGGER.info(String.format("Returning current possible task statuses: %s", taskStatuses.toString()));
            return new ResponseEntity<>(taskStatuses, HttpStatus.OK);
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/task-types", method = GET)
    public ResponseEntity<?> getAllTaskTypes(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all possible task types method with headers: %s", headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            List<TaskType> taskTypes = Arrays.asList(TaskType.values());
            LOGGER.info(String.format("Returning current possible task types: %s", taskTypes.toString()));
            return new ResponseEntity<>(taskTypes, HttpStatus.OK);
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/user-status", method = GET)
    public ResponseEntity<?> getAllUserStatus(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all possible user statuses method with headers: %s", headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            List<UserStatus> userStatuses = Arrays.asList(UserStatus.values());
            LOGGER.info(String.format("Returning current user statuses: %s", userStatuses.toString()));
            return new ResponseEntity<>(userStatuses, HttpStatus.OK);
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/post/{postid}/comments", method = GET)
    public ResponseEntity<?> getAllPostComments(@PathVariable int postid, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all post's comments method with postId: %d /n and headers: %s", postid, headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            Optional<Post> post = postRepository.findById(postid);
            if (post.isPresent()) {
                PostDTO postDTO = dtOsConverter.getDTOFromPost(post.get());
                LOGGER.info(String.format("Returning post: %s", postDTO));
                return new ResponseEntity<>(postDTO.getComments(), HttpStatus.OK);
            } else {
                LOGGER.info(String.format("No post found with id %d", postid));
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping( value = "/user/{key}/assigned-tasks", method = GET)
    public ResponseEntity<?> getAssignedTasksForUser(@PathVariable String key, @RequestHeader Map<String, String> headers){
        LOGGER.info(String.format("Entering get user's assigned tasks with key %s and headers %s", key, headers));
        if(userValidationUtils.isValid(headers)){
            Optional<User> userOptional = userRepository.findByHashKey(key);
            if(userOptional.isPresent()){
                LOGGER.info(String.format("Acquiring tasks assigned to user %s ", userOptional.get()));
                List<Task> allTasks = taskRepository.findAll();

                List<TaskDTO> filteredTasks = allTasks.stream()
                        .filter(task -> task.getAssignees().contains(userOptional.get()))
                        .map(task -> dtOsConverter.getDTOFromTask(task)).collect(Collectors.toList());
                LOGGER.info(String.format("Returning list of tasks: %s", filteredTasks));
                return new ResponseEntity<>(filteredTasks, HttpStatus.OK);
            }
            LOGGER.info(String.format("No user found with key %s", key));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping( value = "/user/{key}/reported-tasks", method = GET)
    public ResponseEntity<?> getReportedTasksByUser(@PathVariable String key, @RequestHeader Map<String, String> headers){
        LOGGER.info(String.format("Entering get reported tasks by user with key %s and headers %s", key, headers));
        if(userValidationUtils.isValid(headers)){
            Optional<User> userOptional = userRepository.findByHashKey(key);
            if(userOptional.isPresent()){
                LOGGER.info(String.format("Acquiring tasks reported by user %s ", userOptional.get()));
                List<Task> allTasks = taskRepository.findAll();

                List<TaskDTO> filteredTasks = allTasks.stream()
                        .filter(task -> task.getReporter().equals(userOptional.get()))
                        .map(task -> dtOsConverter.getDTOFromTask(task)).collect(Collectors.toList());
                LOGGER.info(String.format("Returning list of tasks: %s", filteredTasks));
                return new ResponseEntity<>(filteredTasks, HttpStatus.OK);
            }
            LOGGER.info(String.format("No user found with key %s", key));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    ///Getter based on ids
    @RequestMapping(value = "/user/{id}", method = GET)
    public ResponseEntity<?> getUserById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get user by id method with userId: %d /n and headers: %s", id, headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                LOGGER.info(String.format("Returning user: %s", userOptional.get().toString()));
                return new ResponseEntity<>(dtOsConverter.getDTOFromUser(userOptional.get()), HttpStatus.OK);
            } else {
                LOGGER.info(String.format("No user found with id %s", id));
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/team/{id}", method = GET)
    public ResponseEntity<?> getTeamById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get team by id method with teamId: %d /n and headers: %s", id, headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            Optional<Team> teamOptional = teamRepository.findById(id);
            if (teamOptional.isPresent()) {
                LOGGER.info(String.format("Returning team: %s", teamOptional.get().toString()));
                return new ResponseEntity<>(dtOsConverter.getDTOFromTeam(teamOptional.get()), HttpStatus.OK);
            } else {
                LOGGER.info(String.format("No team found with id %d", id));
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/task/{id}", method = GET)
    public ResponseEntity<?> getTaskById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get task by id method with taskId: %d /n and headers: %s", id, headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            Optional<Task> taskOptional = taskRepository.findById(id);
            if (taskOptional.isPresent()) {
                TaskDTO task = dtOsConverter.getDTOFromTask(taskOptional.get());
                LOGGER.info(String.format("Returning task: %s", task.toString()));
                return new ResponseEntity<>(task, HttpStatus.OK);
            } else {
                LOGGER.info(String.format("No task found with id %s", id));
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/project/{id}", method = GET)
    public ResponseEntity<?> getProjectById(@PathVariable int id, @RequestHeader Map<String, String>headers) {
        LOGGER.info(String.format("Entering get project by id method with projectId: %d /n and headers: %s", id, headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            Optional<Project> projectOptional = projectRepository.findById(id);
            if (projectOptional.isPresent()) {
                LOGGER.info(String.format("Returning project %s", projectOptional.get().toString()));
                return new ResponseEntity<>(dtOsConverter.getDTOFromProject(projectOptional.get()), HttpStatus.OK);
            } else {
                LOGGER.info("No project found");
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/key", method = GET)
    public ResponseEntity<?> getIdForCurrentUser(@RequestHeader Map<String, String>headers) {
        LOGGER.info(String.format("Entering get key for current user method with headers: %s", headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            Optional<User> userOptional = userRepository.findByHashKey(headers.get("token"));
            if (userOptional.isPresent()) {
                LOGGER.info(String.format("Returning user id %s", userOptional.get().getId()));
                return new ResponseEntity<>(userOptional.get().getId(), HttpStatus.OK);
            } else {
                LOGGER.info("No user found");
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/post/taskid={id}", method = GET)
    public ResponseEntity<?> getPostByTaskId(@PathVariable int id, @RequestHeader Map<String, String > headers) {
        LOGGER.info(String.format("Entering get post by taskId method with taskId: %d /n and headers: %s", id, headers.toString()));
        if (userValidationUtils.isValid(headers)) {
            Optional<Task> taskOptional = taskRepository.findById(id);

            if (taskOptional.isPresent()) {
                Optional<Post> postOptional = postRepository.findByTask(taskOptional.get());
                if (postOptional.isPresent()) {
                    PostDTO postDTO = dtOsConverter.getDTOFromPost(postOptional.get());
                    LOGGER.info(String.format("Returning post: %s", postDTO));
                    return new ResponseEntity<>(postDTO, HttpStatus.OK);
                } else {
                    LOGGER.info("Task doesn't have any post yet. Creating an empty one and returning it");
                    Post post = new Post();
                    post.setTask(taskOptional.get());

                    post = postRepository.save(post);
                    LOGGER.info(String.format("Returning post: %s", post.toString()));
                    return new ResponseEntity<>(post, HttpStatus.OK);
                }
            } else {
                LOGGER.info(String.format("No task found with id %s", id));
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/logout", method = GET)
    public ResponseEntity<?> logout(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering logout method with headers: %s", headers.toString()));
        String key = headers.get("key");
        Optional<User> userOptional = userRepository.findByHashKey(key);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(false);
            user.setLastActive(LocalDateTime.now());

            userRepository.save(user);
            LOGGER.info("User status saved in database");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    // images

    @RequestMapping(value = "/user/{id}/photo", method = GET, produces = "image/jpg")
    public ResponseEntity<?> getFile(@PathVariable("id") Integer id, @RequestHeader Map<String, String> headers) throws IOException {
        LOGGER.info(String.format("Entering get photo for user method with id: %s and headers: %s", id, headers));
        if(userValidationUtils.isValid(headers)){
            Optional<User> userOptional = userRepository.findById(id);

            if(userOptional.isPresent() && userOptional.get().getPhoto() == null){
                File file = new ClassPathResource("static/img/avatar.png").getFile();
                byte[] bytes = Files.readAllBytes(file.toPath());
                String encodedString = Base64.getEncoder().encodeToString(bytes);
                LOGGER.info("Exited with default image");
                return new ResponseEntity<>(encodedString, HttpStatus.OK);
            }else if(userOptional.isPresent()){
                File file = new ClassPathResource("static/img/" + userOptional.get().getPhoto()).getFile();
                byte[] bytes = Files.readAllBytes(file.toPath());
                LOGGER.info(String.format("Exited with photo with path %s", userOptional.get().getPhoto()));
                String encodedString = Base64.getEncoder().encodeToString(bytes);
                return new ResponseEntity<>(encodedString, HttpStatus.OK);
            }else{
                LOGGER.info(String.format("No user found with id %s", id));
                return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
            }
        }
        LOGGER.info("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }
}

