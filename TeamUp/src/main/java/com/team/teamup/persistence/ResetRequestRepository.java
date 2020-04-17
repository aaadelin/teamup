package com.team.teamup.persistence;

import com.team.teamup.domain.ResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetRequestRepository extends JpaRepository<ResetRequest, Integer> {
}
