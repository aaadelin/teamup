package com.team.teamup.controller.integration;

import com.team.teamup.domain.Task;
import com.team.teamup.domain.TaskStatus;
import com.team.teamup.domain.enums.TaskType;
import com.team.teamup.dtos.ProjectDTO;
import com.team.teamup.dtos.TaskDTO;
import com.team.teamup.test_utils.CreationUtils;
import com.team.teamup.utils.DTOsConverter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hibernate.cfg.NotYetImplementedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RestGetTaskControllerIntegrationTest {

    @Value("http://localhost:${local.server.port}/api")
    String baseUrl;

    @Autowired
    CreationUtils creationUtils;
    @Autowired
    DTOsConverter dtOsConverter;

    private RequestSpecification getHeaderRequest(){
        return given()
                .header("Content-Type", "application/json")
                .header("token", creationUtils.getLastUser().getHashKey());
    }

    @Before
    public void before(){
        creationUtils.createData(1, 2, 11);
    }

    @Test
    public void notAuthorized() {
        Response response =  given().header("token", "").when().get(baseUrl + "/tasks");

        response.then().assertThat().statusCode(403);
    }

    @Test
    public void getAllTasks() {
        Response response =  getHeaderRequest().get(baseUrl + "/tasks");

        TaskDTO[] tasks = response.getBody().as(TaskDTO[].class);
        Assert.assertEquals(Math.min(creationUtils.getNumberOfTasks(), 10), tasks.length);

        throw new NotYetImplementedException();
    }

    @Test
    public void getAllTaskStatus() {
        Response response = getHeaderRequest().get(baseUrl + "/task-status");

        response.then().assertThat().statusCode(200).and().body("size()", is(0));
        Assert.assertArrayEquals(response.as(TaskStatus[].class), new TaskStatus[0]);
    }

    @Test
    public void getAllTaskTypes() {
        Response response = getHeaderRequest().get(baseUrl + "/task-types");

        response.then().assertThat().statusCode(200).and().body("size()", is(TaskType.values().length));
        Assert.assertArrayEquals(response.as(TaskType[].class), TaskType.values());
    }

    @Test
    public void getTaskById() {
        Task task = creationUtils.getLastTask();
        Response response = getHeaderRequest().get(String.format("%s/tasks/%s", baseUrl, task.getId()));

        response.then().assertThat().statusCode(200);

        Assert.assertEquals(dtOsConverter.getDTOFromTask(task).getId(), response.as(TaskDTO.class).getId());
    }

    @Test
    public void getTasksProject() {
        Task task = creationUtils.getTaskWithProject();

        Response response = getHeaderRequest().get(String.format("%s/tasks/%s/project", baseUrl, task.getId()));
        response.then().statusCode(200);

        Assert.assertEquals(task.getProject().getId(), response.as(ProjectDTO.class).getId());
    }

    @Test
    public void getAssignedTasks() {

        Response response = getHeaderRequest().param("page", 0).get(String.format("%s/tasks/assigned", baseUrl));
        response.then().statusCode(200);
        Assert.assertEquals(10, response.as(TaskDTO[].class).length);

        assert getHeaderRequest().param("page", 1).get("%s/tasks/assigned").as(TaskDTO[].class).length == 1;

//        throw new NotYetImplementedException();
    }

    @Test
    public void getReportedTasks() {
        throw new NotYetImplementedException();
    }

    @Test
    public void getAssignedAndReportedTasks() {
        throw new NotYetImplementedException();
    }
}