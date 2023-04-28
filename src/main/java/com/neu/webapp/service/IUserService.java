package com.neu.webapp.service;

import com.neu.webapp.model.Product;
import com.neu.webapp.model.User;
import org.springframework.http.ResponseEntity;

/**
 * @author Ruolin Li
 */
public interface IUserService {
    ResponseEntity<?> getUserById(long userId);

    ResponseEntity<?> createUser(User user);

    ResponseEntity<?> updateUser(long userId,User userDetails);

    //decode the basic authorization
    User getUserByAuthorization(String Authorization);

    Boolean validEmail(String email);


}
