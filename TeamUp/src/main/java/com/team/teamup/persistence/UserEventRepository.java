package com.team.teamup.persistence;

import com.team.teamup.domain.User;
import com.team.teamup.domain.UserEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserEventRepository extends JpaRepository<UserEvent, Integer> {
    List<UserEvent> findAllByCreatorOrderByTimeDesc(User creator);
    List<UserEvent> findAllByCreatorOrderByTimeDesc(User creator, Pageable pageable);
}
