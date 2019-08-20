package com.team.TeamUp.persistance;

import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByReporter(User reporter);
    List<Task> findAllByAssigneesContaining(User assignee);
    List<Task> findAllByTaskStatus(TaskStatus taskStatus, Pageable pageable);
    List<Task> findAllByTaskStatusAndReporter(TaskStatus taskStatus, User reporter, Pageable pageable);
    List<Task> findAllByTaskStatusAndAssigneesContaining(TaskStatus taskStatus, User assignee, Pageable pageable);
    @Query(value = "select * from task t\n" +
            "inner join task_assignees ta on t.id = ta.task_id\n" +
            "inner join user u on ta.assignees_id = u.id\n" +
            "where t.task_status=?2 and (u.id=?1 or t.reporter_id = ?1)\n" +
            "group by t.id", nativeQuery = true)
    List<Task> findTasksWithStatusAssignedToOrReportedBy(int userId, int taskStatusIndex ,Pageable pageable);
    @Query(value = "select * from task t\n" +
            "inner join task_assignees ta on t.id = ta.task_id\n" +
            "inner join user u on ta.assignees_id = u.id\n" +
            "where t.task_status in ?2 and (u.id=?1 or t.reporter_id = ?1)\n" +
            "group by t.id", nativeQuery = true)
    List<Task> findTasksWithStatusesAssignedToOrReportedBy(int userId, List<Integer> taskStatusIndexes ,Pageable pageable);
    List<Task> findAllByTaskStatus(TaskStatus taskStatus);
    Page<Task> findAll(Pageable pageable);

    List<Task> findAllByTaskStatusInAndAssigneesContaining(List<TaskStatus> statuses, User user, Pageable pageable);
}
