package com.team.teamup.controller.unit;

import com.team.teamup.domain.*;
import com.team.teamup.domain.enums.Department;
import com.team.teamup.domain.enums.TaskStatus;
import com.team.teamup.domain.enums.TaskType;
import com.team.teamup.domain.enums.UserStatus;
import com.team.teamup.dtos.PostDTO;
import com.team.teamup.dtos.ProjectDTO;
import com.team.teamup.dtos.TaskDTO;
import com.team.teamup.dtos.UserDTO;
import com.team.teamup.persistence.*;
import com.team.teamup.service.*;
import com.team.teamup.utils.DTOsConverter;
import com.team.teamup.utils.MailUtils;
import com.team.teamup.utils.TaskUtils;
import com.team.teamup.utils.UserUtils;
import com.team.teamup.validation.UserValidation;
import org.hibernate.cfg.NotYetImplementedException;
import org.json.JSONArray;
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

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest
public class RestGetControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MailUtils mailUtils;
    @MockBean
    ResetRequestRepository resetRequestRepository;
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
    public void setup() {
        when(userValidation.isValid(Mockito.anyString())).thenReturn(true);
        when(userValidation.isUserLoggedIn(anyString())).thenReturn(true);
    }

//    @Test
//    public void getAllUsersEmpty() throws Exception {
//        throw new NotYetImplementedException();
//    }
//
//    @Test
//    public void getAllUsersWithData() throws Exception {
//        throw new NotYetImplementedException();
//    }

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

        verify(teamService, times(1)).getAllTeamDTO();
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
        when(taskService.getAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllTasksData() throws Exception {
        when(taskRepository.findAll()).thenReturn(List.of(new Task(), new Task()));
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        //TODO
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

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

        verify(commentService, times(1)).getAll();
        verify(dtOsConverter, times(0)).getDTOFromComment(any());
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getAllCommentsData() throws Exception {
        when(commentService.getAll()).thenReturn(List.of(new Comment(), new Comment()));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/comments")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        verify(commentService, times(1)).getAll();
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
    public void getAssignedTasksForUser() throws Exception {
        when(userRepository.findByHashKey("1")).thenReturn(Optional.of(new User()));
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/1/assigned-tasks")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getReportedTasksByUser() throws Exception {

        when(userRepository.findByHashKey("1")).thenReturn(Optional.of(new User()));
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/1/reported-tasks")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());

        verify(userService, times(1)).getByHashKey("1");
    }

    @Test
    public void noUserFound() throws Exception {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());
        when(userService.getByHashKey("2")).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/2/reported-tasks")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        verify(userService, times(1)).getByHashKey("2");
    }

    @Test
    public void getUserById() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/1")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/2")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());
    }


    @Test
    public void getTaskById() throws Exception {
        when(taskRepository.findById(1)).thenReturn(Optional.of(new Task()));
        when(taskRepository.findById(2)).thenReturn(Optional.empty());
        when(dtOsConverter.getDTOFromTask(any())).thenReturn(new TaskDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks/1")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks/2")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getProjectById() throws Exception {
        when(projectService.getDTOByID(1)).thenReturn(new ProjectDTO());
        when(projectService.getDTOByID(2)).thenThrow(NoSuchElementException.class);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/projects/1")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/projects/2")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());
        verify(projectService, times(1)).getDTOByID(1);
        verify(projectService, times(1)).getDTOByID(2);
    }

    @Test
    public void getIdForCurrentUser() throws Exception {
        User user = new User();
        user.setId(10);
        when(userService.getByHashKey("1")).thenReturn(user);
        when(userService.getByHashKey("2")).thenThrow(NoSuchElementException.class);
        when(dtOsConverter.getDTOFromUser(any())).thenReturn(new UserDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/key")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertEquals("10", mvcResult.getResponse().getContentAsString());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/key")
                        .header("token", "2")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());
        verify(userService, times(1)).getByHashKey("1");
        verify(userService, times(1)).getByHashKey("2");
    }

    @Test
    public void getUsersByIds() throws Exception {
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(dtOsConverter.getDTOFromUser(any())).thenReturn(new UserDTO());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users?ids=1,2,3")
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

        verify(userService, times(1)).getUsersByIds(any());
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
                MockMvcRequestBuilders.get("/api/posts/taskid=1")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 200);

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/posts/taskid=2")
                        .header("token", "")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), 200);

        verify(postService, times(2)).save(any());
        verify(dtOsConverter, times(2)).getDTOFromPost(any());
    }

    @Test
    public void logout() throws Exception {
        User user = new User();
        user.setId(1);
        user.setHashKey("1");

        when(userService.getByHashKey("1")).thenReturn(user);
        when(userService.getByHashKey("2")).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/logout")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(403, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/logout")
                        .header("token", "2")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(405, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getFile() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/1/photo")
                        .header("token", "1")
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    public void noPhotoAvailable() throws Exception {
        when(userService.getByID(2)).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/2/photo")
                        .header("token", "1")
        ).andReturn();
    }

    @Test
    public void getTasksProject() throws Exception {
        when(dtOsConverter.getDTOFromProject(any())).thenReturn(new ProjectDTO());
        when(taskService.getByID(1)).thenReturn(new Task());
        when(taskService.getByID(2)).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks/1/project")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tasks/2/project")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getTasksForAllow() throws Exception {
        when(userService.getByID(1)).thenReturn(new User());
        when(userService.getByID(2)).thenReturn(null);
        //when(taskUtils.getFilteredTasksByType(any(), any(), any(), any(), Collections.emptyList())).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/1/tasks")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/1/tasks?type=assignedto")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/1/tasks?type=assignedby")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/1/tasks?type=assignedby&term=now")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());

        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/1/tasks?type=assignedby&term=now")
                        .header("token", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());
    }

}