package com.team.teamup.service;

import com.team.teamup.domain.ResetRequest;
import com.team.teamup.persistence.ResetRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class ResetRequestService {

    private ResetRequestRepository resetRequestRepository;

    @Autowired
    public ResetRequestService(ResetRequestRepository resetRequestRepository){
        this.resetRequestRepository = resetRequestRepository;
    }

    public ResetRequest getByID(int id) {
        return resetRequestRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public ResetRequest save(ResetRequest resetRequest) {
        return resetRequestRepository.save(resetRequest);
    }
}
