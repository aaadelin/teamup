package com.team.teamup.utils;

import com.team.teamup.domain.*;
import com.team.teamup.domain.enums.UserEventType;
import com.team.teamup.domain.enums.UserStatus;
import com.team.teamup.dtos.UserDTO;
import com.team.teamup.persistence.ProjectRepository;
import com.team.teamup.persistence.TaskRepository;
import com.team.teamup.persistence.UserEventRepository;
import com.team.teamup.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserUtils {

    private static final int MAX_PAGE_SIZE = 10000;

    private final UserRepository userRepository;
    private UserEventRepository userEventRepository;
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private DTOsConverter dtOsConverter;

    @Autowired
    public UserUtils(UserRepository userRepository, UserEventRepository userEventRepository, TaskRepository taskRepository,
                     ProjectRepository projectRepository, DTOsConverter dtOsConverter) {
        this.userRepository = userRepository;
        this.userEventRepository = userEventRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.dtOsConverter = dtOsConverter;
    }

    public String decryptPassword(String password) {
        // TODO
        String key = "aesEncryptionKey";
//        String initVector = "encryptionIntVec";

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            Key key1 =  new SecretKeySpec(key.getBytes(), "AES");
//            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());
            byte[] initVector = new byte[cipher.getBlockSize()];
            IvParameterSpec iv = new IvParameterSpec(initVector);

            cipher.init(Cipher.DECRYPT_MODE, key1, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(password));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Creates an user event and saves it to the database
     * @param creator User that will be the event 'owner'
     * @param description Event's description
     * @param eventType Type of the event
     */
    public void createEvent(User creator, String description, UserEventType eventType){
        log.debug(String.format("Entered method to create event with user %s, description %s and type %s", creator, description, eventType));
        UserEvent userEvent = new UserEvent();
        userEvent.setCreator(creator);
        userEvent.setDescription(description);
        userEvent.setEventType(eventType);
        userEvent.setTime(LocalDateTime.now());

        userEvent = userEventRepository.save(userEvent);
        List<UserEvent> history = creator.getHistory();
        if(history == null){
            history = new ArrayList<>();
        }
        history.add(userEvent);
        creator.setHistory(history);
        userRepository.save(creator);
        log.debug("Event successfully created");
    }

    /**
     * Method to handle user deletion.
     * The user will be removed from the assignees list from all the tasks
     * Reporter will be changed to his superior
     * Project ownership will be set to his superior
     *
     * If he doesn't has a superior, an admin will be selected
     * If an admin is not available (Improbable case, since admin users cannot be deleted and the deletion bust be initiated by an admin)
     * one will be created
     * @param user User that will be deleted
     */
    public void deleteUserInitiated(User user) {
        List<Task> assignedTasks = taskRepository.findAllByAssigneesContaining(user);
        List<Task> reportedTasks = taskRepository.findAllByReporter(user);
        List<Project> projects = projectRepository.findAllByOwner(user);
        Team team = user.getTeam();
        User leader = team == null? null : team.getLeader();

        for (Task task : assignedTasks) { //remove the user from assignees from all the tasks
            task.getAssignees().remove(user);
            taskRepository.save(task);
        }
        if (leader != null) { //if he has a leader, leader is now the reporter
            for (Task task : reportedTasks){
                task.setReporter(leader);
                taskRepository.save(task);
            }
        }else{
            List<User> admins = userRepository.findAllByStatus(UserStatus.ADMIN);
            leader = admins.size() > 0 ? admins.get(0) : createAdmin() ; // if he doesn't have a leader, set an admin. There should always be an admin, otherwise an error will be thrown
            for (Task task : reportedTasks) {
                task.setReporter(leader);
                taskRepository.save(task);
            }
        }

        // at this point the leader variable is already initialized with an existing user or a newly created
        // so the projects will be set to that user
        for (Project project : projects){
            project.setOwner(leader);
            projectRepository.save(project);
        }
    }

    /**
     * Creates an admin user
     * @return The user just created and saved
     */
    private User createAdmin(){
        log.debug("Creating admin");
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("System");
        userDTO.setLastName("Administrator");
        int nano = LocalDateTime.now().getNano();
        userDTO.setUsername("admin" + nano);
        userDTO.setPassword(String.valueOf(nano));
        userDTO.setStatus(UserStatus.ADMIN);

        User user = dtOsConverter.getUserFromDTO(userDTO, UserStatus.ADMIN);
        user = userRepository.save(user);
        log.debug("Created admin with username {}", user.getUsername());
        return user;
    }

    /**
     * Method to create an admin if none are existing at the time
     * Used at running the server when a new database is set and there are no users.
     */
    public void createAdminIfNoneExistent(){
        log.debug("Entering create admin if none existent");
        if(userRepository.findAllByStatus(UserStatus.ADMIN).isEmpty()){
            createAdmin();
        }
    }

    /**
     * Get users that contain specified ids
     * @param ids list of ids to get users
     * @return ResponseEntry with the users and OK status if all of the ids had a correspondent,
     * NOT FOUND status if at least one didn't had a correspondent
     */
    public List<UserDTO> getUsersByIds(List<Integer> ids){
        List<UserDTO> users = new ArrayList<>();
        for (int id : ids) {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                log.debug(String.format("Adding user: %s", userOptional.get()));
                users.add(dtOsConverter.getDTOFromUser(userOptional.get()));
            }
        }
        return users;
    }

    /**
     * Method to filter users by first name and last name containing the specified filter
     * @param filter String, word to be contained in the first name or the last name
     * @return list of userdto filtered by criteria
     */
    public List<UserDTO> filterUsers(String filter){
        return userRepository.findAllByFirstNameContainingOrLastNameContaining(filter, filter)
                .stream().map(dtOsConverter::getDTOFromUser).collect(Collectors.toList());
    }

    /**
     * Method to return sorted users
     * @param page Integer, page number to get users from
     * @param sort String, property of the User to sort by
     * @param isAdmin Boolean, to know if to return all the users, including admin ones
     * @param pageSize Integer, number of items per page
     * @return List of userDTOs sorted by the sort criteria
     */
    public List<UserDTO> sortUsers(Integer page, String sort, Boolean isAdmin, Integer pageSize){
        List<UserDTO> users;
        if(page == -1){
            if(isAdmin){
                //if is admin, return all users
                users = userRepository.findAll(PageRequest.of(0, MAX_PAGE_SIZE, Sort.by(sort))).stream()
                        .map(dtOsConverter::getDTOFromUser).collect(Collectors.toList());
            }else{
                users = userRepository.findAll(PageRequest.of(0, MAX_PAGE_SIZE, Sort.by(sort))).stream()
                        .filter(user -> user.getStatus() != UserStatus.ADMIN).map(dtOsConverter::getDTOFromUser).collect(Collectors.toList());
            }
        }else {
            if(isAdmin){
                //if is admin, return all users
                users = userRepository.findAll(PageRequest.of(page, pageSize, Sort.by(sort))).stream()
                        .map(dtOsConverter::getDTOFromUser).collect(Collectors.toList());
            }else{
                users = userRepository.findAll(PageRequest.of(page, pageSize, Sort.by(sort))).stream()
                        .filter(user -> user.getStatus() != UserStatus.ADMIN).map(dtOsConverter::getDTOFromUser).collect(Collectors.toList());
            }
        }
        return users;
    }
}
