package com.team.TeamUp.persistance;

import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByReporter(User reporter);
    List<Task> findAllByAssigneesContaining(User assignee);
}
