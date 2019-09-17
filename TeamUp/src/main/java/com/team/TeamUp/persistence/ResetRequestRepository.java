package com.team.TeamUp.persistence;

import com.team.TeamUp.domain.ResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetRequestRepository extends JpaRepository<ResetRequest, Integer> {
}
