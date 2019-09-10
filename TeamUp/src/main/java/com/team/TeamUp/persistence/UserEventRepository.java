package com.team.TeamUp.persistence;

import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.UserEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserEventRepository extends JpaRepository<UserEvent, Integer> {
    List<UserEvent> findAllByCreatorOrderByTimeDesc(User creator);
    List<UserEvent> findAllByCreatorOrderByTimeDesc(User creator, Pageable pageable);
}
