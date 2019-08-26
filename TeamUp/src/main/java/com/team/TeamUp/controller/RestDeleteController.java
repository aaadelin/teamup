package com.team.TeamUp.controller;

import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.UserEventType;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.UserUtils;
import com.team.TeamUp.validation.UserValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

//DELETE methods - for deleting

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestDeleteController extends AbstractRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestGetController.class);
    @Autowired
    private UserUtils userUtils;

    public RestDeleteController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                                ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository,
                                UserValidation userValidation, DTOsConverter dtOsConverter, UserEventRepository userEventRepository) {
        super(teamRepository, userRepository, taskRepository, projectRepository, commentRepository, postRepository, userValidation, dtOsConverter, userEventRepository);
        LOGGER.info("Creating RestDeleteController");
    }

    /**
     * @param id int representing the id of user entity to be deleted
     * @return responseEntity signaling the success or failure
     */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering delete user method with user id: %s \n and headers: %s", id, headers.toString()));
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()){
            userUtils.createEvent(userRepository.findByHashKey(headers.get("token")).orElseThrow(),
                    String.format("Deleted user \"%s %s\"", userOptional.get().getFirstName(), userOptional.get().getLastName()),
                    UserEventType.DELETE);
            userRepository.deleteById(id);
            LOGGER.info(String.format("User with id %s has been successfully deleted", id));
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        LOGGER.info(String.format("User with id %s has not been found", id));
        return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/user/{id}/photo", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePhoto(@PathVariable int id,
                                         @RequestHeader Map<String, String> headers) throws IOException {
        LOGGER.info(String.format("Entered delete photo with headers: %s", headers));
        Optional<User> userOptional = userRepository.findByHashKey(headers.get("token"));
        if(userOptional.isPresent() && (userOptional.get().getStatus() == UserStatus.ADMIN || userOptional.get().getId() == id)){
            ClassPathResource resource = new ClassPathResource("static/img/" + userOptional.get().getPhoto());
            if(resource.exists()){
                boolean deleted = resource.getFile().delete();
                userOptional.get().setPhoto(null);
                userRepository.save(userOptional.get());
                if(deleted){
                    LOGGER.info("Photo deleted successfully");
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
