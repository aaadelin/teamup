package com.team.TeamUp.utils;

import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.persistance.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskUtils {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private DTOsConverter dtOsConverter;

    public List<TaskDTO> getFilteredTasksByType(User user, String term, String type){
        List<Task> tasks = new ArrayList<>();
        if(type != null && type.toLowerCase().equals("assignedto")){
            tasks = taskRepository.findAllByAssigneesContaining(user);
        }else if(type != null && type.toLowerCase().equals("assignedby")){
            tasks = taskRepository.findAllByReporter(user);
        }

        if(term == null){
            return tasks.stream()
                    .map(task -> dtOsConverter.getDTOFromTask(task))
                    .collect(Collectors.toList());
        }

        return tasks.stream()
                .filter(task -> task.getSummary().toLowerCase().contains(term.toLowerCase()) ||
                        task.getDescription().toLowerCase().contains(term.toLowerCase()))
                .map(task -> dtOsConverter.getDTOFromTask(task))
                .collect(Collectors.toList());
    }
}
