package com.team.TeamUp.persistence;

import com.team.TeamUp.domain.Project;
import com.team.TeamUp.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    List<Project> findAllByNameContainingOrDescriptionContaining(String name, String description);
    List<Project> findAllByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
    List<Project> findAllByOwner(User owner);
}
