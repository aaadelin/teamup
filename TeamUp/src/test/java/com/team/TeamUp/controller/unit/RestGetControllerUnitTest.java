package com.team.TeamUp.controller.unit;

import com.team.TeamUp.domain.*;
import com.team.TeamUp.domain.enums.Department;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.domain.enums.TaskType;
import com.team.TeamUp.domain.enums.UserStatus;
import com.team.TeamUp.dtos.PostDTO;
import com.team.TeamUp.dtos.ProjectDTO;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.dtos.UserDTO;
import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.utils.TaskUtils;
import com.team.TeamUp.validation.UserValidation;
import org.json.JSONArray;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
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
    @MockBean
    TaskUtils taskUtils;

    @Before
    public void setup() {
        when(userValidation.isValid(Mockito.anyString())).thenReturn(true);
        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);
    }

    @Test
    public void getAllUsersEmpty() throws Exception {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
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

        assertEquals(200, mvcResult.getResponse().getStatus());
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

        assertEquals(403, mvcResult.getResponse().getStatus());
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
        assertEquals(200, mvcResult.getResponse().getStatus());
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
        assertEquals(200, mvcResult.getResponse().getStatus());
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
        assertEquals(403, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllTasksEmpty() throws Exception {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(taskRepository, times(1)).findAll();
        verify(dtOsConverter, times(0)).getDTOFromTask(any());
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllTasksData() throws Exception {
        when(taskRepository.findAll()).thenReturn(List.of(new Task(), new Task()));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(taskRepository, times(1)).findAll();
        verify(dtOsConverter, times(2)).getDTOFromTask(any());
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllTasksReject() throws Exception {
        when(taskRepository.findAll()).thenReturn(List.of(new Task(), new Task()));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(taskRepository, times(0)).findAll();
        verify(dtOsConverter, times(0)).getDTOFromTask(any());
        assertEquals(403, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllProjectsEmpty() throws Exception {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/projects")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(projectRepository, times(1)).findAll();
        verify(dtOsConverter, times(0)).getDTOFromProject(any());
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllProjectsData() throws Exception {
        when(projectRepository.findAll()).thenReturn(List.of(new Project()));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/projects")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(projectRepository, times(1)).findAll();
        verify(dtOsConverter, times(1)).getDTOFromProject(any());
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllProjectsReject() throws Exception {
        when(projectRepository.findAll()).thenReturn(List.of(new Project()));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/projects")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(projectRepository, times(0)).findAll();
        verify(dtOsConverter, times(0)).getDTOFromProject(any());
        assertEquals(403, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllPostsEmpty() throws Exception {
        when(postRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/posts")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(postRepository, times(1)).findAll();
        verify(dtOsConverter, times(0)).getDTOFromPost(any());
        assertEquals(200, mvcResult.getResponse().getStatus());

    }

    @Test
    public void getAllPostsData() throws Exception {
        when(postRepository.findAll()).thenReturn(List.of(new Post()));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/posts")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(postRepository, times(1)).findAll();
        verify(dtOsConverter, times(1)).getDTOFromPost(any());
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllPostsReject() throws Exception {
        when(postRepository.findAll()).thenReturn(List.of(new Post()));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(postRepository, times(0)).findAll();
        verify(dtOsConverter, times(0)).getDTOFromPost(any());
        assertEquals(403, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllCommentsEmpty() throws Exception {
        when(commentRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/comments")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(commentRepository, times(1)).findAll();
        verify(dtOsConverter, times(0)).getDTOFromComment(any());
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllCommentsData() throws Exception {
        when(commentRepository.findAll()).thenReturn(List.of(new Comment(), new Comment()));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/comments")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(commentRepository, times(1)).findAll();
        verify(dtOsConverter, times(2)).getDTOFromComment(any());
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllCommentsReject() throws Exception {
        when(commentRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(commentRepository, times(0)).findAll();
        verify(dtOsConverter, times(0)).getDTOFromComment(any());
        assertEquals(403, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllDepartments() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/departments")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        List<Department> departments = Arrays.asList(Department.values());
        JSONArray departmentsArray = new JSONArray(departments);
        JSONArray departmentsResponse = new JSONArray(mvcResult.getResponse().getContentAsString());
        assertEquals(departmentsArray.toString(), departmentsResponse.toString());
    }

    @Test
    public void getAllDepartmentsReject() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/departments")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(403, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllTaskStatus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/task-status")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        List<TaskStatus> taskStatuses = Arrays.asList(TaskStatus.values());
        JSONArray statusesArray = new JSONArray(taskStatuses);
        JSONArray statusesResponse = new JSONArray(mvcResult.getResponse().getContentAsString());
        assertEquals(statusesArray.toString(), statusesResponse.toString());
    }

    @Test
    public void getAllTaskStatusReject() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/task-status")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(403, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllTaskTypes() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/task-types")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        List<TaskType> taskTypes = Arrays.asList(TaskType.values());
        JSONArray typesArray = new JSONArray(taskTypes);
        JSONArray typesResponse = new JSONArray(mvcResult.getResponse().getContentAsString());
        assertEquals(typesArray.toString(), typesResponse.toString());
    }

    @Test
    public void getAllTaskTypesReject() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/task-types")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(403, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllUserStatus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user-status")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        List<UserStatus> userStatuses = Arrays.asList(UserStatus.values());
        JSONArray statusesArray = new JSONArray(userStatuses);
        JSONArray statusesResponse = new JSONArray(mvcResult.getResponse().getContentAsString());
        assertEquals(statusesArray.toString(), statusesResponse.toString());

    }

    @Test
    public void getAllUserStatusReject() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user-status")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(403, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllPostCommentsData() throws Exception {
        when(postRepository.findById(1)).thenReturn(Optional.of(new Post()));
        when(postRepository.findById(2)).thenReturn(Optional.empty());
        when(dtOsConverter.getDTOFromPost(any())).thenReturn(new PostDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/post/1/comments")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(postRepository, times(1)).findById(any());
        verify(dtOsConverter, times(1)).getDTOFromPost(any());
        verify(dtOsConverter, times(0)).getDTOFromComment(any());
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/post/2/comments")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAssignedTasksForUser() throws Exception {
        when(userRepository.findByHashKey("1")).thenReturn(Optional.of(new User()));
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/1/assigned-tasks")
                .header("token", "")
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());

        when(userRepository.findByHashKey("2")).thenReturn(Optional.empty());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/2/assigned-tasks")
                .header("token", "")
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(404, mvcResult.getResponse().getStatus());
        verify(userRepository, times(1)).findByHashKey("1");
        verify(userRepository, times(1)).findByHashKey("2");
        verify(taskRepository, times(1)).findAll();
        verify(dtOsConverter, times(0)).getDTOFromTask(any());
    }

    @Test
    public void getReportedTasksByUser() throws Exception {

        when(userRepository.findByHashKey("1")).thenReturn(Optional.of(new User()));
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/1/reported-tasks")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());

        when(userRepository.findByHashKey("2")).thenReturn(Optional.empty());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/2/reported-tasks")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), 404);
        verify(userRepository, times(1)).findByHashKey("1");
        verify(userRepository, times(1)).findByHashKey("2");
        verify(taskRepository, times(1)).findAll();
        verify(dtOsConverter, times(0)).getDTOFromTask(any());
    }

    @Test
    public void getUserById() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/1")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 200);

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/2")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 404);
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findById(2);
        verify(dtOsConverter, times(1)).getDTOFromUser(any());
    }

    @Test
    public void getTeamById() throws Exception {
        when(teamRepository.findById(1)).thenReturn(Optional.of(new Team()));
        when(teamRepository.findById(2)).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/team/1")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 200);

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/team/2")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 404);
        verify(teamRepository, times(1)).findById(1);
        verify(teamRepository, times(1)).findById(2);
        verify(dtOsConverter, times(1)).getDTOFromTeam(any());
    }

    @Test
    public void getTaskById() throws Exception {
        when(taskRepository.findById(1)).thenReturn(Optional.of(new Task()));
        when(taskRepository.findById(2)).thenReturn(Optional.empty());
        when(dtOsConverter.getDTOFromTask(any())).thenReturn(new TaskDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/task/1")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 200);

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/task/2")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 404);
        verify(taskRepository, times(1)).findById(1);
        verify(taskRepository, times(1)).findById(2);
        verify(dtOsConverter, times(1)).getDTOFromTask(any());
    }

    @Test
    public void getProjectById() throws Exception {
        when(projectRepository.findById(1)).thenReturn(Optional.of(new Project()));
        when(projectRepository.findById(2)).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/project/1")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 200);

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/project/2")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 404);
        verify(projectRepository, times(1)).findById(1);
        verify(projectRepository, times(1)).findById(2);
        verify(dtOsConverter, times(1)).getDTOFromProject(any());
    }

    @Test
    public void getIdForCurrentUser() throws Exception {
        User user = new User();
        user.setId(10);
        when(userRepository.findByHashKey("1")).thenReturn(Optional.of(user));
        when(userRepository.findByHashKey("2")).thenReturn(Optional.empty());
        when(dtOsConverter.getDTOFromUser(any())).thenReturn(new UserDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/key")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 200);

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/key")
                        .header("token", "2")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 404);
        verify(userRepository, times(1)).findByHashKey("1");
        verify(userRepository, times(1)).findByHashKey("2");
        verify(dtOsConverter, times(0)).getDTOFromUser(any());
    }

    @Test
    public void getUsersByIds() throws Exception {
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(dtOsConverter.getDTOFromUser(any())).thenReturn(new UserDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/1,2,3")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 200);

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/[]")
                        .header("token", "2")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 400);

        verify(userRepository, times(3)).findById(any());
        verify(dtOsConverter, times(3)).getDTOFromUser(any());
    }

    @Test
    public void getPostByTaskId() throws Exception {
        Task task = new Task();
        task.setId(1);
        Post post = new Post();
        post.setTask(task);

        Task task1 = new Task();
        task1.setId(2);

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(postRepository.findByTask(task)).thenReturn(Optional.of(post));
        when(dtOsConverter.getDTOFromPost(post)).thenReturn(new PostDTO());

        when(taskRepository.findById(2)).thenReturn(Optional.of(task1));
        when(postRepository.findByTask(task1)).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/post/taskid=1")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 200);

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/post/taskid=2")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 200);

        verify(postRepository, times(1)).save(any());
        verify(dtOsConverter, times(2)).getDTOFromPost(any());
    }

    @Test
    public void logout() throws Exception {
        User user = new User();
        user.setId(1);
        user.setHashKey("1");

        when(userRepository.findByHashKey("1")).thenReturn(Optional.of(user));
        when(userRepository.findByHashKey("2")).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/logout")
                .header("token", "1")
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/logout")
                        .header("token", "2")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(403, mvcResult.getResponse().getStatus());

        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void getFile() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/1/photo")
                        .header("token", "1")
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/2/photo")
                        .header("token", "1")
        ).andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());

    }

    @Test
    public void getTasksProject() throws Exception {
        when(dtOsConverter.getDTOFromProject(any())).thenReturn(new ProjectDTO());
        when(taskRepository.findById(1)).thenReturn(Optional.of(new Task()));
        when(taskRepository.findById(2)).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/task/1/project")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/task/2/project")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());

        verify(taskRepository, times(2)).findById(any());
        verify(dtOsConverter, times(1)).getDTOFromProject(any());
    }

    @Test
    public void getTasksForAllow() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        when(taskUtils.getFilteredTasksByType(any(), any(), any())).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/1/tasks")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/1/tasks?type=assignedto")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/1/tasks?type=assignedby")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/1/tasks?type=assignedby&term=now")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/1/tasks?type=assignedby&term=now")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getTasksForDeny() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        when(taskUtils.getFilteredTasksByType(any(), any(), any())).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/2/tasks?type=assignedby")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(403, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/2/tasks?type=assignedto")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(403, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/2/tasks")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(403, mvcResult.getResponse().getStatus());
    }

}