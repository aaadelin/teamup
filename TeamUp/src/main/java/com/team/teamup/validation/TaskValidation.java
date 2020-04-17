package com.team.teamup.validation;

import com.team.teamup.domain.Task;
import com.team.teamup.domain.enums.TaskStatus;
import com.team.teamup.dtos.TaskDTO;

import static com.team.teamup.domain.enums.TaskStatus.*;

public class TaskValidation {
    /**
     *
     * @param original Task entity consisting of the previous task from the database
     * @param taskDTO TaskDTO consisting in the alteration of the original
     * @return true if the change in status between the original and the altered one is valid, false otherwise
     */
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
