package com.neu.webapp.service.inplement;

import com.neu.webapp.model.User;
import com.neu.webapp.repository.UserRepository;
import com.neu.webapp.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

/**
 * @author Ruolin Li
 */
@Service
public class UserService implements IUserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //A user can only get their own account information. 403 Forbidden
    @Override
    public ResponseEntity<?> getUserById(long userId) {
        User user = userRepository.findUserById(userId);
        if(user==null){
            return new ResponseEntity<>("{ \"error\": \"User not founds\" }",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createUser(User user) {
        if(validEmail(user.getUsername())==false){
            //invalid email 400 BAD_REQUEST
            return new ResponseEntity<>("{ \"error\": \"Invalid email\" }",HttpStatus.BAD_REQUEST);
        }
        if(userRepository.findUserByUsername(user.getUsername())!=null){
            //already exist same username(email) 400 BAD_REQUEST
            return new ResponseEntity<>("{ \"error\": \"Exist same username(email)\" }",HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String token = Base64.getEncoder().encodeToString((user.getUsername()+user.getPassword()).getBytes(StandardCharsets.UTF_8));
        System.out.println(token);
        //return token in the header
        MultiValueMap map = new LinkedMultiValueMap();
        map.add("Token",token);
        try{
            logger.info("Creating user :" + user.getUsername());
            return new ResponseEntity<>(userRepository.save(user),map,HttpStatus.CREATED);
        }catch(Exception e){
            logger.warn("Create User Failed:" + e.getMessage());
            return new ResponseEntity<>("{ \"error\": \"BAD_REQUEST\" }",map,HttpStatus.BAD_REQUEST);
        }
    }

    //Attempt to update any other field should return 400 Bad RequestHTTP response code
    //A user can only update their own account information. 403 Forbidden
    @Override
    public ResponseEntity<?> updateUser(long userId, User userDetails) {
        User user = userRepository.findUserById(userId);
        if(user==null){
            return new ResponseEntity<>("{ \"error\": \"User not found\" }",HttpStatus.NOT_FOUND);
        }
        //can't update username(email)
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));

        String newToken = Base64.getEncoder().encodeToString((user.getUsername()+user.getPassword()).getBytes(StandardCharsets.UTF_8));
        System.out.println(newToken);
        //when update user send a new token in the header
        MultiValueMap map = new LinkedMultiValueMap();
        map.add("Token",newToken);
        try{
            logger.info("Updating user :" + user.getUsername());
            return new ResponseEntity<>(userRepository.save(user),map,HttpStatus.NO_CONTENT);
        }catch(Exception e){
            logger.warn("Update User Failed:" + e.getMessage());
            return new ResponseEntity<>("{ \"error\": \"BAD_REQUEST\" }",map,HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public User getUserByAuthorization(String authorization) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            return userRepository.findUserByUsername(values[0]);
    }

    @Override
    //use re to valid email(username)
    public Boolean validEmail(String email) {
        String emailPattern = "[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        return email.matches(emailPattern);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findUserByUsername(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        ArrayList<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("USER"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                list
        );
    }
}