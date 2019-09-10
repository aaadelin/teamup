package com.team.TeamUp.persistence;

import com.team.TeamUp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByHashKey(String hashKey);
    Optional<User> findByUsernameAndPassword(String username, String password);
}
