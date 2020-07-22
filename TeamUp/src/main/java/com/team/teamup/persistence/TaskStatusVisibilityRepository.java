package com.team.teamup.persistence;

import com.team.teamup.domain.TaskStatusVisibility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStatusVisibilityRepository extends JpaRepository<TaskStatusVisibility, Integer> {
}
