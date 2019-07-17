package com.team.TeamUp.controller;

import com.team.TeamUp.domain.*;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.dtos.TeamDTO;
import com.team.TeamUp.dtos.UserDTO;
import com.team.TeamUp.persistance.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestGetController extends AbstractRestController{

    public RestGetController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                             ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository) {
        super(teamRepository, userRepository, taskRepository, projectRepository, commentRepository, postRepository);
    }

    //GET methods - for selecting

    @RequestMapping(value = "/users", method = GET)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(user -> dtOsConverter.getDTOFromUser(user)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/teams", method = GET)
    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll().stream().map(team -> dtOsConverter.getDTOFromTeam(team)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/tasks", method = GET)
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream().map(task-> dtOsConverter.getDTOFromTask(task)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/projects", method = GET)
    public List<Project> getAllProjects() {
        return projectRepository.findAll();//.stream().map(project -> dtOsConverter.getDTOFromProject(project)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/posts", method = GET)
    public List<Post> getAllPosts() {
        return postRepository.findAll();//.stream().map(project -> dtOsConverter.getDTOFromProject(project)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/comments", method = GET)
    public List<Comment> getAllComments() {
        return commentRepository.findAll();//.stream().map(project -> dtOsConverter.getDTOFromProject(project)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/post/{postid}/comments", method = GET)
    public ResponseEntity<?> getAllPostComments(@PathVariable int postid) {
        Optional<Post> post = postRepository.findById(postid);
        if(post.isPresent()){
            return new ResponseEntity<>(post.get().getComments(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    ///Getter based on ids
    @RequestMapping(value = "/user/{id}", method = GET)
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            return new ResponseEntity<>(dtOsConverter.getDTOFromUser(userOptional.get()), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/team/{id}", method = GET)
    public ResponseEntity<?> getTeamById(@PathVariable int id) {
        Optional<Team> teamOptional = teamRepository.findById(id);
        if(teamOptional.isPresent()){
            return new ResponseEntity<>(dtOsConverter.getDTOFromTeam(teamOptional.get()), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/task/{id}", method = GET)
    public ResponseEntity<?> getTaskById(@PathVariable int id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if(taskOptional.isPresent()){
            return new ResponseEntity<>(taskOptional, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/project/{id}", method = GET)
    public ResponseEntity<?> getProjectById(@PathVariable int id) {
        Optional<Project> projectOptional= projectRepository.findById(id);
        if(projectOptional.isPresent()){
            return new ResponseEntity<>(dtOsConverter.getDTOFromProject(projectOptional.get()), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/post/taskid={id}", method = GET)
    public ResponseEntity<?> getPostByTaskId(@PathVariable int id) {
        Optional<Task> taskOptional = taskRepository.findById(id);

        if(taskOptional.isPresent()){
            Optional<Post> postOptional = postRepository.findByTask(taskOptional.get());
            if(postOptional.isPresent()){
                return new ResponseEntity<>(postOptional.get(), HttpStatus.OK);
            }else{
                Post post = new Post();
                post.setTask(taskOptional.get());

                post = postRepository.save(post);

                return new ResponseEntity<>(post, HttpStatus.OK);
            }
        }else{
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value= "/logout", method= GET)
    public ResponseEntity<?> logout(@RequestHeader Map<String, String> headers){
        String key = headers.get("key");
        Optional<User> userOptional = userRepository.findByHashKey(key);

        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setActive(false);
            user.setLastActive(LocalDateTime.now());

            userRepository.save(user);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }
}

