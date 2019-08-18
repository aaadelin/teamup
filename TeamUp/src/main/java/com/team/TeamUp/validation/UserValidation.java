package com.team.TeamUp.validation;

import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.persistance.UserRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Map;
import java.util.Optional;

@Component
public class UserValidation {

    private UserRepository userRepository;
    public static final Logger LOGGER = LoggerFactory.getLogger(UserValidation.class);

    @Autowired
    public UserValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
        LOGGER.info("Creating instance of userValidationUtils");
    }

    public boolean isValid(String key) {
        LOGGER.info(String.format("Checking if user with key %s is valid", key));
        Optional<User> userOptional = userRepository.findByHashKey(key);
        boolean answer = userOptional.isPresent();
        LOGGER.info(String.format("The key is %s", answer));
        return answer;
    }

    public boolean isValid(Map<String, String> headers) {
        LOGGER.info(String.format("Checking if headers send from user are valid (%s)", headers));
        if (headers.containsKey("token")) {
            return isValid(headers.get("token"));
        }
        LOGGER.info("Headers did not contain necessary information");
        return false;
    }

    public boolean isValid(String key, UserStatus userStatus){
        LOGGER.info(String.format("Checking if user with key %s has status %s", key, userStatus));
        Optional<User> userOptional = userRepository.findByHashKey(key);
        boolean answer = userOptional.isPresent() && userOptional.get().getStatus().equals(userStatus);
        LOGGER.info(String.format("Exiting with status: %s", answer));
        return answer;
    }

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
}