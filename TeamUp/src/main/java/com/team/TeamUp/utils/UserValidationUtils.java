package com.team.TeamUp.utils;

import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class UserValidationUtils {

    private UserRepository userRepository;

    @Autowired
    public UserValidationUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isValid(String key) {
        Optional<User> userOptional = userRepository.findByHashKey(key);
        return userOptional.isPresent();
    }

    public boolean isValid(Map<String, String> headers) {
        if (headers.containsKey("token")) {
            return isValid(headers.get("token"));
        }
        return false;
    }

    public boolean isValid(Map<String, String> headers, UserStatus status) {
        if (headers.containsKey("token")) {
            String token = headers.get("token");

            Optional<User> userOptional = userRepository.findByHashKey(token);
            return userOptional.isPresent() && userOptional.get().getStatus().equals(status);
        }
        return false;
    }

    public boolean isOwner(Map<String, String> headers, ProjectDTO projectDTO) {

        if(headers.containsKey("token")){
            String token = headers.get("token");
            Optional<User> userOptional = userRepository.findByHashKey(token);
            return userOptional.isPresent() && userOptional.get().getId() == projectDTO.getOwnerID();
        }
        return false;
    }
}