package com.team.TeamUp.controller.unit;

import com.team.TeamUp.persistence.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TaskUtils;
import com.team.TeamUp.utils.UserUtils;
import com.team.TeamUp.validation.UserValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest
public class RestPutControllerUnitTest {

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
    LocationRepository locationRepository;
    @MockBean
    CommentRepository commentRepository;
    @MockBean
    UserEventRepository userEventRepository;
    @MockBean
    UserUtils userUtils;
    @MockBean
    UserValidation userValidation;
    @MockBean
    DTOsConverter dtOsConverter;
    @MockBean
    TaskUtils taskUtils;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void updateUser() {
    }

    @Test
    public void updateProject() {
    }

    @Test
    public void updateTask() {
    }

    @Test
    public void updateTeam() {
    }
}