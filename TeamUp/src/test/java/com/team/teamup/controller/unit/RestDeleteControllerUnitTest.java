package com.team.teamup.controller.unit;

import com.team.teamup.domain.User;
import com.team.teamup.domain.enums.UserStatus;
import com.team.teamup.persistence.*;
import com.team.teamup.service.*;
import com.team.teamup.utils.DTOsConverter;
import com.team.teamup.utils.MailUtils;
import com.team.teamup.utils.TaskUtils;
import com.team.teamup.utils.UserUtils;
import com.team.teamup.validation.UserValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest
public class RestDeleteControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MailUtils mailUtils;
    @MockBean
    UserEventRepository userEventRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    TeamRepository teamRepository;
    @MockBean
    PostRepository postRepository;
    @MockBean
    TaskRepository taskRepository;
    @MockBean
    LocationRepository locationRepository;
    @MockBean
    ResetRequestRepository resetRequestRepository;
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
    @MockBean
    UserUtils userUtils;
    @MockBean
    UserService userService;
    @MockBean
    LocationService locationService;
    @MockBean
    TeamService teamService;
    @MockBean
    CommentService commentService;
    @MockBean
    PostService postService;
    @MockBean
    ProjectService projectService;
    @MockBean
    ResetRequestService resetRequestService;
    @MockBean
    TaskService taskService;

    @Before
    public void setUp() throws Exception {
        when(userValidation.isValid(Mockito.anyString())).thenReturn(true);
        when(userValidation.isValid("1", UserStatus.ADMIN)).thenReturn(true);
        when(userValidation.isValid("2", UserStatus.ADMIN)).thenReturn(false);
        when(userRepository.findById(10)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(11)).thenReturn(Optional.empty());
        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);
        when(userRepository.findByHashKey(anyString())).thenReturn(Optional.of(new User()));
    }

    @Test
    public void deleteUserExisting() throws Exception {
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/users/10")
                .header("token", "1")
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(404, mvcResult.getResponse().getStatus());

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