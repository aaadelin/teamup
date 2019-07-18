package com.team.TeamUp.utils;

import com.team.TeamUp.domain.*;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.dtos.TeamDTO;
import com.team.TeamUp.dtos.UserDTO;
import com.team.TeamUp.persistance.*;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class used to convert from DTOs to Domain entities and backwards.
 */
public class DTOsConverter {

    private UserRepository userRepository;
    private TeamRepository teamRepository;
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;

    public DTOsConverter(UserRepository userRepository,
                         TeamRepository teamRepository,
                         TaskRepository taskRepository,
                         ProjectRepository projectRepository){

        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;

    }

    /**
     *
     * @param user user Object to be converted
     * @return userDTO instance containing user parameters data
     */
    public UserDTO getDTOFromUser(User user){
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setLastActive(user.getLastActive());
        userDTO.setActive(user.isActive());
        userDTO.setStatus(user.getStatus());
        userDTO.setPhoto(user.getPhoto());
        if(user.getTeam() != null){
            userDTO.setTeamID(user.getTeam().getId());
            userDTO.setDepartment(user.getTeam().getDepartment());
        }else{
            userDTO.setTeamID(-1);
        }

        return userDTO;
    }

    /**
     *
     * @param userDTO UserDTO instance to be converted to User domain model
     * @return user entity containing userDTO data
     */
    public User getUserFromDTO(UserDTO userDTO){
        Optional<User> userOptional = userRepository.findById(userDTO.getId());
        User user;
        user = userOptional.orElseGet(User::new);

        if(userDTO.getUsername() != null){
            user.setUsername(userDTO.getUsername());
        }
        if(userDTO.getFirstName() != null){
            user.setFirstName(userDTO.getFirstName());
        }
        if(userDTO.getLastName() != null){
            user.setLastName(userDTO.getLastName());
        }
        if(userDTO.getLastActive() != null) {
            user.setLastActive(userDTO.getLastActive());
        }
        user.setActive(userDTO.isActive());
        user.setPhoto(userDTO.getPhoto());
        user.setStatus(userDTO.getStatus());
        user.setPassword(TokenUtils.getMD5Token(userDTO.getPassword()));

        Optional<Team> team = teamRepository.findById(userDTO.getTeamID());

        user.setTeam(team.orElse(null));
        if(user.getHashKey() == null){
            user.setHashKey(TokenUtils.getMD5Token());
        }

        return user;
    }

    /**
     *
     * @param taskDTO TaskDTO instance to be converted to Task domain model
     * @return Task object containing taskDTO data
     */
    public Task getTaskFromDTO(TaskDTO taskDTO){ //TODO
        Task task;
        Optional<Task> taskOptional = taskRepository.findById(taskDTO.getId());
        task = taskOptional.orElseGet(Task::new);

        task.setSummary(taskDTO.getSummary());
        task.setDescription(taskDTO.getDescription());
        task.setDoneAt(taskDTO.getDoneAt());
        task.setLastChanged(taskDTO.getLastChanged());
        task.setDeadline(taskDTO.getDeadline());
        task.setTaskStatus(taskDTO.getTaskStatus());

        Optional<Project> project = projectRepository.findById(taskDTO.getProject());
        project.ifPresent(task::setProject);
        //TODO change with user's hash key instead of id
        task.setReporter(userRepository.findById(taskDTO.getReporterID()).get()); //should always have a reporter
        task.setDifficulty(taskDTO.getDifficulty());
        task.setPriority(taskDTO.getPriority());
        task.setDepartment(taskDTO.getDepartment());
        task.setTaskType(taskDTO.getTaskType());
        task.setAssignees(taskDTO.getAssignees().stream().map(assignee -> userRepository.findById(assignee).get()).collect(Collectors.toList()));

        return task;
    }

    /**
     *
     * @param task Task domain model instance to be converted to TaskDTO
     * @return TaskDTO instance containing task data
     */
    public TaskDTO getDTOFromTask(Task task){ //TODO
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
        taskDTO.setDepartment(taskDTO.getDepartment());
        taskDTO.setTaskType(task.getTaskType());
        taskDTO.setReporterID(task.getReporter().getId());
        taskDTO.setAssignees(task.getAssignees().stream().map(User::getId).collect(Collectors.toList()));

        return taskDTO;
    }

    /**
     *
     * @param projectDTO ProjectDTO instance to be converted to Project domain model
     * @return Project object containing projectDTO data
     */
    public Project getProjectFromDTO(ProjectDTO projectDTO){ //TODO
        Optional<Project> projectOptional = projectRepository.findById(projectDTO.getId());

        Project project = projectOptional.orElseGet(Project::new);

        project.setId(projectDTO.getId());
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setDeadline(projectDTO.getDeadline());
        project.setOwner(userRepository.findById(projectDTO.getOwnerID()).get());

        return project;
    }

    /**
     *
     * @param project Project domain instance to be converted to ProjectDTO
     * @return ProjectDTO instance containing project data
     */
    public ProjectDTO getDTOFromProject(Project project){ //TODO
        ProjectDTO projectDTO = new ProjectDTO();

        projectDTO.setId(project.getId());
        projectDTO.setDeadline(project.getDeadline());
        projectDTO.setDescription(project.getDescription());
        projectDTO.setName(project.getName());
        projectDTO.setTasksIDs(project.getTasks().stream().map(Task::getId).collect(Collectors.toList()));
        projectDTO.setOwnerID(project.getOwner().getId());

        return projectDTO;
    }

    /**
     *
     * @param teamDTO TeamDTO object to be converted to Team model
     * @return Team model instance containing the information from teamDTO
     */
    public Team getTeamFromDTO(TeamDTO teamDTO){
        Optional<Team> teamOptional = teamRepository.findById(teamDTO.getId());
        Team team = teamOptional.orElseGet(Team::new);

        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
        team.setLocation(teamDTO.getLocation());
        team.setDepartment(teamDTO.getDepartment());

        return team;
    }

    /**
     * Only the admin can change the leader
     * @param teamDTO TeamDTO object to be converted to Team model
     * @return Team model instance containing the information from teamDTO
     */
    public Team getTeamFromDTO(TeamDTO teamDTO, UserStatus status){

        Team team = getTeamFromDTO(teamDTO);

        if(status == UserStatus.ADMIN){
            Optional<User> leader = userRepository.findById(teamDTO.getLeaderID());
            leader.ifPresent(team::setLeader);
        }

        return team;
    }

    /**
     *
     * @param team Team domain instance to be converted to TeamDTO
     * @return TeamDTO instance containing team object data
     */
    public TeamDTO getDTOFromTeam(Team team){
        TeamDTO teamDTO = new TeamDTO();

        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());
        teamDTO.setDescription(team.getDescription());
        teamDTO.setLocation(team.getLocation());
        teamDTO.setDepartment(team.getDepartment());
        if(team.getLeader() != null){
            teamDTO.setLeaderID(team.getLeader().getId());
        }
        teamDTO.setMembers(team.getMembers().stream().map(User::getId).collect(Collectors.toList()));

        return teamDTO;
    }
}