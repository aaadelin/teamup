package com.team.teamup.persistence;

import com.team.teamup.domain.Team;
import com.team.teamup.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    List<Team> findAllByLeader(User leader);
}
