package com.team.TeamUp.utils;

import com.team.TeamUp.domain.*;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.*;
import com.team.TeamUp.persistence.*;
import com.team.TeamUp.validation.TaskValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class used to convert from DTOs to Domain entities and backwards.
 */
@Component
@Slf4j
public class DTOsConverter {

    private UserRepository userRepository;
    private TeamRepository teamRepository;
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private PostRepository postRepository;
    private CommentRepository commentRepository;
    private LocationRepository locationRepository;
    private ResetRequestRepository resetRequestRepository;
    private TaskValidation taskValidation;

    public DTOsConverter(UserRepository userRepository,
                         TeamRepository teamRepository,
                         TaskRepository taskRepository,
                         ProjectRepository projectRepository,
                         PostRepository postRepository,
                         LocationRepository locationRepository,
                         CommentRepository commentRepository,
                         ResetRequestRepository resetRequestRepository) {

        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.postRepository = postRepository;
        this.locationRepository = locationRepository;
        this.commentRepository = commentRepository;
        this.resetRequestRepository = resetRequestRepository;
        this.taskValidation = new TaskValidation();

        log.info("Instance of class DtosConverter created");
    }

    /**
     * @param user user Object to be converted
     * @return userDTO instance containing user parameters data
     */
    public UserDTO getDTOFromUser(User user) {
        log.info(String.format("Method get UserDTO from User called with user parameter: %s", user));
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setLastActive(user.getLastActive());
        userDTO.setActive(user.isActive());
        userDTO.setStatus(user.getStatus());
        userDTO.setPhoto(user.getPhoto());
        userDTO.setJoinedAt(user.getJoinedAt());
        if(user.getMail() == null){
            user.setMail("");
        }
        userDTO.setLocked(user.isLocked());
        userDTO.setMail(user.getMail());
        if (user.getTeam() != null) {
            userDTO.setTeamID(user.getTeam().getId());
            userDTO.setDepartment(user.getTeam().getDepartment());
        } else {
            userDTO.setTeamID(-1);
        }

        log.info(String.format("UserDTO instance created: %s", userDTO));
        return userDTO;
    }

    /**
     * @param userDTO UserDTO instance to be converted to User domain model
     * @return user entity containing userDTO data
     */
    public User getUserFromDTO(UserDTO userDTO, UserStatus requesterStatus) {
        log.info(String.format("Method create User from UserDTO called with parameter %s", userDTO));

        Optional<User> userOptional = userRepository.findById(userDTO.getId());
        User user;
        user = userOptional.orElseGet(User::new);
        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getLastActive() != null) {
            user.setLastActive(userDTO.getLastActive());
        }
        user.setActive(userDTO.isActive());
        if (userDTO.getPhoto() != null) {
            user.setPhoto(userDTO.getPhoto());
        }
        if((userOptional.isPresent() && userOptional.get().getJoinedAt() == null) || userOptional.isEmpty()){
            user.setJoinedAt(LocalDate.now());
        }
        if(userDTO.getMail() == null){
            userDTO.setMail("");
        }
        user.setMail(userDTO.getMail());
        user.setMinutesUntilLogout(60);
        user.setStatus(userDTO.getStatus());
        user.setLocked(userDTO.isLocked());
        if (userDTO.getPassword() != null){
            user.setPassword(TokenUtils.getMD5Token(userDTO.getPassword()));
        }

        Optional<Team> team = teamRepository.findById(userDTO.getTeamID());

        user.setTeam(team.orElse(null));
        if (user.getHashKey() == null) {
            user.setHashKey(TokenUtils.getMD5Token());
        }

        log.info(String.format("Instance of type User created: %s", user));
        return user;
    }

    /**
     * @param taskDTO TaskDTO instance to be converted to Task domain model
     * @return Task object containing taskDTO data
     */
    public Task getTaskFromDTO(TaskDTO taskDTO, String reporterKey) {
        log.info(String.format("Method create Task from TaskDto called with parameter: %s", taskDTO));

        Optional<Task> taskOptional = taskRepository.findById(taskDTO.getId());
        Task task = taskOptional.orElseGet(Task::new);
        if (taskDTO.getSummary() != null && !taskDTO.getSummary().trim().equals("") ||
                !taskDTO.getDescription().trim().equals("")) {

            task.setSummary(taskDTO.getSummary());
        } else {
            throw new IllegalArgumentException();
        }
        task.setDescription(taskDTO.getDescription());
        task.setDoneAt(taskDTO.getDoneAt());
        task.setLastChanged(LocalDateTime.now());
        task.setTaskStatus(taskDTO.getTaskStatus());
        task.setCreatedAt(LocalDateTime.now());

        Optional<Project> project = projectRepository.findById(taskDTO.getProject());
        task.setProject(project.orElseThrow());
        Optional<User> reporter = userRepository.findByHashKey(reporterKey);
        task.setReporter(reporter.orElseThrow());

        if(taskDTO.getDeadline().isBefore(task.getProject().getDeadline()) && taskDTO.getDeadline()
                .isAfter(LocalDateTime.now().minusMinutes(1))){
            //because of the possible delay of the network, it is acceptable to be 1 minute earlier than NOW
            task.setDeadline(taskDTO.getDeadline());
        }else{
            throw new IllegalArgumentException("Task deadline would be after project's deadline or before NOW");
        }

        if (taskDTO.getDifficulty() <= 3 && taskDTO.getDifficulty() >= 1) {
            task.setDifficulty(taskDTO.getDifficulty());
        }
        if (taskDTO.getPriority() <= 3 && taskDTO.getPriority() >= 1) {
            task.setPriority(taskDTO.getPriority());
        }
        task.setDepartment(taskDTO.getDepartment());
        task.setTaskType(taskDTO.getTaskType());
        List<User> list = new ArrayList<>();
        for (Integer assignee : taskDTO.getAssignees()) {
            Optional<User> user = userRepository.findById(assignee);
            user.ifPresent(list::add);
        }
        task.setAssignees(list);

        log.info(String.format("Instance of type Task created: %s", task));
        return task;
    }

    /**
     * Get the task updated from a taskDTO depending on the user status
     * If the user is the reporter or ADMIN he can change every option available,
     * If the user is assignee, he can only change the status,
     * Otherwise, no change is allowed
     * @param taskDTO taskDTO entity to be con
     * @param user User that made the change
     * @return task updated accordingly
     */
    public Task getTaskFromDTOForUpdate(TaskDTO taskDTO, User user) {
        log.info(String.format("Method to update Task from TaskDto called with parameter: %s", taskDTO));

        Optional<Task> taskOptional = taskRepository.findById(taskDTO.getId());
        Task task = taskOptional.orElseThrow();

        if (taskDTO.getDescription() == null && taskDTO.getDeadline() == null && taskDTO.getTaskType() == null &&
                taskDTO.getDifficulty() == 0 && taskDTO.getPriority() == 0 && taskDTO.getTaskStatus() != null &&
                (task.getAssignees().contains(user) || task.getReporter().getId() == user.getId()) &&
                taskValidation.isTaskStatusChangeValid(taskOptional.get(), taskDTO)) {
            // Only the status is updated
            task.setTaskStatus(taskDTO.getTaskStatus());
            task.setLastChanged(LocalDateTime.now());
            return task;

        } else if (taskDTO.getDescription() != null && !taskDTO.getDescription().trim().equals("") &&
                taskDTO.getDeadline() != null && taskDTO.getTaskType() != null &&
                taskDTO.getDifficulty() != 0 && taskDTO.getPriority() != 0 && taskDTO.getTaskStatus() != null &&
                taskValidation.isTaskStatusChangeValid(taskOptional.get(), taskDTO) &&
                (taskDTO.getReporterID() == user.getId() || user.getStatus().equals(UserStatus.ADMIN))) {

            task.setDescription(taskDTO.getDescription());
            task.setDeadline(taskDTO.getDeadline());
            task.setTaskType(taskDTO.getTaskType());
            if (taskDTO.getDifficulty() >= 1 && taskDTO.getDifficulty() <= 3){
                task.setDifficulty(taskDTO.getDifficulty());
            }
            if (taskDTO.getPriority() >= 1 && taskDTO.getPriority() <= 3){
                task.setPriority(taskDTO.getPriority());
            }
            task.setTaskStatus(taskDTO.getTaskStatus());
            if(task.getTaskStatus().equals(TaskStatus.CLOSED)){
                task.setDoneAt(LocalDateTime.now());
            }else{
                task.setDoneAt(null);
            }
            task.setLastChanged(LocalDateTime.now());
            task.setAssignees(taskDTO.getAssignees().stream()
                    .map(assigneeId -> userRepository.findById(assigneeId).orElseThrow())
                    .collect(Collectors.toList()));
            return task;
        } else {
            throw new IllegalArgumentException();
        }
    }


    /**
     * @param task Task domain model instance to be converted to TaskDTO
     * @return TaskDTO instance containing task data
     */
    public TaskDTO getDTOFromTask(Task task) {
        log.info(String.format("Method to create TaskDTO from Task called with parameter :%s", task));
        TaskDTO taskDTO = new TaskDTO();

        taskDTO.setId(task.getId());
        taskDTO.setSummary(task.getSummary());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setCreatedAt(task.getCreatedAt());
        taskDTO.setDoneAt(task.getDoneAt());
        taskDTO.setLastChanged(task.getLastChanged());
        taskDTO.setDeadline(task.getDeadline());
        taskDTO.setTaskStatus(task.getTaskStatus());
        taskDTO.setProject(task.getProject().getId());
        taskDTO.setDifficulty(task.getDifficulty());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setDepartment(task.getDepartment());
        taskDTO.setTaskType(task.getTaskType());
        taskDTO.setReporterID(task.getReporter().getId());
        if(task.getAssignees() == null){
            task.setAssignees(new ArrayList<>());
        }
        taskDTO.setAssignees(task.getAssignees().stream().map(User::getId).collect(Collectors.toList()));

        log.info(String.format("Instance of type TaskDTO created: %s", taskDTO));
        return taskDTO;
    }

    /**
     * @param projectDTO ProjectDTO instance to be converted to Project domain model
     * @return Project object containing projectDTO data
     */
    public Project getProjectFromDTO(ProjectDTO projectDTO) {
        log.info(String.format("Method to create Project from ProjectDTO called with parameter: %s", projectDTO));
        Optional<Project> projectOptional = projectRepository.findById(projectDTO.getId());

        Project project = projectOptional.orElseGet(Project::new);

        if(projectDTO.getId() != 0){
            project.setId(projectDTO.getId());
        }
        if(!(projectDTO.getName() == null || projectDTO.getName().equals(""))) {
            project.setName(projectDTO.getName());
        }
        if(!(projectDTO.getDescription() == null || projectDTO.getDescription().equals(""))) {
            project.setDescription(projectDTO.getDescription());
        }
        project.setDeadline(projectDTO.getDeadline());
        if(projectDTO.getOwnerID() != 0){
            project.setOwner(userRepository.findById(projectDTO.getOwnerID()).get());
        }
        project.setArchived(projectDTO.isArchived());

        if(projectDTO.getVersion() != null){
            project.setVersion(projectDTO.getVersion());
        }else{
            project.setVersion("0.0.1");
        }

        log.info(String.format("Instance of type Project created: %s", project));
        return project;
    }

    /**
     * @param project Project domain instance to be converted to ProjectDTO
     * @return ProjectDTO instance containing project data
     */
    public ProjectDTO getDTOFromProject(Project project) {
        log.info(String.format("Method to create ProjectDTO from Project called with parameter: %s", project));
        ProjectDTO projectDTO = new ProjectDTO();

        projectDTO.setId(project.getId());
        projectDTO.setDeadline(project.getDeadline());
        projectDTO.setDescription(project.getDescription());
        projectDTO.setName(project.getName());
        projectDTO.setVersion(project.getVersion());
        projectDTO.setTasksIDs(project.getTasks().stream().map(Task::getId).collect(Collectors.toList()));
        projectDTO.setOwnerID(project.getOwner().getId());
        projectDTO.setArchived(project.isArchived());

        log.info(String.format("Instance of type ProjectDTO created: %s", projectDTO));
        return projectDTO;
    }

    /**
     * @param teamDTO TeamDTO object to be converted to Team model
     * @return Team model instance containing the information from teamDTO
     */
    private Team getTeamFromDTO(TeamDTO teamDTO) {
        log.info(String.format("Method to create Team from DTO called with parameter: %s", teamDTO));
        Optional<Team> teamOptional = teamRepository.findById(teamDTO.getId());
        Team team = teamOptional.orElseGet(Team::new);

        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
        team.setLocation(locationRepository.getOne(teamDTO.getLocation()));
        team.setDepartment(teamDTO.getDepartment());

        log.info(String.format("Instance of type Team created: %s", team));
        return team;
    }

    /**
     * Only the admin can change the leader
     *
     * @param teamDTO TeamDTO object to be converted to Team model
     * @return Team model instance containing the information from teamDTO
     */
    public Team getTeamFromDTO(TeamDTO teamDTO, UserStatus status) {
        log.info("Method to create instance Team from TeamDTO called with {} and user status {}", teamDTO, status);
        Team team = getTeamFromDTO(teamDTO);

        if (status == UserStatus.ADMIN) {
            log.info("User is able to create such instances");
            Optional<User> leader = userRepository.findById(teamDTO.getLeaderID());
            team.setLeader(leader.orElse(null));
        }

        log.info(String.format("Instance of type Team returned: %s", team));
        return team;
    }

    /**
     * @param team Team domain instance to be converted to TeamDTO
     * @return TeamDTO instance containing team object data
     */
    public TeamDTO getDTOFromTeam(Team team) {
        log.info(String.format("Method to convert from Team to TeamDTO called with parameter: %s", team));
        TeamDTO teamDTO = new TeamDTO();

        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());
        teamDTO.setDescription(team.getDescription());
        teamDTO.setLocation(team.getLocation().getId());
        teamDTO.setDepartment(team.getDepartment());
        if (team.getLeader() != null) {
            teamDTO.setLeaderID(team.getLeader().getId());
        }
        teamDTO.setMembers(team.getMembers().stream().map(User::getId).collect(Collectors.toList()));

        log.info(String.format("Instance of type team created: %s", teamDTO));
        return teamDTO;
    }

    /**
     * Converts LocationDTO to Location
     * @param location location to be converted
     * @return LocationDTO object
     */
    public LocationDTO getDTOFromLocation(Location location){
        log.info(String.format("Method to convert from location to DTO entered with parameter: %s", location));
        LocationDTO locationDTO = new LocationDTO();

        locationDTO.setId(location.getId());
        locationDTO.setAddress(location.getAddress());
        locationDTO.setCity(location.getCity());
        locationDTO.setCountry(location.getCountry());
        locationDTO.setTeams(location.getTeams().stream().map(Team::getId).collect(Collectors.toList()));

        log.info(String.format("Exiting with entity: %s", locationDTO));
        return locationDTO;
    }

    /**
     * Get Location from LocationDTO
     * @param locationDTO DTO object to be converted to location
     * @return Location object
     */
    public Location getLocationFromDTO(LocationDTO locationDTO){
        log.info("Method to convert from locationDTO to Location entered with parameter: {}", locationDTO);

        Optional<Location> locationOptional = locationRepository.findById(locationDTO.getId());
        Location location = new Location();
        locationOptional.ifPresent(value -> location.setId(value.getId()));

        location.setAddress(locationDTO.getAddress());
        location.setCity(locationDTO.getCity());
        location.setCountry(locationDTO.getCountry());
        location.setTeams(teamRepository.findAllById(locationDTO.getTeams()));
        log.info(String.format("Exiting with entity: %s", location));

        return location;
    }

    /**
     * Converts CommentDTO to Comment
     * @param commentDTO CommentDTO entity to be converted
     * @return Comment object
     */
    public Comment getCommentFromDTO(CommentDTO commentDTO) {
        log.info(String.format("Method to convert from CommentDTO to Comment called with parameter: %s", commentDTO));
        Comment comment = commentRepository.findById(commentDTO.getId()).orElseGet(Comment::new);
        comment.setTitle(commentDTO.getTitle());
        comment.setContent(commentDTO.getContent());
        comment.setCreator(userRepository.findById(commentDTO.getCreator().getId()).orElseThrow());
        Optional<Comment> commentOptional = commentRepository.findById(commentDTO.getParent());
        commentOptional.ifPresent(comment::setParent);
        Optional<Post> postOptional = postRepository.findById(commentDTO.getPostId());
        postOptional.ifPresent(comment::setPost);
        comment.setReplies(commentDTO.getReplies().stream().map(this::getCommentFromDTO).collect(Collectors.toList()));
        if(commentDTO.getDatePosted() == null){
            commentDTO.setDatePosted(LocalDateTime.now());
        }
        comment.setDatePosted(commentDTO.getDatePosted());
        log.info(String.format("Instance of type Comment created: %s", comment));
        return comment;
    }

    /**
     * Converts Comment to CommentDTO
     * @param comment Comment to be converted
     * @return CommentDTO object
     */
    public CommentDTO getDTOFromComment(Comment comment) {
        log.info(String.format("Method to convert from Comment to CommentDTO called with parameter: %s", comment));
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setTitle(comment.getTitle());
        commentDTO.setContent(comment.getContent());
        commentDTO.setCreator(this.getDTOFromUser(comment.getCreator()));
        commentDTO.setReplies(comment.getReplies().stream().map(this::getDTOFromComment).collect(Collectors.toList()));
        commentDTO.setDatePosted(comment.getDatePosted());
        int parentId = comment.getParent() != null?comment.getParent().getId() : 0;
        commentDTO.setParent(parentId);
        commentDTO.setPostId(comment.getPost().getId());
        log.info(String.format("Instance of type CommentDTO created: %s", commentDTO));
        return commentDTO;
    }

    /**
     * Converts a PostDTO to a Post object
     * @param postDTO PostDTO object to be converted
     * @return Post object
     */
    public Post getPostFromDTO(PostDTO postDTO) {
        log.info(String.format("Method to convert from PostDTO to Post called with parameter: %s", postDTO));
        Post post = postRepository.findById(postDTO.getId()).orElseGet(Post::new);

        post.setTask(taskRepository.findById(postDTO.getTaskDTO().getId()).orElseThrow());
        post.setComments(postDTO.getComments().stream().map(this::getCommentFromDTO).collect(Collectors.toList()));

        log.info(String.format("Instance of type Post created: %s", post));
        return post;
    }

    /**
     * Converts a Post object to a postDTO
     * @param post Post object to be converted
     * @return PostDTO object
     */
    public PostDTO getDTOFromPost(Post post) {
        log.info(String.format("Method to convert from Post to PostDTO called with parameter: %s", post));

        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setTaskDTO(getDTOFromTask(post.getTask()));
        postDTO.setComments(post.getComments().stream().map(this::getDTOFromComment).collect(Collectors.toList()));

        if(postDTO.getComments() == null){
            postDTO.setComments(new ArrayList<>());
        }

        log.info(String.format("Instance of type PostDTO created: %s", postDTO));
        return postDTO;
    }

    /**
     * Converts a ResetRequest to a DTO
     * @param resetRequest ResetRequest object to be converted
     * @return ResetRequestDTO object
     */
    public ResetRequestDTO getDTOFromResetRequest(ResetRequest resetRequest){
        ResetRequestDTO resetRequestDTO = new ResetRequestDTO();
        resetRequestDTO.setId(resetRequest.getId());
        resetRequestDTO.setCreatedAt(resetRequest.getCreatedAt());
        resetRequestDTO.setUserId(resetRequest.getUser().getId());

        return resetRequestDTO;
    }

    /**
     * Converts a ResetRequestDTO to a ResetRequest
     * @param resetRequestDTO ResetRequestDTO object to be converted
     * @return ResetRequest object
     */
    public ResetRequest getResetRequestFromDTO(ResetRequestDTO resetRequestDTO){
        ResetRequest resetRequest = resetRequestRepository.findById(resetRequestDTO.getId()).orElse(new ResetRequest());
        if(resetRequestDTO.getUserId() != 0){
            resetRequest.setUser(userRepository.findById(resetRequestDTO.getUserId()).orElseThrow());
        }
        if(resetRequestDTO.getCreatedAt() == null){
            resetRequest.setCreatedAt(LocalDateTime.now());
        }else{
            resetRequest.setCreatedAt(resetRequestDTO.getCreatedAt());
        }
        return resetRequest;
    }
}