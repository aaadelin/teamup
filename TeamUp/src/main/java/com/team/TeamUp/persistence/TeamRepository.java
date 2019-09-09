package com.team.TeamUp.persistance;

import com.team.TeamUp.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
}
