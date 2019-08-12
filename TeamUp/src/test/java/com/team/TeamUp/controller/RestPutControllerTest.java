package com.team.TeamUp.controller;

import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.UserValidationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class RestPutControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;
//    @MockBean
//    TeamRepository teamRepository;
//    @MockBean
//    PostRepository postRepository;
//    @MockBean
//    TaskRepository taskRepository;
//    @MockBean
//    ProjectRepository projectRepository;
//    @MockBean
//    CommentRepository commentRepository;

    @Test
    public void updateUser() throws Exception {

        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users")
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());

        Mockito.verify(userRepository).findAll();
    }
}