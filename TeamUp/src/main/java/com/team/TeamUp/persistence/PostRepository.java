package com.team.TeamUp.persistance;

import com.team.TeamUp.domain.Post;
import com.team.TeamUp.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findByTask(Task task);
}
