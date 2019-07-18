package com.team.TeamUp.controller;

import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.UserValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//DELETE methods - for deleting

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RestDeleteController extends AbstractRestController {


    public RestDeleteController(TeamRepository teamRepository, UserRepository userRepository, TaskRepository taskRepository,
                                ProjectRepository projectRepository, CommentRepository commentRepository, PostRepository postRepository,
                                UserValidationUtils userValidationUtils) {
        super(teamRepository, userRepository, taskRepository, projectRepository, commentRepository, postRepository, userValidationUtils);
    }

    /**
     * @param id int representing the id of user entity to be deleted
     * @return responseEntity signaling the success or failure
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        if (userValidationUtils.isValid(headers, UserStatus.ADMIN)) {
            try {
                userRepository.deleteById(id);
                return new ResponseEntity<>("OK", HttpStatus.OK);
            } catch (Exception ignore) {
                return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
    }

}
