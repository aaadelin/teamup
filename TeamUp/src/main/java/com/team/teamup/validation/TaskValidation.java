package com.team.teamup.validation;

import com.team.teamup.domain.Task;
import com.team.teamup.domain.TaskStatus;

public class TaskValidation {
    /**
     *
     * @param original Task entity consisting of the previous task from the database
     * @return true if the change in status between the original and the altered one is valid, false otherwise
     */
    public boolean isTaskStatusChangeValid(Task original, TaskStatus newTaskStatus, boolean ignoreOrder){
        if (ignoreOrder) {
            return true;
        }
        TaskStatus originalStatus = original.getTaskStatus();
        return original.getTaskStatus().canChange(newTaskStatus);
    }

}
