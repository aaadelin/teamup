package com.team.TeamUp.persistence;

import com.team.TeamUp.domain.Team;
import com.team.TeamUp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    List<Team> findAllByLeader(User leader);
}
