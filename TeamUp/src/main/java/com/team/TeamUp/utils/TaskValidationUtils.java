package com.team.TeamUp.utils;

import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.dtos.TaskDTO;

import static com.team.TeamUp.domain.enums.TaskStatus.*;

public class TaskValidationUtils {

    public boolean isTaskStatusChangeValid(Task original, TaskDTO taskDTO){
        TaskStatus originalStatus = original.getTaskStatus();
        switch (originalStatus){
            case OPEN:
            case REOPENED:
                return taskDTO.getTaskStatus().equals(IN_PROGRESS);
            case IN_PROGRESS:
                return taskDTO.getTaskStatus().equals(OPEN) || taskDTO.getTaskStatus().equals(UNDER_REVIEW);
            case UNDER_REVIEW:
                return taskDTO.getTaskStatus().equals(IN_PROGRESS) || taskDTO.getTaskStatus().equals(APPROVED);
            case APPROVED:
                return taskDTO.getTaskStatus().equals(UNDER_REVIEW) || taskDTO.getTaskStatus().equals(CLOSED);
            case CLOSED:
                return taskDTO.getTaskStatus().equals(REOPENED);
        }
        return false;
    }
}
