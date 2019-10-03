package com.team.TeamUp.service;

import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.persistence.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class TaskService {

    private TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    /**
     *
     * @param user user that is assigned to tasks
     * @param lastDays optional integer representing last number of days that a task has been updated
     * @return list of tasks that are assigned to that user and are modified in lastDays
     */
    public List<Task> getTasksWhereUserIsAssigned(User user, Integer lastDays){
        if(lastDays == null){
            log.info("Getting statistics from all time");
            return taskRepository.findAllByAssigneesContaining(user);
        }

        log.info(String.format("Getting statistics from last %s days", lastDays));
        LocalDate nowDate = LocalDate.now().minusDays(lastDays);
        Date sqlDate = Date.valueOf(nowDate);
        return taskRepository.findAllByAssigneesContainingAndLastChangedAfter(user, sqlDate);
    }
}
