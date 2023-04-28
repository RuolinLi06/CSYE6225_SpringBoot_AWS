package com.neu.webapp.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * @author Ruolin Li
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserController userController;
    @Test
    public void testNotNull() {
        Assertions.assertThat(userController).isNotNull();
    }
    @Test
    void createUser() throws Exception {
        String requestBody = "{\"first_name\": \"Jane\",\"last_name\": \"Doe\",\"password\": \"somepassword\",\"username\": \"jane.doe@example.com\"}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        String errorBody = "{\"first_name\": \"Jane\",\"last_name\": \"Doe\",\"password\": \"somepassword\",\"username\": \"username is not a email\"}";

        this.mockMvc.perform(MockMvcRequestBuilders.post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(errorBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void getUserById() throws Exception{

        this.mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}