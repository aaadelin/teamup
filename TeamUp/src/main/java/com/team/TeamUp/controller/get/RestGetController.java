package com.team.TeamUp.controller.get;

import com.team.TeamUp.domain.*;
import com.team.TeamUp.domain.enums.Department;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.*;
import com.team.TeamUp.service.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TaskUtils;
import com.team.TeamUp.validation.UserValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Slf4j
public class RestGetController {

    private UserService userService;
    private TaskService taskService;
    private TeamService teamService;
    private ProjectService projectService;
    private CommentService commentService;
    private PostService postService;
    private LocationService locationRepository;
    private ResetRequestService resetRequestService;

    private DTOsConverter dtOsConverter;
    private UserValidation userValidation;
    private TaskUtils taskUtils;

    @Autowired
    public RestGetController(UserService userService, TeamService teamService, TaskService taskService,
                             ProjectService projectService, CommentService commentService, PostService postService,
                             DTOsConverter dtOsConverter, LocationService locationService, UserValidation userValidation,
                             ResetRequestService resetRequestService, TaskUtils taskUtils) {
        this.taskService = taskService;
        this.teamService = teamService;
        this.userService = userService;

        this.projectService = projectService;
        this.commentService = commentService;
        this.postService = postService;
        this.locationRepository = locationService;
        this.resetRequestService = resetRequestService;
        this.dtOsConverter = dtOsConverter;
        this.userValidation = userValidation;
        this.taskUtils = taskUtils;
        log.info("Creating RestGetController");
    }

    //GET methods - for selecting

    @RequestMapping(value = "/teams", method = GET)
    public ResponseEntity<?> getAllTeams(@RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get all teams method with headers: %s", headers.toString()));
        List<TeamDTO> teams = teamService.getAllTeamDTO();
        log.info(String.format("Returning list of teams: %s", teams.toString()));
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }

    @RequestMapping(value = "/locations", method = GET)
    public ResponseEntity<?> getAllLocations(@RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get all locations method with headers: %s", headers.toString()));
        List<Location> locations = locationRepository.getAll();
        log.info(String.format("Returning list of teams: %s", locations.toString()));
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @RequestMapping(value = "/projects", method = GET)
    public ResponseEntity<?> getAllProjects(@RequestHeader Map<String, String> headers,
                                            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                            @RequestParam(name = "search", required = false) String search) {
        log.info(String.format("Entering get all projects method with headers: %s, page %s and search term: %s", headers, page, search));
        List<ProjectDTO> projects = projectService.findByTermInPage(search, page);

        log.info(String.format("Returning list of projects: %s", projects));
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @RequestMapping(value = "/posts", method = GET)
    public ResponseEntity<?> getAllPosts(@RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get all posts method with headers: %s", headers.toString()));
        List<PostDTO> posts = postService.getAllDTOS();
        log.info(String.format("Returning list of posts: %s", posts.toString()));
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @RequestMapping(value = "/comments", method = GET)
    public ResponseEntity<?> getAllComments(@RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get all comments method with headers: %s", headers.toString()));
        List<CommentDTO> comments = commentService.getAll().stream().map(dtOsConverter::getDTOFromComment).collect(Collectors.toList());
        log.info(String.format("Returning list of comments: %s", comments.toString()));
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @RequestMapping(value = "/departments", method = GET)
    public ResponseEntity<?> getAllDepartments(@RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get all departments method with headers: %s", headers.toString()));
        List<Department> departments = Arrays.asList(Department.values());
        log.info(String.format("Returning current department types: %s", departments.toString()));
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    @RequestMapping(value = "/posts/{postId}/comments", method = GET)
    public ResponseEntity<?> getAllPostComments(@PathVariable int postId, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get all post's comments method with postId: %d /n and headers: %s", postId, headers.toString()));
        try {
            PostDTO postDTO = postService.getDTOByID(postId);
            Collections.reverse(postDTO.getComments());
            log.info(String.format("Returning post comments: %s", postDTO.getComments()));
            return new ResponseEntity<>(postDTO.getComments(), HttpStatus.OK);
        } catch(NoSuchElementException ifnore) {
            log.info(String.format("No post found with id %d", postId));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/teams/{id}", method = GET)
    public ResponseEntity<?> getTeamById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get team by id method with teamId: %d /n and headers: %s", id, headers.toString()));
        try{
            TeamDTO teamDTO = teamService.getTeamDTOByID(id);
            log.info(String.format("Returning team: %s", teamDTO.toString()));
            return new ResponseEntity<>(teamDTO, HttpStatus.OK);
        } catch (NoSuchElementException ignore){
            log.info(String.format("No team found with id %d", id));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/projects/{id}", method = GET)
    public ResponseEntity<?> getProjectById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get project by id method with projectId: %d /n and headers: %s", id, headers.toString()));
        try{
            ProjectDTO project = projectService.getDTOByID(id);
            log.info(String.format("Returning project %s", project.toString()));
            return new ResponseEntity<>(project, HttpStatus.OK);
        } catch (NoSuchElementException ignore){
            log.info("No project found");
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/projects/{id}/statistics", method = GET)
    public ResponseEntity<?> getProjectStatisticsById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get project's statistics by id method with projectId: %d /n and headers: %s", id, headers.toString()));
        try {
            Project project = projectService.getByID(id);
            int[] stats = new int[3];
            for(Task task : project.getTasks()){
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
            log.info(String.format("Returning project's statistics %s", Arrays.toString(stats)));
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch(NoSuchElementException ignore) {
            log.info("No project found");
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/projects/{id}/statistics/detailed", method = GET)
    public ResponseEntity<?> getProjectDetailedStatisticsById(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        log.info("Entering get project's detailed statistics by id method with projectId: {}  and headers: {}", id, headers.toString());
        Project project = projectService.getByID(id);

        List<Integer> counts = taskUtils.createStatisticsFromListOfTasks(project.getTasks());
        return new ResponseEntity<>(counts, HttpStatus.OK);
    }

    @RequestMapping(value = "/projects/{id}/tasks", method = GET)
    public ResponseEntity<?> getProjectsTasks(@PathVariable int id,
                                              @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                              @RequestHeader Map<String, String> headers){
        log.info(String.format("Entering method to get all tasks from a project with project id %s page %s and headers %s", id, page, headers));
        Project project = projectService.getByID(id);
        List<Task> tasks = taskService.getAllByProject(project, page);
        log.info(String.format("Exiting with list of tasks: %s", tasks));
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @RequestMapping(value = "/key", method = GET)
    public ResponseEntity<?> getIdForCurrentUser(@RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get key for current user method with headers: %s", headers.toString()));
        try{
            User user = userService.getByHashKey(headers.get("token"));
            log.info(String.format("Returning user id %s", user.getId()));
            return new ResponseEntity<>(user.getId(), HttpStatus.OK);
        } catch (NoSuchElementException ignore){
            log.info("No user found");
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/posts/taskid={id}", method = GET)
    public ResponseEntity<?> getPostByTaskId(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering get post by taskId method with taskId: %d /n and headers: %s", id, headers.toString()));

        try{
            Task task = taskService.getByID(id);

            Optional<Post> postOptional = postService.getByTask(task);
            if (postOptional.isPresent()) {
                PostDTO postDTO = dtOsConverter.getDTOFromPost(postOptional.get());
                if(postDTO.getComments() == null){
                    postDTO.setComments(Collections.emptyList());
                }
                Collections.reverse(postDTO.getComments());
                log.info(String.format("Returning post: %s", postDTO));
                return new ResponseEntity<>(postDTO, HttpStatus.OK);
            } else {
                log.info("Task doesn't have any post yet. Creating an empty one and returning it");
                Post post = new Post();
                post.setTask(task);

                post = postService.save(post);
                PostDTO postDTO = dtOsConverter.getDTOFromPost(post);
                log.info(String.format("Returning post: %s", postDTO));
                return new ResponseEntity<>(postDTO, HttpStatus.OK);
            }
        } catch (NoSuchElementException ignore){
            log.info(String.format("No task found with id %s", id));
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/admin", method = GET)
    public ResponseEntity<?> isUserAdmin(@RequestHeader Map<String, String> headers){
        log.info(String.format("Entering method to check if user is admin with headers %s", headers));
        Boolean status = userValidation.isValid(headers, UserStatus.ADMIN);
        log.info(String.format("Exiting with status %s", status));
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/requests/{id}", method = GET)
    public ResponseEntity<?> getRequestById(@PathVariable int id){
        log.info("Entered method to get request with request id: {}", id);
        ResetRequestDTO resetRequestDTO = dtOsConverter.getDTOFromResetRequest(resetRequestService.getByID(id));

        if(resetRequestDTO.getCreatedAt().equals(LocalDateTime.MIN)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        log.info(String.format("Exited with request %s", resetRequestDTO));
        return new ResponseEntity<>(resetRequestDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/teams/{id}/statistics", method = GET)
    public ResponseEntity<?> getTeamsStatistics(@PathVariable int id){
        log.info("Entered method to get teams statistics with team id {} ", id);
        Team team = teamService.getTeamByID(id);
        List<Task> tasks = taskService.getTasksByAssignees(team.getMembers());
        List<Integer> counts = taskUtils.createStatisticsFromListOfTasks(tasks);
        return new ResponseEntity<>(counts, HttpStatus.OK);
    }
}

