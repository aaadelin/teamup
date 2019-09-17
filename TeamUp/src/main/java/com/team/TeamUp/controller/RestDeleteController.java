package com.team.TeamUp.controller;

import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.UserEventType;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.persistence.UserRepository;
import com.team.TeamUp.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

//DELETE methods - for deleting

@RestController
@RequestMapping("/api")
@CrossOrigin
@Slf4j
public class RestDeleteController {
    
    private UserRepository userRepository;
    private UserUtils userUtils;

    @Autowired
    public RestDeleteController(UserRepository userRepository, UserUtils userUtils) {
        this.userRepository = userRepository;
        this.userUtils = userUtils;
        log.info("Creating RestDeleteController");
    }

    /**
     * @param id int representing the id of user entity to be deleted
     * @return responseEntity signaling the success or failure
     */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering delete user method with user id: %s \n and headers: %s", id, headers.toString()));
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()){
            userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                    String.format("Deleted user \"%s %s\"", userOptional.get().getFirstName(), userOptional.get().getLastName()),
                    UserEventType.DELETE);
            userRepository.deleteById(id);
            log.info(String.format("User with id %s has been successfully deleted", id));
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        log.info(String.format("User with id %s has not been found", id));
        return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/user/{id}/photo", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePhoto(@PathVariable int id,
                                         @RequestHeader Map<String, String> headers) throws IOException {
        log.info(String.format("Entered delete photo with headers: %s", headers));
        Optional<User> userOptional = userRepository.findByHashKey(headers.get("token"));
        if(userOptional.isPresent() && (userOptional.get().getStatus() == UserStatus.ADMIN || userOptional.get().getId() == id)){
            ClassPathResource resource = new ClassPathResource("static/img/" + userOptional.get().getPhoto());
            if(resource.exists()){
                boolean deleted = resource.getFile().delete();
                userOptional.get().setPhoto(null);
                userRepository.save(userOptional.get());
                if(deleted){
                    log.info("Photo deleted successfully");
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
