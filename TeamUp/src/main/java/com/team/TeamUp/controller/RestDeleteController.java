package com.team.TeamUp.controller;

import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.persistance.*;
import com.team.TeamUp.validation.UserValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//DELETE methods - for deleting

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestDeleteController extends AbstractRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestGetController.class);

    public RestDeleteController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                                ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository,
                                UserValidation userValidation) {
        super(teamRepository, userRepository, taskRepository, projectRepository, commentRepository, postRepository, userValidation);
        LOGGER.info("Creating RestDeleteController");
    }

    /**
     * @param id int representing the id of user entity to be deleted
     * @return responseEntity signaling the success or failure
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        LOGGER.info(String.format("Entering delete user method with user id: %s \n and headers: %s", id, headers.toString()));
        if (userValidation.isValid(headers, UserStatus.ADMIN)) {
            try {
                userRepository.deleteById(id);
                LOGGER.info(String.format("User with id %s has been successfully deleted", id));
                return new ResponseEntity<>("OK", HttpStatus.OK);
            } catch (Exception ignore) {
                LOGGER.info(String.format("User with id %s has not been found", id));
                return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
            }
        }
        LOGGER.error("User not eligible");
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

}
