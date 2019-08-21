package com.team.TeamUp.validation;

import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.persistance.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.team.TeamUp.domain.enums.TaskStatus.*;

public class TaskValidation {

    public boolean isTaskStatusChangeValid(Task original, TaskDTO taskDTO){
        TaskStatus originalStatus = original.getTaskStatus();
        switch (originalStatus){
            case OPEN:
                return taskDTO.getTaskStatus().equals(OPEN) || taskDTO.getTaskStatus().equals(IN_PROGRESS);
            case REOPENED:
                return taskDTO.getTaskStatus().equals(IN_PROGRESS) || taskDTO.getTaskStatus().equals(REOPENED);
            case IN_PROGRESS:
                return taskDTO.getTaskStatus().equals(OPEN) || taskDTO.getTaskStatus().equals(UNDER_REVIEW) || taskDTO.getTaskStatus().equals(IN_PROGRESS);
            case UNDER_REVIEW:
                return taskDTO.getTaskStatus().equals(IN_PROGRESS) || taskDTO.getTaskStatus().equals(APPROVED) || taskDTO.getTaskStatus().equals(UNDER_REVIEW);
            case APPROVED:
                return taskDTO.getTaskStatus().equals(UNDER_REVIEW) || taskDTO.getTaskStatus().equals(CLOSED) || taskDTO.getTaskStatus().equals(APPROVED); // TODO from approved to reopened
            case CLOSED:
                return taskDTO.getTaskStatus().equals(REOPENED) || taskDTO.getTaskStatus().equals(CLOSED);
        }
        return false;
    }

}