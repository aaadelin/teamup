package com.team.TeamUp.persistence;

import com.team.TeamUp.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
}
