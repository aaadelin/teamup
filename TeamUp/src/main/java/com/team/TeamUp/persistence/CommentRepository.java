package com.team.TeamUp.persistence;

import com.team.TeamUp.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
