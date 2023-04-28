package com.neu.webapp.repository;

import com.neu.webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Ruolin Li
 */
//@Repository
@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    User findUserById(long id);
    User findUserByUsername(String username);
}
