package com.team.TeamUp.controller.get;

import com.team.TeamUp.domain.*;
import com.team.TeamUp.domain.enums.Department;
import com.team.TeamUp.dtos.CommentDTO;
import com.team.TeamUp.dtos.PostDTO;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.dtos.TeamDTO;
import com.team.TeamUp.persistence.*;
import com.team.TeamUp.utils.DTOsConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestGetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestGetController.class);
    private static final int PAGE_SIZE = 10;


    private TeamRepository teamRepository;
    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private LocationRepository locationRepository;
    private DTOsConverter dtOsConverter;

    @Autowired
    public RestGetController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                             ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository,
                             DTOsConverter dtOsConverter, LocationRepository locationRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository ;
        this.projectRepository = projectRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.locationRepository = locationRepository;
        this.dtOsConverter = dtOsConverter;
        LOGGER.info("Creating RestGetController");
    }

    //GET methods - for selecting

    @RequestMapping(value = "/teams", method = GET)
    public ResponseEntity<?> getAllTeams(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all teams method with headers: %s", headers.toString()));
        List<TeamDTO> teams = teamRepository.findAll().stream().map(dtOsConverter::getDTOFromTeam).collect(Collectors.toList());
        LOGGER.info(String.format("Returning list of teams: %s", teams.toString()));
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }

    @RequestMapping(value = "/locations", method = GET)
    public ResponseEntity<?> getAllLocations(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all locations method with headers: %s", headers.toString()));
        List<Location> locations = locationRepository.findAll();
        LOGGER.info(String.format("Returning list of teams: %s", locations.toString()));
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @RequestMapping(value = "/projects", method = GET)
    public ResponseEntity<?> getAllProjects(@RequestHeader Map<String, String> headers,
                                            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                            @RequestParam(name = "search", required = false) String search) {
        LOGGER.info(String.format("Entering get all projects method with headers: %s, page %s and search term: %s", headers, page, search));
        List<ProjectDTO> projects;
        if(search != null){
            projects = projectRepository.findAllByNameContainingOrDescriptionContaining(search, search, PageRequest.of(page, PAGE_SIZE))
                    .stream().map(dtOsConverter::getDTOFromProject).collect(Collectors.toList());
        }else{
            projects = projectRepository.findAll(PageRequest.of(page, PAGE_SIZE)).stream().map(dtOsConverter::getDTOFromProject).collect(Collectors.toList());
        }
        LOGGER.info(String.format("Returning list of projects: %s", projects));
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @RequestMapping(value = "/posts", method = GET)
    public ResponseEntity<?> getAllPosts(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all posts method with headers: %s", headers.toString()));
        List<PostDTO> posts = postRepository.findAll().stream().map(dtOsConverter::getDTOFromPost).collect(Collectors.toList());
        LOGGER.info(String.format("Returning list of posts: %s", posts.toString()));
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @RequestMapping(value = "/comments", method = GET)
    public ResponseEntity<?> getAllComments(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all comments method with headers: %s", headers.toString()));
        List<CommentDTO> comments = commentRepository.findAll().stream().map(dtOsConverter::getDTOFromComment).collect(Collectors.toList());
        LOGGER.info(String.format("Returning list of comments: %s", comments.toString()));
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @RequestMapping(value = "/departments", method = GET)
    public ResponseEntity<?> getAllDepartments(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all departments method with headers: %s", headers.toString()));
        List<Department> departments = Arrays.asList(Department.values());
        LOGGER.info(String.format("Returning current department types: %s", departments.toString()));
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    @RequestMapping(value = "/posts/{postId}/comments", method = GET)
    public ResponseEntity<?> getAllPostComments(@PathVariable int postId, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get all post's comments method with postId: %d /n and headers: %s", postId, headers.toString()));
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            PostDTO postDTO = dtOsConverter.getDTOFromPost(post.get());
            Collections.reverse(postDTO.getComments());
            LOGGER.info(String.format("Returning post comments: %s", postDTO.getComments()));
            return new ResponseEntity<>(postDTO.getComments(), HttpStatus.OK);
        } else {
            LOGGER.info(String.format("No post found with id %d", postId));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/teams/{id}", method = GET)
    public ResponseEntity<?> getTeamById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get team by id method with teamId: %d /n and headers: %s", id, headers.toString()));
        Optional<Team> teamOptional = teamRepository.findById(id);
        if (teamOptional.isPresent()) {
            LOGGER.info(String.format("Returning team: %s", teamOptional.get().toString()));
            return new ResponseEntity<>(dtOsConverter.getDTOFromTeam(teamOptional.get()), HttpStatus.OK);
        } else {
            LOGGER.info(String.format("No team found with id %d", id));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/projects/{id}", method = GET)
    public ResponseEntity<?> getProjectById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get project by id method with projectId: %d /n and headers: %s", id, headers.toString()));
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isPresent()) {
            LOGGER.info(String.format("Returning project %s", projectOptional.get().toString()));
            return new ResponseEntity<>(dtOsConverter.getDTOFromProject(projectOptional.get()), HttpStatus.OK);
        } else {
            LOGGER.info("No project found");
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/projects/{id}/statistics", method = GET)
    public ResponseEntity<?> getProjectStatisticsById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get project's statistics by id method with projectId: %d /n and headers: %s", id, headers.toString()));
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isPresent()) {
            int[] stats = new int[3];
            for(Task task : projectOptional.get().getTasks()){
                switch (task.getTaskStatus()){
                    case OPEN:
                    case REOPENED:
                        stats[0]++;
                        break;
                    case IN_PROGRESS:
                        stats[1]++;
                        break;
                    case UNDER_REVIEW:
                    case APPROVED:
                        stats[2]++;
                        break;
                    //todo add CLOSED to the finished category and Under_review at in_progress category?
                }
            }
            LOGGER.info(String.format("Returning project's statistics %s", Arrays.toString(stats)));
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } else {
            LOGGER.info("No project found");
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/projects/{id}/tasks", method = GET)
    public ResponseEntity<?> getProjectsTasks(@PathVariable int id, @RequestHeader Map<String, String> headers){
        LOGGER.info(String.format("Entering method to get all tasks from a project with project id %s and headers %s", id, headers));
        //TODO get only the task id and the status
        List<Task> tasks = projectRepository.findById(id).orElseThrow().getTasks();
        LOGGER.info(String.format("Exiting with list of tasks: %s", tasks));
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @RequestMapping(value = "/key", method = GET)
    public ResponseEntity<?> getIdForCurrentUser(@RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get key for current user method with headers: %s", headers.toString()));
        Optional<User> userOptional = userRepository.findByHashKey(headers.get("token"));
        if (userOptional.isPresent()) {
            LOGGER.info(String.format("Returning user id %s", userOptional.get().getId()));
            return new ResponseEntity<>(userOptional.get().getId(), HttpStatus.OK);
        } else {
            LOGGER.info("No user found");
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/posts/taskid={id}", method = GET)
    public ResponseEntity<?> getPostByTaskId(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering get post by taskId method with taskId: %d /n and headers: %s", id, headers.toString()));
        Optional<Task> taskOptional = taskRepository.findById(id);

        if (taskOptional.isPresent()) {
            Optional<Post> postOptional = postRepository.findByTask(taskOptional.get());
            if (postOptional.isPresent()) {
                PostDTO postDTO = dtOsConverter.getDTOFromPost(postOptional.get());
                if(postDTO.getComments() == null){
                    postDTO.setComments(Collections.emptyList());
                }
                Collections.reverse(postDTO.getComments());
                LOGGER.info(String.format("Returning post: %s", postDTO));
                return new ResponseEntity<>(postDTO, HttpStatus.OK);
            } else {
                LOGGER.info("Task doesn't have any post yet. Creating an empty one and returning it");
                Post post = new Post();
                post.setTask(taskOptional.get());

                post = postRepository.save(post);
                PostDTO postDTO = dtOsConverter.getDTOFromPost(post);
                LOGGER.info(String.format("Returning post: %s", postDTO));
                return new ResponseEntity<>(postDTO, HttpStatus.OK);
            }
        } else {
            LOGGER.info(String.format("No task found with id %s", id));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }
}

