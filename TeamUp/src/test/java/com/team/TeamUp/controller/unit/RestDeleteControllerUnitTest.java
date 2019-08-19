package com.team.TeamUp.controller.unit;

import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TaskUtils;
import com.team.TeamUp.validation.UserValidation;
import org.junit.Before;
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

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest
public class RestDeleteControllerUnitTest {

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
    @MockBean
    DTOsConverter dtOsConverter;
    @MockBean
    TaskUtils taskUtils;

    @Before
    public void setUp() throws Exception {
        when(userValidation.isValid(Mockito.anyString())).thenReturn(true);
        when(userValidation.isValid("1", UserStatus.ADMIN)).thenReturn(true);
        when(userValidation.isValid("2", UserStatus.ADMIN)).thenReturn(false);
        when(userRepository.findById(10)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(11)).thenReturn(Optional.empty());
        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);
    }

    @Test
    public void deleteUserExisting() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/user/10")
                .header("token", 1)
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());

        }

    @Test
    public void deleteUserNotExisting() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/user/11")
                        .header("token", 1)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteUserExistingReject() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/user/10")
                        .header("token", "2")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(403, mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteUserNotExistingReject() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/user/11")
                        .header("token", "2")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(403, mvcResult.getResponse().getStatus());
    }
}