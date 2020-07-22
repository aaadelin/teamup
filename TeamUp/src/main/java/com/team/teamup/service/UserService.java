package com.team.teamup.service;

import com.team.teamup.domain.User;
import com.team.teamup.domain.UserAuthentication;
import com.team.teamup.domain.UserEvent;
import com.team.teamup.domain.UserPreferences;
import com.team.teamup.domain.enums.UserStatus;
import com.team.teamup.dtos.*;
import com.team.teamup.persistence.UserAuthenticationRepository;
import com.team.teamup.persistence.UserEventRepository;
import com.team.teamup.persistence.UserPreferencesRepository;
import com.team.teamup.persistence.UserRepository;
import com.team.teamup.utils.DTOsConverter;
import com.team.teamup.utils.UserUtils;
import com.team.teamup.validation.UserValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserService {

    private static final int PAGE_SIZE = 10;

    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final UserAuthenticationRepository authenticationRepository;
    private final UserPreferencesRepository preferencesRepository;
    private final TeamService teamService;
    private final TaskService taskService;

    private final UserEventRepository eventRepository;

    private UserUtils userUtils;
    private final UserValidation userValidation;
    private final DTOsConverter dtOsConverter;

    @Autowired
    public UserService(UserRepository userRepository,
                       ProjectService projectService,
                       UserAuthenticationRepository authenticationRepository,
                       UserPreferencesRepository preferencesRepository,
                       TeamService teamService,
                       UserEventRepository eventRepository,
                       TaskService taskService,
                       UserValidation userValidation,
                       DTOsConverter dtOsConverter){
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.teamService = teamService;
        this.eventRepository = eventRepository;
        this.taskService = taskService;
        this.authenticationRepository = authenticationRepository;
        this.preferencesRepository = preferencesRepository;
        this.userValidation = userValidation;
        this.dtOsConverter = dtOsConverter;
    }

    @Autowired
    public void setUserUtils(UserUtils userUtils){
        this.userUtils = userUtils;
    }

    public List<TaskDTO> getReportedTasks(User user){
        return taskService.getAll().stream()
                .filter(task -> task.getReporter().equals(user))
                .map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
    }

    public List<TaskDTO> getAssignedTasks(User user){

        return taskService.getAll().stream()
                .filter(task -> task.getAssignees().contains(user))
                .map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());

    }

    /**
     *
     * @param key user's hashKey
     * @return user that corresponds to that key
     */
    public User getByHashKey(String key){
        return authenticationRepository.findByHashKey(key).orElseThrow().getUser();
    }

    /**
     *
     * @param filter optional string containing a filter for user's name
     * @param sort string on what field og the user to be sorted
     * @param page page number from repository
     * @param token string token for authentication
     * @return list of usersDTOs filtered and sorted from that page
     */
    public List<UserDTO> getSortedAndFilteredUsers(String filter, String sort, Integer page, String token){
        if(filter == null || filter.equals("")){
            boolean isAdmin = userValidation.isValid(token, UserStatus.ADMIN);
            return userUtils.sortUsers(page, sort, isAdmin, PAGE_SIZE);
        } else {
            return userUtils.filterUsers(filter);
        }
    }

    /**
     *
     * @param ids list with user's ids
     * @return list of users that exit with those ids
     */
    public List<UserDTO> getUsersByIds(List<Integer> ids){
        return userUtils.getUsersByIds(ids);
    }

    /**
     *
     * @param token token parameter from headers
     * @return true if the user has been successfully logged out or false if there is no user with that key
     */
    public boolean logout(String token){
        Optional<UserAuthentication> userOptional = authenticationRepository.findByHashKey(token);

        if (userOptional.isPresent()) {
            User user = userOptional.get().getUser();
            user.getAuthentication().setActive(false);
            user.getAuthentication().setLastActive(LocalDateTime.now());

            userUtils.saveUser(user);
            log.debug("User status saved in database");
            return true;
        }

        log.debug("User not eligible");
        return false;
    }

    /**
     *
     * @param id user's id
     * @return string containing user's photo or the default photo if the user did not set one
     * @throws IOException if the files are not found
     */
    public String getUsersPhoto(int id) throws IOException {
        User user = userRepository.findById(id).orElseThrow();
        String encodedString;
        String path = System.getProperty("user.home") + "/.TeamUpData/";

        if (user.getPhoto() == null) {
            File f = new File(path + "avatar.png");
            byte[] bytes = Files.readAllBytes(f.toPath());
            encodedString = Base64.getEncoder().encodeToString(bytes);
            log.debug("Exited with default image");
        } else {
            File file = new File(path + user.getPhoto());
            byte[] bytes = Files.readAllBytes(file.toPath());
            log.info(String.format("Exited with photo with path %s", user.getPhoto()));
            encodedString = Base64.getEncoder().encodeToString(bytes);
        }
        return encodedString;
    }

    /**
     *
     * @param id user's id
     * @return list of all user's history
     */
    public List<UserEvent> getUsersHistory(int id){
        User user = userRepository.findById(id).orElseThrow();
        return eventRepository.findAllByCreatorOrderByTimeDesc(user);
    }

    /**
     *
     * @param id user's id
     * @param page repository page number
     * @return list of user events from that page
     */
    public List<UserEvent> getUsersHistoryByPage(int id, int page){
        User user = userRepository.findById(id).orElseThrow();
        return eventRepository.findAllByCreatorOrderByTimeDesc(user, PageRequest.of(page, PAGE_SIZE));
    }

    /**
     *
     * @return list of users that can be team leaders
     */
    public List<UserDTO> getHighRankUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().filter(user -> user.getStatus().ordinal() >= 3 && user.getStatus() != UserStatus.ADMIN)
                .map(dtOsConverter::getDTOFromUser).collect(Collectors.toList());
    }

    /**
     *
     * @param id user's id
     * @return user with that id
     * @throws NoSuchElementException if there is not an user with that id
     */
    public User getByID(int id){
        return userRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     *
     * @param id user's id
     * @return userDTO mapping of the user entity with that id
     * @throws NoSuchElementException if there is not an user with that id
     */
    public UserDTO getUserDTOByID(int id){
        User user = getByID(id);
        return dtOsConverter.getDTOFromUser(user);
    }

    /**
     *
     * @param userID user's id
     * @return list of teams that have this user as leader
     */
    public List<TeamDTO> getUsersLeadingTeams(int userID){
        User user = userRepository.findById(userID).orElseThrow();
        return teamService.getByLeader(user).stream()
                .map(dtOsConverter::getDTOFromTeam).collect(Collectors.toList());
    }

    /**
     * Returns list of ProjectDTOs of which the owner is the user with id userID
     * @param userID user's id
     * @return list of projects with owner with id userID
     */
    public List<ProjectDTO> getUsersOwnedProjects(int userID){
        log.debug("Entered method to get user's owned projects with userid {}", userID);
        User owner = userRepository.findById(userID).orElseThrow();

        return projectService.getByOwner(owner)
                .stream().map(dtOsConverter::getDTOFromProject).collect(Collectors.toList());
    }

    public User save(User user) {
        return userUtils.saveUser(user);
    }

    public boolean exists(int id) {
        return userRepository.findById(id).isPresent();
    }

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        Optional<UserAuthentication> userAuth = authenticationRepository.findByUsernameAndPassword(username, password);
        if(userAuth.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(userAuth.get().getUser());
    }

    public void deleteById(int id) {
        userUtils.deleteUserById(id);
    }

    public List<String> getUserNames(){
        return userRepository.findAll().stream().map(user -> user.getAuthentication().getUsername()).collect(Collectors.toList());
    }

    public UserPreferencesDTO getUserPreferencesById(int userId){
        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isPresent()){
            UserPreferences preferences = userOptional.get().getPreferences();
            return dtOsConverter.getDTOFromPreferences(preferences);
        }else {
            throw new EntityNotFoundException();
        }
    }

    public void updateUserPreferences(UserPreferencesDTO preferences){
        UserPreferences preferences1 = dtOsConverter.getPreferencesFromDTO(preferences);
        preferencesRepository.save(preferences1);
    }
}
