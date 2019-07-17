package com.team.TeamUp.persistance;

import com.team.TeamUp.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
}
