package com.team.TeamUp.controller;

import com.team.TeamUp.domain.Team;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.validation.UserValidation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class RestGetControllerUnitTest {

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

    @Before
    public void setup() {
        when(userValidation.isValid(Mockito.anyString())).thenReturn(true);
    }

    @Test
    public void getAllUsersEmpty() throws Exception {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        Assert.assertEquals(mvcResult.getResponse().getStatus(), 200);
        verify(userRepository).findAll();
    }

    @Test
    public void getAllUsersWithData() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(new User()));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        Assert.assertEquals(mvcResult.getResponse().getStatus(), 200);
        verify(userRepository).findAll();
        verify(dtOsConverter).getDTOFromUser(any());
    }

    @Test
    public void getAllUserReject() throws Exception {
        User user = new User();
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userValidation.isValid(Mockito.anyString())).thenReturn(false);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        Assert.assertEquals(mvcResult.getResponse().getStatus(), 403);
    }

    @Test
    public void getAllTeamsEmpty() throws Exception {
        when(teamRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/teams")
                .header("token", "")
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(teamRepository).findAll();
        verify(dtOsConverter, times(0)).getDTOFromTeam(any());
        Assert.assertEquals(mvcResult.getResponse().getStatus(), 200);
    }

    @Test
    public void getAllTeamsData() throws Exception {
        when(teamRepository.findAll()).thenReturn(List.of(new Team(), new Team()));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/teams")
                .header("token", "")
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(teamRepository, times(1)).findAll();
        verify(dtOsConverter, times(2)).getDTOFromTeam(any());
        Assert.assertEquals(mvcResult.getResponse().getStatus(), 200);
    }

    @Test
    public void getAllTeamsReject() throws Exception {
        when(teamRepository.findAll()).thenReturn(List.of(new Team(), new Team()));
        when(userValidation.isValid(anyString())).thenReturn(false);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/teams")
                .header("token", "")
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(teamRepository, times(0)).findAll();
        verify(dtOsConverter, times(0)).getDTOFromTeam(any());
        Assert.assertEquals(mvcResult.getResponse().getStatus(), 403);
    }

    @Test
    public void getAllTasks() {
        assert false;
    }

    @Test
    public void getAllProjects() {
        assert false;
    }

    @Test
    public void getAllPosts() {
        assert false;
    }

    @Test
    public void getAllComments() {
        assert false;
    }

    @Test
    public void getAllDepartments() {
        assert false;
    }

    @Test
    public void getAllTaskStatus() {
        assert false;
    }

    @Test
    public void getAllTaskTypes() {
        assert false;
    }

    @Test
    public void getAllUserStatus() {
        assert false;
    }

    @Test
    public void getAllPostComments() {
        assert false;
    }

    @Test
    public void getAssignedTasksForUser() {
        assert false;
    }

    @Test
    public void getReportedTasksByUser() {
        assert false;
    }

    @Test
    public void getUserById() {
        assert false;
    }

    @Test
    public void getTeamById() {
        assert false;
    }

    @Test
    public void getTaskById() {
        assert false;
    }

    @Test
    public void getProjectById() {
        assert false;
    }

    @Test
    public void getIdForCurrentUser() {
        assert false;
    }

    @Test
    public void getUsersByIds() {
        assert false;
    }

    @Test
    public void getPostByTaskId() {
        assert false;
    }

    @Test
    public void logout() {
        assert false;
    }

    @Test
    public void getFile() {
        assert false;
    }

    @Test
    public void getTasksProject() {
        assert false;
    }

    @Test
    public void getTasksFor() {
        assert false;
    }
}