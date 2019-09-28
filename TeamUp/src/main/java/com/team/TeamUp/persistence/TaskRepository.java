package com.team.TeamUp.persistence;

import com.team.TeamUp.domain.Project;
import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    List<Task> findAllByTaskStatusAndAssigneesContainingOrderByDeadlineAsc(TaskStatus taskStatus, User assignee, Pageable pageable);
    List<Task> findAllByTaskStatusAndAssigneesContainingOrderByDeadlineDesc(TaskStatus taskStatus, User assignee, Pageable pageable);
    List<Task> findAllByTaskStatusInAndAssigneesContaining(List<TaskStatus> statuses, User user, Pageable pageable);
    List<Task> findAllByTaskStatusInAndAssigneesContainingOrderByDeadlineAsc(List<TaskStatus> statuses, User user, Pageable pageable);
    List<Task> findAllByTaskStatusInAndAssigneesContainingOrderByDeadlineDesc(List<TaskStatus> statuses, User user, Pageable pageable);
    List<Task> findAllByTaskStatusInAndAssigneesContainingOrderByLastChangedAsc(List<TaskStatus> statuses, User user, Pageable pageable);
    List<Task> findAllByTaskStatusInAndAssigneesContainingOrderByLastChangedDesc(List<TaskStatus> statuses, User user, Pageable pageable);
    List<Task> findAllByTaskStatusInAndAssigneesContainingOrderByPriorityAsc(List<TaskStatus> statuses, User user, Pageable pageable);
    List<Task> findAllByTaskStatusInAndAssigneesContainingOrderByPriorityDesc(List<TaskStatus> statuses, User user, Pageable pageable);
    List<Task> findAllByTaskStatusIn(List<TaskStatus> statuses, Pageable pageable);
    List<Task> findAllByAssigneesContainingAndTaskStatusIn(User assignee, List<TaskStatus> statuses, Pageable pageable);
    List<Task> findAllByProject(Project project);
    List<Task> findAllByProject(Project project, Pageable pageable);
    List<Task> findAllByProjectOrderByIdDesc(Project project, Pageable pageable);
    List<Task> findAllByTaskStatusInAndAssigneesContaining(List<TaskStatus> taskStatuses, User user);

    @Query(value = "select * from task t\n" +
            "where t.task_status in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContaining(List<Integer> taskStatuses, String description, String summary, Pageable pageable);


    @Query(value = "select * from task t\n" +
            "where t.task_status in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))\n" +
            "order by t.deadline", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContainingOrderByDeadline(List<Integer> taskStatuses, String description, String summary, Pageable pageable);

    @Query(value = "select * from task t\n" +
            "where t.task_status in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))\n" +
            "order by t.deadline desc", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContainingOrderByDeadlineDesc(List<Integer> taskStatuses, String description, String summary, Pageable pageable);

    @Query(value = "select * from task t\n" +
            "where t.task_status in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))\n" +
            "order by t.last_changed", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContainingOrderByLastChanged(List<Integer> taskStatuses, String description, String summary, Pageable pageable);

    @Query(value = "select * from task t\n" +
            "where t.task_status in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))\n" +
            "order by t.last_changed desc", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContainingOrderByLastChangedDesc(List<Integer> taskStatuses, String description, String summary, Pageable pageable);


    @Query(value = "select t.* from task t\n" +
            "where t.task_status in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))\n" +
            "order by t.priority", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContainingOrderByPriority(List<Integer> taskStatuses, String description, String summary, Pageable pageable);

    @Query(value = "select t.* from task t\n" +
            "where t.task_status in ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%'))\n" +
            "order by t.priority desc", nativeQuery = true)
    List<Task> findAllByTaskStatusInAndDescriptionContainingOrSummaryContainingOrderByPriorityDesc(List<Integer> taskStatuses, String description, String summary, Pageable pageable);

    @Query(value = "select t.* from task t\n" +
            "inner join task_assignees ta on t.id = ta.task_id\n" +
            "inner join user u on ta.assignees_id = u.id\n" +
            "where t.task_status=?2 and (u.id=?1 or t.reporter_id = ?1)\n" +
            "group by t.id", nativeQuery = true)
    List<Task> findTasksWithStatusAssignedToOrReportedBy(int userId, int taskStatusIndex ,Pageable pageable);

    @Query(value = "select t.* from task t\n" +
            "left join task_assignees ta on t.id = ta.task_id\n" +
            "left join user u on ta.assignees_id = u.id\n" +
            "where t.task_status in ?2 and (u.id=?1 or t.reporter_id = ?1)\n" +
            "group by t.id", nativeQuery = true)
    List<Task> findTasksWithStatusesAssignedToOrReportedBy(int userId, List<Integer> taskStatusIndexes ,Pageable pageable);

    @Query(value = "select t.* from task t " +
            "where t.reporter_id = ?1 and " +
            "    (t.summary like concat('%', ?2, '%') or " +
            "     t.description like concat('%', ?2, '%')) " +
            "    and t.task_status in ?3 ", nativeQuery = true)
    List<Task> findAllByReporterAndSummaryContainingOrDescriptionContainingAndTaskStatusIn(int reporter, String searchTerm, List<Integer> statuses, Pageable pageable);


    @Query(value = "select t.* from task t " +
            "inner join task_assignees ta on t.id = ta.task_id " +
            "inner join user u on u.id = ta.assignees_id " +
            "where u.id = ?1 and (t.summary like concat('%', ?2, '%') or t.description like concat('%', ?2, '%')) and t.task_status in ?3 " +
            "group by t.id", nativeQuery = true)
    List<Task> findAllByAssigneesContainingAndSummaryContainingOrDescriptionContainingAndTaskStatusIn(int assignee, String searchTerm, List<Integer> statuses, Pageable pageable);

}
