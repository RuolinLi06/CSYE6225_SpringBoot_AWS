package com.neu.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * @author Ruolin Li
 */
/*
  "id": 1,
  "first_name": "Jane",
  "last_name": "Doe",
  "username": "jane.doe@example.com",
  "account_created": "2016-08-29T09:12:33.001Z",
  "account_updated": "2016-08-29T09:12:33.001Z"
 */
@Data
@Entity
@Table(name = "user_account")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @JsonProperty("first_name")
    private String firstName;
    @NotNull
    @JsonProperty("last_name")
    private String lastName;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    //Use email for username
    @NotNull
    private String username;

    @Column(name = "account_created",nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date accountCreated;

    @Column(name = "account_updated",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date accountUpdated;

}
