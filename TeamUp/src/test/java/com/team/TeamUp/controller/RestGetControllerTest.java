package com.team.TeamUp.controller;

import com.team.TeamUp.persistance.*;
import com.team.TeamUp.validation.UserValidation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

@RunWith(SpringRunner.class)
@WebMvcTest
public class RestGetControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;
    @MockBean
    TeamRepository teamRepository;
    @MockBean
    PostRepository postRepository;
    @MockBean
    TaskRepository taskRepository;
    @MockBean
    ProjectRepository projectRepository;
    @MockBean
    CommentRepository commentRepository;
    @MockBean
    UserValidation userValidation;

    @Test
    public void updateUser() throws Exception {

        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(userValidation.isValid(Mockito.anyString())).thenReturn(true);
        Mockito.when(userValidation.isValid(Mockito.anyMap())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users")
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());

        Mockito.verify(userRepository).findAll();
    }
}