package com.team.TeamUp.controller.unit;

import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.persistence.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TaskUtils;
import com.team.TeamUp.utils.UserUtils;
import com.team.TeamUp.validation.UserValidation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@WebMvcTest
public class RestGetTaskControllerUnitTest {

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
    LocationRepository locationRepository;
    @MockBean
    ProjectRepository projectRepository;
    @MockBean
    CommentRepository commentRepository;
    @MockBean
    UserEventRepository userEventRepository;
    @MockBean
    UserValidation userValidation;
    @MockBean
    DTOsConverter dtOsConverter;
    @MockBean
    TaskUtils taskUtils;
    @MockBean
    UserUtils userUtils;


    @Test
    public void failAuthentication() throws Exception {
        when(userValidation.isValid("1")).thenReturn(true);
        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks")
                .header("token", "1")
        ).andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());

        when(userValidation.isValid("2")).thenReturn(false);

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks")
                .header("token", "")
        ).andReturn();

        Assert.assertEquals(403, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks")
        ).andReturn();

        Assert.assertEquals(403, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllTasks() throws Exception {
        //page, status, statuses, search, sort, desc, last

        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);
        when(userValidation.isValid(anyString())).thenReturn(true);
        when(userRepository.findByHashKey(anyString())).thenReturn(Optional.of(new User()));
        when(dtOsConverter.getDTOFromTask(any())).thenReturn(new TaskDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks")
                .header("token", "")
        ).andDo(print())
                .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllTaskStatus() throws Exception {
        when(userValidation.isValid(anyString())).thenReturn(true);
        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/task-status")
                .header("token", "")
        ).andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllTaskTypes() throws Exception {
        when(userValidation.isValid(anyString())).thenReturn(true);
        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/task-types")
                        .header("token", "")
        ).andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getTaskById() throws Exception {
        when(userValidation.isValid(anyString())).thenReturn(true);
        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);
        when(taskRepository.findById(1)).thenReturn(Optional.of(new Task()));
        when(taskRepository.findById(2)).thenReturn(Optional.empty());
        when(dtOsConverter.getDTOFromTask(any())).thenReturn(new TaskDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks/1")
                        .header("token", "")
        ).andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks/2")
                .header("token", "")
        ).andReturn();

        Assert.assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getTasksProject() throws Exception {
        when(userValidation.isValid(anyString())).thenReturn(true);
        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);
        when(taskRepository.findById(1)).thenReturn(Optional.of(new Task()));
        when(taskRepository.findById(2)).thenReturn(Optional.empty());
        when(dtOsConverter.getDTOFromProject(any())).thenReturn(new ProjectDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks/1/project")
                .header("token", "")
        ).andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks/2/project")
                .header("token", "")
        ).andReturn();

        Assert.assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAssignedTasks() throws Exception {
        // page, search, sort, desc, statuses

        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);
        when(userValidation.isValid(anyString())).thenReturn(true);
        when(userRepository.findByHashKey(anyString())).thenReturn(Optional.of(new User()));
        when(taskRepository.findAllByTaskStatusAndReporter(any(), any(), any())).thenReturn(Arrays.asList(new Task(), new Task()));
        when(dtOsConverter.getDTOFromTask(any())).thenReturn(new TaskDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks/assigned")
                        .header("token", "")
                        .param("page", "1")
        ).andDo(print())
                .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getReportedTasks() throws Exception {
        // page, search, status
        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);
        when(userValidation.isValid(anyString())).thenReturn(true);
        when(userRepository.findByHashKey(anyString())).thenReturn(Optional.of(new User()));
        when(taskRepository.findAllByTaskStatusAndReporter(any(), any(), any())).thenReturn(Arrays.asList(new Task(), new Task()));
        when(dtOsConverter.getDTOFromTask(any())).thenReturn(new TaskDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks/reported")
                .header("token", "")
                .param("page", "1")
        ).andDo(print())
                .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAssignedAndReportedTasks() throws Exception {
        // page, search, status, statuses
        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);
        when(userValidation.isValid(anyString())).thenReturn(true);
        when(userRepository.findByHashKey(anyString())).thenReturn(Optional.of(new User()));
        when(taskRepository.findAllByTaskStatusAndReporter(any(), any(), any())).thenReturn(Arrays.asList(new Task(), new Task()));
        when(dtOsConverter.getDTOFromTask(any())).thenReturn(new TaskDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks/assigned-reported")
                        .header("token", "")
                        .param("page", "1")
        ).andDo(print())
                .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }
}