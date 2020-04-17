package com.team.teamup.persistence;

import com.team.teamup.domain.User;
import com.team.teamup.domain.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByHashKey(String hashKey);
    Optional<User> findByUsernameAndPassword(String username, String password);
    List<User> findAllByStatus(UserStatus userStatus);
    List<User> findAllByFirstNameContainingOrLastNameContaining(String firstName, String lastName);
}
