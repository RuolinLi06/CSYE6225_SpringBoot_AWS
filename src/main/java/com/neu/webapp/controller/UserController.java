package com.neu.webapp.controller;

import com.neu.webapp.model.User;
import com.neu.webapp.service.IUserService;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Ruolin Li
 */
@RestController
@RequestMapping("/v1/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private StatsDClient statsDClient;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        statsDClient.incrementCounter("endpoint.user.post");
        return userService.createUser(user);
    }

    @GetMapping(path ="/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(value = "id") Long userId,
                                            @RequestHeader(value = "Authorization") String authorization,
                                            @RequestHeader(value = "Token") String token) {
        statsDClient.incrementCounter("endpoint.user.id.get");
        User user = userService.getUserByAuthorization(authorization);
        logger.info(user.getUsername()+"is getting User "+userId+" info");
        if(user.getId()!=userId){
            //retrive other user's account,Forbidden
            return new ResponseEntity<>("{ \"error\": \"Can't retrive other user's account\" }",HttpStatus.FORBIDDEN);
        }
        if(!token.equals(Base64.getEncoder().encodeToString((user.getUsername()+user.getPassword()).getBytes(StandardCharsets.UTF_8)))){
            //wrong token
            return new ResponseEntity<>("{ \"error\": \"Wrong token\" }",HttpStatus.UNAUTHORIZED);
        }
        return userService.getUserById(userId);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateUser(@PathVariable(value = "id") Long userId, @RequestBody User userDetails,
                                           @RequestHeader(value = "Authorization") String authorization,
                                           @RequestHeader(value = "Token") String token) {
        statsDClient.incrementCounter("endpoint.user.id.put");
        User user = userService.getUserByAuthorization(authorization);
        logger.info(user.getUsername()+"is updating User "+userId+" info");
        if(user.getId()!=userId){
            //retrive other user's account,Forbidden
            return new ResponseEntity<>("{ \"error\": \"Can't retrieve other user's account\" }",HttpStatus.FORBIDDEN);
        }
        if(!user.getUsername().equals(userDetails.getUsername())){
            //want to change the username(email), Bad Request
            return new ResponseEntity<>("{ \"error\": \"Can't change username(email)\" }",HttpStatus.BAD_REQUEST);
        }
        if(!token.equals(Base64.getEncoder().encodeToString((user.getUsername()+user.getPassword()).getBytes(StandardCharsets.UTF_8)))){
            //wrong token
            return new ResponseEntity<>("{ \"error\": \"Wrong token\" }",HttpStatus.UNAUTHORIZED);
        }
        return userService.updateUser(userId, userDetails);
    }
}