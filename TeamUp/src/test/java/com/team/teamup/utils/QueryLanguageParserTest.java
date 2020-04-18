package com.team.teamup.utils;

import com.team.teamup.domain.Project;
import com.team.teamup.domain.Task;
import com.team.teamup.domain.User;
import com.team.teamup.domain.enums.TaskStatus;
import com.team.teamup.persistence.TaskRepository;
import com.team.teamup.utils.query.QueryLanguageParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class QueryLanguageParserTest {

    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private QueryLanguageParser qlp;

    private List<Task> tasks;

    private void initializeTasks(){
        LocalDateTime dateTime = LocalDateTime.of(2020, 10, 2, 0, 0);
        User user = User.builder()
                    .username("imaria")
                    .build();
        User user1 = User.builder()
                .username("mion")
                .build();
        User defaultUser = User.builder()
                .username("default")
                .build();

        Project project = Project.builder()
                .id(1)
                .name("new Project")
                .description("project description")
                .owner(user1)
                .version("0.1")
                .archived(true)
                .deadline(dateTime.plusDays(10))
                .build();
        Project project1 = Project.builder()
                .id(2)
                .name("new new Project")
                .description("no description")
                .owner(user)
                .version("0.3")
                .archived(false)
                .deadline(dateTime.minusMonths(1))
                .build();

        Task task1 = Task.builder()
                .id(1)
                .summary("Create task")
                .description("Create task")
                .createdAt(dateTime)
                .deadline(dateTime.plusDays(10))
                .difficulty(1)
                .priority(3)
                .taskStatus(TaskStatus.OPEN)
                .reporter(user)
                .assignees(List.of(user1, user))
                .project(project)
                .build();
        Task task2 = Task.builder()
                .id(2)
                .summary("Update task")
                .description("Update task")
                .createdAt(dateTime)
                .deadline(dateTime.plusDays(9))
                .difficulty(2)
                .priority(2)
                .taskStatus(TaskStatus.IN_PROGRESS)
                .reporter(user1)
                .assignees(List.of(user1))
                .project(project)
                .build();
        Task task3 = Task.builder()
                .id(3)
                .summary("Read task")
                .description("Read task")
                .createdAt(dateTime.plusDays(1))
                .deadline(dateTime.plusDays(8))
                .difficulty(3)
                .priority(1)
                .taskStatus(TaskStatus.UNDER_REVIEW)
                .reporter(user)
                .assignees(List.of(defaultUser))
                .project(project1)
                .build();
        Task task4 = Task.builder()
                .id(4)
                .summary("Read bug")
                .description("Read bug")
                .createdAt(dateTime.plusDays(2))
                .deadline(dateTime.plusDays(7))
                .difficulty(1)
                .priority(2)
                .taskStatus(TaskStatus.CLOSED)
                .reporter(defaultUser)
                .assignees(List.of(defaultUser))
                .project(project1)
                .build();
        Task task5 = Task.builder()
                .id(5)
                .summary("Update bug")
                .description("Update bug")
                .createdAt(dateTime.plusDays(3))
                .deadline(dateTime.plusDays(5))
                .difficulty(1)
                .priority(1)
                .taskStatus(TaskStatus.OPEN)
                .reporter(defaultUser)
                .assignees(List.of(defaultUser))
                .project(project1)
                .build();
        tasks = List.of(task1, task2, task3, task4, task5);
    }

    @Before
    public void setup(){
        initializeTasks();
        when(taskRepository.findAll()).thenReturn(tasks);
    }

    @Test
    public void getAllByQueryAndOnly() {
        String condition = "select task as t where t.summary like \"task\" and t.description like \"read\"";
        List<Task> expectedTasks  = qlp.getAllByQuery(condition);
        Assert.assertEquals(1, expectedTasks.size());
    }

    @Test
    public void getAllByQueryOrOnly() {
        String condition = "select task as t where t.summary like \"task\" or t.description like \"read\"";
        List<Task> expectedTasks  = qlp.getAllByQuery(condition);
        Assert.assertEquals(4, expectedTasks.size());
    }

    @Test
    public void getAllByQueryMixedConditions() {
        String condition = "select task as t where t.summary like \"task\" or t.description like \"read\" and t.difficulty in [1]";
        List<Task> expectedTasks  = qlp.getAllByQuery(condition);
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void getAllByQueryLimit() {
        String condition = "select task as task where t.summary like \"task\" or t.description like \"read\" and t.difficulty in [1] limit 1";
        List<Task> expectedTasks  = qlp.getAllByQuery(condition);
        Assert.assertEquals(1, expectedTasks.size());
    }

    @Test
    public void shouldDifferentiateAttributesGivenAliases() {
        String condition = "select task as T, project as P where T.summary like \"task\" or T.description like \"read\" and P.owner=mion";
        List<Task> expectedTasks  = qlp.getAllByQuery(condition);
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void getAllByQueryLimitWithoutCondition() {
        String condition = "select task where limit 2";
        List<Task> expectedTasks  = qlp.getAllByQuery(condition);
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void getAllByQueryWithoutCondition() {
        String condition = "select task where";
        List<Task> expectedTasks  = qlp.getAllByQuery(condition);
        Assert.assertEquals(5, expectedTasks.size());
    }

    @Test
    public void getAllByQueryWithoutwhereStatement() {
        String condition = "select task";
        List<Task> expectedTasks  = qlp.getAllByQuery(condition);
        Assert.assertEquals(5, expectedTasks.size());
    }

    @Test
    public void lessOrEqual() {
        String condition = String.format("deadline<=%d-%d-%d", 2020, 10, 10);
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(3, expectedTasks.size());
    }

    @Test
    public void greaterOrEqual() {
        String condition = String.format("deadline>=%d-%d-%d", 2020, 10, 11);
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void equalSummary() {
        String condition = "summary=\"update Bug\"";
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(1, expectedTasks.size());
    }

    @Test
    public void equalCreated() {
        String condition = String.format("created=%d-%d-%d", 2020, 10, 2);
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void equalPriority() {
        String condition = "priority=2";
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void equalStatus() {
        String condition = "status=under_review";
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(1, expectedTasks.size());

        condition = "status=in_progress";
        predicate = qlp.evaluateSimpleTaskCondition(condition);
        expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(1, expectedTasks.size());
    }

    @Test
    public void difficultyInList() {
        String condition = "difficulty in [1, 3]";
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(4, expectedTasks.size());
    }

    @Test
    public void statusInList() {
        String condition = "status in [open, closed]";
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(3, expectedTasks.size());
    }

    @Test
    public void assigneesInList() {
        String condition = "assignees in [mion]";
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void multipleAssigneesInList() {
        String condition = "assignees in [mion, imaria]";
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void multipleAssigneesEquals() {
        String condition = "assignees = mion, imaria";
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(1, expectedTasks.size());
    }

    @Test
    public void reporterInList() {
        String condition = "reporter in [mion, imaria]";
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(3, expectedTasks.size());
    }

    @Test
    public void reporterEqual() {
        String condition = "reporter = mion";
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(1, expectedTasks.size());
    }

    @Test
    public void selectWhere() {
        String condition = "select where";
        List<Task> tasks = qlp.getAllByQuery(condition);
        Assert.assertEquals(5, tasks.size());
    }

    @Test
    public void likeString() {
        String condition = "summary like \"read\"";
        Predicate<Task> predicate = qlp.evaluateSimpleTaskCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());

        condition = "summary like \"bug\"";
        predicate = qlp.evaluateSimpleTaskCondition(condition);
        expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());

        condition = "summary like \"task\"";
        predicate = qlp.evaluateSimpleTaskCondition(condition);
        expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(3, expectedTasks.size());
    }

    @Test
    public void projectNameEqualsString(){
        String condition = "name= \"new new\"";
        Predicate<Task> predicate = qlp.evaluateSimpleProjectCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(0, expectedTasks.size());
    }

    @Test
    public void projectNameLikeString(){
        String condition = "name like \"new new\"";
        Predicate<Task> predicate = qlp.evaluateSimpleProjectCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(3, expectedTasks.size());
    }

    @Test
    public void projectDescriptionEqualsString(){
        String condition = "description = \"no description\"";
        Predicate<Task> predicate = qlp.evaluateSimpleProjectCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(3, expectedTasks.size());
    }

    @Test
    public void projectDescriptionLikeString(){
        String condition = "description like \"project\"";
        Predicate<Task> predicate = qlp.evaluateSimpleProjectCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void projectDeadlineEquals(){
        String condition = String.format("deadline=%d-%d-%d", 2020, 10, 12);
        Predicate<Task> predicate = qlp.evaluateSimpleProjectCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void projectDeadlineLessOrEqual(){
        String condition = String.format("deadline<=%d-%d-%d", 2020, 10, 2);
        Predicate<Task> predicate = qlp.evaluateSimpleProjectCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(3, expectedTasks.size());
    }

    @Test
    public void projectDeadlineGreaterOrEqual(){
        String condition = String.format("deadline>=%d-%d-%d", 2020, 10, 2);
        Predicate<Task> predicate = qlp.evaluateSimpleProjectCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void projectOwnerEquals(){
        String condition = "owner = mion";
        Predicate<Task> predicate = qlp.evaluateSimpleProjectCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void projectOwnerIn(){
        String condition = "owner in [ mion ]";
        Predicate<Task> predicate = qlp.evaluateSimpleProjectCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());

        condition = "owner in [ mion, imaria ]";
        predicate = qlp.evaluateSimpleProjectCondition(condition);
        expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(5, expectedTasks.size());
    }

    @Test
    public void projectVersionEquals(){
        String condition = "version = 0.3";
        Predicate<Task> predicate = qlp.evaluateSimpleProjectCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(3, expectedTasks.size());
    }

    @Test
    public void projectVersionIn(){
        String condition = "version in [0.1]";
        Predicate<Task> predicate = qlp.evaluateSimpleProjectCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());
    }

    @Test
    public void projectArchivedTrue(){
        String condition = "archived = true";
        Predicate<Task> predicate = qlp.evaluateSimpleProjectCondition(condition);
        List<Task> expectedTasks = tasks.stream().filter(predicate).collect(Collectors.toList());
        Assert.assertEquals(2, expectedTasks.size());
    }
}