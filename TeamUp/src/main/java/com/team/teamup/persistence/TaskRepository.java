package com.team.teamup.persistence;

import com.team.teamup.domain.Project;
import com.team.teamup.domain.Task;
import com.team.teamup.domain.User;
import com.team.teamup.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    Page<Task> findAll(Pageable pageable);
    List<Task> findAllByReporter(User reporter);
    List<Task> findAllByReporter(User reporter, Pageable pageable);
    List<Task> findAllByReporterAndSummaryContainingOrDescriptionContaining(User reporter, String summary, String description, Pageable pageable);
    List<Task> findAllByTaskStatus(TaskStatus taskStatus);
    List<Task> findAllByAssigneesContaining(User assignee);
    List<Task> findAllByAssigneesContaining(User assignee, Pageable pageable);
    List<Task> findAllByAssigneesContainingAndSummaryContainingOrDescriptionContaining(User assignee, String summary, String description, Pageable pageable);
    List<Task> findAllByTaskStatus(TaskStatus taskStatus, Pageable pageable);
    List<Task> findAllByTaskStatusAndReporter(TaskStatus taskStatus, User reporter, Pageable pageable);
    List<Task> findAllByTaskStatusAndAssigneesContaining(TaskStatus taskStatus, User assignee, Pageable pageable);
    List<Task> findAllByTaskStatusInAndAssigneesContaining(List<TaskStatus> statuses, User user, Pageable pageable);
    List<Task> findAllByTaskStatusIn(List<TaskStatus> statuses, Pageable pageable);
    List<Task> findAllByAssigneesContainingAndTaskStatusIn(User assignee, List<TaskStatus> statuses, Pageable pageable);
    List<Task> findAllByProject(Project project);
    List<Task> findAllByProject(Project project, Pageable pageable);
    List<Task> findAllByProjectAndTaskStatusIn(Project project, List<TaskStatus> statuses, Pageable pageable);
    List<Task> findAllByProjectOrderByIdDesc(Project project, Pageable pageable);
    List<Task> findAllByTaskStatusInAndAssigneesContaining(List<TaskStatus> taskStatuses, User user);
    List<Task> findDistinctByAssigneesIn(Collection<User> users);
    List<Task> findAllByAssigneesContainingAndLastChangedAfter(User assignee, Date dateAfter);
    int countTaskByAssigneesContainingAndTaskStatusIn(User assignee, List<TaskStatus> statuses);

    @Query(value = "select * from task t\n" +
            "inner join task_status ts on ts.id = t.task_status_id where ts.id in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContaining(List<Integer> taskStatuses, String description, String summary, Pageable pageable);


    @Query(value = "select * from task t\n" +
            "inner join task_status ts on ts.id = t.task_status_id where ts.id in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))\n" +
            "order by t.deadline", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContainingOrderByDeadline(List<Integer> taskStatuses, String description, String summary, Pageable pageable);

    @Query(value = "select * from task t\n" +
            "inner join task_status ts on ts.id = t.task_status_id where ts.id in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))\n" +
            "order by t.deadline desc", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContainingOrderByDeadlineDesc(List<Integer> taskStatuses, String description, String summary, Pageable pageable);

    @Query(value = "select * from task t\n" +
            "inner join task_status ts on ts.id = t.task_status_id where ts.id in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))\n" +
            "order by t.last_changed", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContainingOrderByLastChanged(List<Integer> taskStatuses, String description, String summary, Pageable pageable);

    @Query(value = "select * from task t\n" +
            "inner join task_status ts on ts.id = t.task_status_id where ts.id in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))\n" +
            "order by t.last_changed desc", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContainingOrderByLastChangedDesc(List<Integer> taskStatuses, String description, String summary, Pageable pageable);


    @Query(value = "select t.* from task t\n" +
            "inner join task_status ts on ts.id = t.task_status_id where ts.id in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))\n" +
            "order by t.priority", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContainingOrderByPriority(List<Integer> taskStatuses, String description, String summary, Pageable pageable);

    @Query(value = "select t.* from task t\n" +
            "inner join task_status ts on ts.id = t.task_status_id where ts.id in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))\n" +
            "order by t.priority desc", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContainingOrderByPriorityDesc(List<Integer> taskStatuses, String description, String summary, Pageable pageable);

    @Query(value = "select t.* from task t\n" +
            "inner join task_assignees ta on t.id = ta.task_id\n" +
            "inner join user u on ta.assignees_id = u.id\n" +
            "inner join task_status ts on ts.id=?2\n" +
            "inner join task_status ts on ts.id = t.task_status_id\n" +
            "where ts.id=?2 and (u.id=?1 or t.reporter_id = ?1)\n" +
            "group by t.id", nativeQuery = true)
    List<Task> findTasksWithStatusAssignedToOrReportedBy(int userId, int taskStatusId ,Pageable pageable);

    @Query(value = "select t.* from task t\n" +
            "left join task_assignees ta on t.id = ta.task_id\n" +
            "left join user u on ta.assignees_id = u.id\n" +
            "inner join task_status ts on ts.id = t.task_status_id\n" +
            "where ts.id in ?2 and (u.id=?1 or t.reporter_id = ?1)\n" +
            "group by t.id", nativeQuery = true)
    List<Task> findTasksWithStatusesAssignedToOrReportedBy(int userId, List<Integer> taskStatusIndexes ,Pageable pageable);

    @Query(value = "select t.* from task t " +
            "inner join task_status ts on ts.id = t.task_status_id\n" +
            "where t.reporter_id = ?1 and " +
            "    (t.summary like concat('%', ?2, '%') or " +
            "     t.description like concat('%', ?2, '%')) " +
            "    and ts.id in ?3 ", nativeQuery = true)
    List<Task> findAllByReporterAndSummaryContainingOrDescriptionContainingAndTaskStatusIn(int reporter, String searchTerm, List<Integer> statuses, Pageable pageable);


    @Query(value = "select t.* from task t " +
            "inner join task_assignees ta on t.id = ta.task_id " +
            "inner join task_status ts on ts.id = t.task_status_id " +
            "inner join user u on u.id = ta.assignees_id " +
            "where u.id = ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%')) and ts.id in ?3 " +
            "group by t.id", nativeQuery = true)
    List<Task> findAllByAssigneesContainingAndSummaryContainingOrDescriptionContainingAndTaskStatusIn(int assignee, String searchTerm, List<Integer> statuses, Pageable pageable);

    List<Task> findDistinctByAssigneesIn(List<User> assignees, Pageable page);
    List<Task> findDistinctByAssigneesInAndTaskStatusIn(List<User> assignees, List<TaskStatus> status, Pageable page);
}
