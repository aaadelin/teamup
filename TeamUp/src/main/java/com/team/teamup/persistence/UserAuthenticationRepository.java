package com.team.teamup.persistence;

import com.team.teamup.domain.User;
import com.team.teamup.domain.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, Integer> {
    Optional<UserAuthentication> findByHashKey(String hashKey);
    Optional<UserAuthentication> findByUsernameAndPassword(String username, String password);
}
