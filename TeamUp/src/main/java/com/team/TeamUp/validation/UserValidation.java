package com.team.TeamUp.validation;

import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.persistence.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Class used for user validation.
 * Contains methods to check if a user is logged in or logged out,
 * is a valid user and to check if a client is a user or not
 */
@Component
public class UserValidation {

    private UserRepository userRepository;
    public static final Logger LOGGER = LoggerFactory.getLogger(UserValidation.class);

    @Autowired
    public UserValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
        LOGGER.info("Creating instance of userValidationUtils");
    }

    /**
     * checks if the key corresponds to an existing user
     *
     * @param key a string
     * @return true if there is a user with the key, false otherwise
     */
    public boolean isValid(String key) {
        LOGGER.info(String.format("Checking if user with key %s is valid", key));
        Optional<User> userOptional = userRepository.findByHashKey(key);
        boolean answer = userOptional.isPresent();
        LOGGER.info(String.format("The key is %s", answer));
        return answer;
    }

    /**
     * checks if the headers are sent from a valid user
     *
     * @param headers headers from client's request
     * @return true if there is a user that matches the key in the headers, false otherwise
     */
    public boolean isValid(Map<String, String> headers) {
        LOGGER.info(String.format("Checking if headers send from user are valid (%s)", headers));
        if (headers.containsKey("token")) {
            return isValid(headers.get("token"));
        }
        LOGGER.info("Headers did not contain necessary information");
        return false;
    }

    /**
     * checks whether the user with the specified key has the specified status
     *
     * @param key user's key
     * @param userStatus UserStatus to compare the user with
     * @return boolean true if the user has the specified status, false otherwise
     */
    public boolean isValid(String key, UserStatus userStatus){
        LOGGER.info(String.format("Checking if user with key %s has status %s", key, userStatus));
        Optional<User> userOptional = userRepository.findByHashKey(key);
        boolean answer = userOptional.isPresent() && userOptional.get().getStatus().equals(userStatus);
        LOGGER.info(String.format("Exiting with status: %s", answer));
        return answer;
    }

    /**
     * checks whether the client that sent the headers has the specified status
     *
     * @param headers Headers from client's request
     * @param status UserStatus to compare the user with
     * @return boolean true if the user has the specified status, false otherwise
     */
    public boolean isValid(Map<String, String> headers, UserStatus status) {
        LOGGER.info(String.format("Checking if headers (%s) send from user are valid and user has the specified status %s", headers, status));
        if (headers.containsKey("token")) {
            String token = headers.get("token");

            Optional<User> userOptional = userRepository.findByHashKey(token);
            return userOptional.isPresent() && userOptional.get().getStatus().equals(status);
        }
        LOGGER.info("Headers did not contain necessary information");
        return false;
    }

    /**
     * checks if the client with the headers is the owner of the project
     *
     * @param headers Map containing the headers sent by the client
     * @param projectDTO ProjectDTO entity
     * @return true if the user that sent the headers is the owner of the project
     */
    public boolean isOwner(Map<String, String> headers, ProjectDTO projectDTO) {
        LOGGER.info(String.format("Checking if user with specified headers (%s) is the owner of the project %s", headers, projectDTO));
        if(headers.containsKey("token")){
            String token = headers.get("token");
            Optional<User> userOptional = userRepository.findByHashKey(token);
            return userOptional.isPresent() && userOptional.get().getId() == projectDTO.getOwnerID();
        }
        LOGGER.info("Headers did not contain necessary information");
        return false;
    }

    /**
     *checks if the user is not logged out
     *
     * @param key User Hash key
     * @return true if user with the key is logged in and false otherwise
     */
    public boolean isUserLoggedIn(String key){
        Optional<User> userOptional = userRepository.findByHashKey(key);
        return userOptional.isPresent() && !isLoggedOut(userOptional.get());
    }

    /**
     * Checks if the user is not active and has not been active in the past hour
     *
     * @param user User entity
     * @return boolean value, true if user is logged out and false otherwise
     */
    private boolean isLoggedOut(User user){
        if(!user.isActive() || user.getLastActive() == null){
            return true;
        }
        if(user.getLastActive().isBefore(LocalDateTime.now().minusMinutes(user.getMinutesUntilLogout()))){ //if user hasn't been active in last 4 hours (didn't make any request)
            user.setActive(false);
            userRepository.save(user);
            return true;
        }
        user.setLastActive(LocalDateTime.now());
        user = userRepository.saveAndFlush(user);
        return false;
    }

}