package com.team.teamup.utils.query;

import com.team.teamup.domain.enums.TaskStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ReflectionQueryLanguageParserTest {

    @Mock
    private JpaRepository<MockTask, Integer> taskRepository;
    @InjectMocks
    private ReflectionQueryLanguageParser<MockTask, Integer> qlp;

    private List<MockTask> tasks;

    private void initializeTasks() {
        qlp.setClazz(MockTask.class);

        LocalDateTime dateTime = LocalDateTime.of(2020, 10, 2, 10, 10);

        MockLocation location1 = MockLocation.builder()
                .address("van")
                .country("Romania")
                .build();

        MockLocation location2 = MockLocation.builder()
                .address("van")
                .country("RU")
                .build();
        MockUser user1 = MockUser.builder()
                .id(1)
                .name("Ana")
                .age(10)
                .bornDate(dateTime)
                .location(location1)
                .build();
        MockUser user2 = MockUser.builder()
                .id(2)
                .name("Matei")
                .age(11)
                .bornDate(dateTime.minusYears(1))
                .location(location2)
                .build();
        MockTask task1 = MockTask.builder()
                .id(1)
                .summary("Task read")
                .description("Description")
                .deadline(LocalDate.from(dateTime.plusDays(1)))
                .priority(1)
                .difficulty(1)
                .lastChanged(dateTime)
                .owner(user1)
                .status(TaskStatus.OPEN)
                .assignees(List.of(user1))
                .difficulties(List.of(1))
                .build();
        MockTask task2 = MockTask.builder()
                .id(2)
                .summary("Task create")
                .description("description")
                .deadline(LocalDate.from(dateTime.plusDays(2)))
                .priority(1)
                .difficulty(2)
                .lastChanged(dateTime.plusDays(1).plusMinutes(1))
                .owner(user1)
                .status(TaskStatus.OPEN)
                .assignees(List.of(user2))
                .difficulties(List.of(1, 2))
                .build();
        MockTask task3 = MockTask.builder()
                .id(3)
                .summary("Task update")
                .description("update")
                .deadline(LocalDate.from(dateTime.plusDays(3)))
                .priority(2)
                .difficulty(3)
                .lastChanged(dateTime.plusDays(1))
                .owner(user2)
                .status(TaskStatus.OPEN)
                .assignees(List.of(user1))
                .difficulties(List.of(4))
                .build();
        MockTask task4 = MockTask.builder()
                .id(4)
                .summary("Task delete")
                .description("deletion")
                .deadline(LocalDate.from(dateTime.plusDays(4)))
                .priority(2)
                .difficulty(4)
                .lastChanged(dateTime.plusDays(2))
                .owner(user2)
                .status(TaskStatus.CLOSED)
                .assignees(List.of(user2))
                .difficulties(List.of(5))
                .build();
        MockTask task5 = MockTask.builder()
                .id(5)
                .summary("Update bug")
                .description("deleted")
                .deadline(LocalDate.from(dateTime.plusDays(5)))
                .priority(3)
                .difficulty(2)
                .lastChanged(dateTime.plusDays(3))
                .owner(user2)
                .status(TaskStatus.IN_PROGRESS)
                .assignees(List.of(user1, user2))
                .difficulties(List.of(5))
                .build();
        tasks = List.of(task1, task2, task3, task4, task5);
    }

    @Before
    public void setup() {
        initializeTasks();
        when(taskRepository.findAll()).thenReturn(tasks);
    }

    @Test
    public void testSearchBySummary() {
        String search = "select where summary=\"Update bug\"";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(1, tasks.size());
    }

    @Test
    public void testSearchBySummaryLike() {
        String search = "select where summary like \"task\"";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(4, tasks.size());
    }

    @Test
    public void testSearchByDescriptionNotEqual() {
        String search = "select where description != \"description\"";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(3, tasks.size());
    }

    @Test
    public void testSearchByPriorityIn() {
        String search = "select where priority in [1, 2]";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(4, tasks.size());
    }

    @Test
    public void testSearchByPriorityEquals() {
        String search = "select where priority = 1";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(2, tasks.size());

        String search1 = "select where priority = 3";
        List<MockTask> tasks1 = qlp.getAllByQuery(search1);
        assertEquals(1, tasks1.size());
    }

    @Test
    public void testSearchByDifficultyLower() {
        String search = "select where difficulty < 5";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(5, tasks.size());
    }

    @Test
    public void testSearchByDifficultyNotEqual() {
        String search = "select where difficulty != 3";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(4, tasks.size());
    }

    @Test
    public void testDeadlineEquals(){
        String search = "select where deadline = 2020-10-3";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(1, tasks.size());
    }

    @Test
    public void testDeadlineWithHourNotEquals(){
        String search = "select where deadline = 2020-10-3 10:9";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(0, tasks.size());
    }

    @Test
    public void testLastChangedWithHourEquals(){
        String search = "select where lastChanged = 2020-10-3 10:10";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(1, tasks.size());
    }

    @Test
    public void testLastChangedWithoutHourEquals(){
        String search = "select where lastChanged = 2020-10-3";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(2, tasks.size());
    }

    @Test
    public void testDeadlineBeforeOrEquals(){
        String search = "select where deadline <= 2020-10-5";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(3, tasks.size());
    }

    @Test
    public void testDeadlineBeforeOrEqualsAndDifficultyLower(){
        String search = "select where deadline <= 2020-10-5 and difficulty < 3";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(2, tasks.size());
    }

    @Test
    public void testDeadlineBeforeOrEqualsOrDifficultyLower(){
        String search = "select where deadline <= 2020-10-5 or difficulty < 3";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(4, tasks.size());
    }

    @Test
    public void testGetByOwner(){
        String search = "select where owner=\"Ana\"";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(2, tasks.size());
    }

    @Test
    public void testGetByOwnerName(){
        String search = "select where owner.age=11";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(3, tasks.size());
    }

    @Test
    public void testGetByOwnerLocationCountry(){
        String search = "select where owner.location.country=\"RU\" and summary like \"update\"";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(2, tasks.size());
    }

    @Test
    public void testGetByStatus(){
        String search = "select where status in [open, closed]";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(4, tasks.size());
    }

    @Test
    public void testGetByOwnerIn(){
        String search = "select where owner in [\"Ana\", \"Geo\"]";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(2, tasks.size());
    }

    @Test
    public void testGetByAssigneesIn(){
        String search = "select where assignees in [\"Ana\", \"Geo\"]";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(3, tasks.size());
    }

    @Test
    public void testGetByDifficultiesIn(){
        String search = "select where difficulties in [1, 2, 3]";
        List<MockTask> tasks = qlp.getAllByQuery(search);
        assertEquals(3, tasks.size());
    }
}