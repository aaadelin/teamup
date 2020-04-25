package com.team.teamup.controller;

import com.team.teamup.domain.Location;
import com.team.teamup.domain.User;
import com.team.teamup.domain.enums.UserEventType;
import com.team.teamup.domain.enums.UserStatus;
import com.team.teamup.service.LocationService;
import com.team.teamup.service.UserService;
import com.team.teamup.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class RestDeleteController {
    
    private final UserService userService;
    private final LocationService locationService;
    private final UserUtils userUtils;
    private static final String TOKEN = "token";

    @Autowired
    public RestDeleteController(UserService userService, UserUtils userUtils, LocationService locationService) {
        this.userService = userService;
        this.userUtils = userUtils;
        this.locationService = locationService;
        log.info("Creating RestDeleteController");
    }

    /**
     * @param id int representing the id of user entity to be deleted
     * @return responseEntity signaling the success or failure
     */
    @DeleteMapping(value = "/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id, @RequestHeader Map<String, String> headers) {
        log.info(String.format("Entering delete user method with user id: %s %n and headers: %s", id, headers.toString()));
        User user = userService.getByID(id);
        if (user.getStatus() != UserStatus.ADMIN){ //users with status admin cannot be deleted
            userUtils.createEvent(userService.getByHashKey(headers.get(TOKEN)),
                    String.format("Deleted user \"%s %s\"", user.getFirstName(), user.getLastName()),
                    UserEventType.DELETE);
            userUtils.deleteUserInitiated(user);
            userService.deleteById(id);
            log.info(String.format("User with id %s has been successfully deleted", id));
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        log.info(String.format("User with id %s has not been found", id));
        return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
    }

    /**
     * DELETE method to delete user's photo
     * @param id user's id
     * @param headers headers of the requester
     * @return OK if the photo has been successfully deleted, NOT FOUND if it has not been found
     * @throws IOException if the photo does not exist
     */
    @DeleteMapping(value = "/user/{id}/photo")
    public ResponseEntity<?> deletePhoto(@PathVariable int id,
                                         @RequestHeader Map<String, String> headers) throws IOException {
        log.info(String.format("Entered delete photo with headers: %s", headers));
        User user = userService.getByHashKey(headers.get(TOKEN));
        if(user.getStatus() == UserStatus.ADMIN || user.getId() == id){
            String path = System.getProperty("user.home") + "/.TeamUpData/" + user.getPhoto();
            File resource = new File(path);
            if(resource.exists()){
                boolean deleted = resource.delete();
                user.setPhoto(null);
                userService.save(user);
                if(deleted){
                    log.info("Photo deleted successfully");
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/locations/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable int id, @RequestHeader Map<String, String> headers){
        log.info(String.format("Entered delete location with headers: %s", headers));
        User user = userService.getByHashKey(headers.get(TOKEN));
        if(user.getStatus() == UserStatus.ADMIN ){
            Optional<Location> locationOptional = locationService.findById(id);
            if(locationOptional.isPresent()){
                locationService.delete(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
