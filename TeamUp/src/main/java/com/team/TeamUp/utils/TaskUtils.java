package com.team.TeamUp.utils;

import com.team.TeamUp.controller.get.RestGetTaskController;
import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.persistance.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestGetTaskController.class);
    private static final int PAGE_SIZE = 10;

    private TaskRepository taskRepository;
    private DTOsConverter dtOsConverter;

    @Autowired
    public TaskUtils(TaskRepository taskRepository, DTOsConverter dtOsConverter){
        this.taskRepository = taskRepository;
        this.dtOsConverter = dtOsConverter;
    }

    public List<TaskDTO> getFilteredTasksByType(User user, String term, String type, int page){
        List<Task> tasks = new ArrayList<>();
        if(type != null && type.toLowerCase().equals("assignedto")){
            tasks = taskRepository.findAllByAssigneesContainingAndSummaryContainingOrDescriptionContaining(user, term, term, PageRequest.of(page, PAGE_SIZE));
        }else if(type != null && type.toLowerCase().equals("assignedby")){
            tasks = taskRepository.findAllByReporterAndSummaryContainingOrDescriptionContaining(user, term, term, PageRequest.of(page, PAGE_SIZE));
        }

        if(term == null){
            return taskRepository.findAll(PageRequest.of(page, PAGE_SIZE)).stream()
                    .map(task -> dtOsConverter.getDTOFromTask(task))
                    .collect(Collectors.toList());
        }

        return tasks.stream().map(task -> dtOsConverter.getDTOFromTask(task))
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getLastNTasks(long numberOfTasksToReturn){
        long totalNumberOfTasls = taskRepository.count();
        long numberOfWholePages = totalNumberOfTasls / 10;
        long tasksFromLastPage = totalNumberOfTasls % 10;

        if(numberOfTasksToReturn > totalNumberOfTasls){ //if there are more required than available, return all
            return getAllTasksConvertedToDTOs();
        }

        List<TaskDTO> tasks = new ArrayList<>();

        if(tasksFromLastPage != 0){ //if there are tasks that do not make a whole page
            tasks.addAll(taskRepository.findAll(PageRequest.of(Math.toIntExact(numberOfWholePages), 10))
                    .stream() //get all of them
                    .sorted((o1, o2) -> o2.getId() - o1.getId()) //sort descending
                    .limit(numberOfTasksToReturn % 10)//limit the number if request is lower
                    .map(dtOsConverter::getDTOFromTask)
                    .collect(Collectors.toList()));
            numberOfWholePages--;
            numberOfTasksToReturn -= numberOfTasksToReturn % 10;
        }
        while(numberOfTasksToReturn >= 10){ //while i can get whole pages
            tasks.addAll(taskRepository.findAll(PageRequest.of(Math.toIntExact(numberOfWholePages), 10))
                    .stream()
                    .sorted((o1, o2) -> o2.getId() - o1.getId()) //sort descending
                    .map(dtOsConverter::getDTOFromTask)
                    .collect(Collectors.toList()));
            numberOfWholePages--;
            numberOfTasksToReturn -= 10;
        }
        if(numberOfTasksToReturn != 0){ //if the number remaining does not represent a whole page
            List<TaskDTO> lastTasks = taskRepository.findAll(PageRequest.of(Math.toIntExact(numberOfWholePages), 10))
                    .stream()
                    .limit(numberOfTasksToReturn)
                    .map(dtOsConverter::getDTOFromTask)
                    .collect(Collectors.toList());
            tasks.addAll(lastTasks);
        }
        return tasks;
    }

    public List<TaskDTO> getAllTasksConvertedToDTOs(){
        return taskRepository.findAll().stream()
                .map(dtOsConverter::getDTOFromTask)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> filterTasks(String filterWord, List<TaskDTO> tasks){
        if(filterWord != null && !filterWord.strip().equals("")) {
            return tasks.stream().filter(task -> task.getSummary().toLowerCase().contains(filterWord) || task.getDescription().toLowerCase().contains(filterWord)).collect(Collectors.toList());
        }
        return tasks;
    }

    public List<TaskDTO> findAssignedTasksByParameters(User user, Integer page, List<TaskStatus> statuses, String search, String sort, Boolean desc){
        List<Task> tasks;
        LOGGER.info("Filter method preferred: by multiple statuses");
        if(sort.equals("")){
            tasks = taskRepository.findAllByTaskStatusInAndAssigneesContaining(statuses,
                    user,
                    PageRequest.of(page, PAGE_SIZE));
        }else{
            tasks = getAssignedSortedTasks(user, statuses, page, sort, desc);
        }
        List<TaskDTO> taskDTOS = tasks.stream().map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        taskDTOS = filterTasks(search, taskDTOS);

        return taskDTOS;
    }

    private List<Task> getAssignedSortedTasks(User user, List<TaskStatus> statuses, Integer page, String sort, Boolean desc){
        switch(sort){
            case "deadline":
                if(desc){
                    return taskRepository.findAllByTaskStatusInAndAssigneesContainingOrderByDeadlineDesc(statuses, user, PageRequest.of(page, PAGE_SIZE));
                }
                return taskRepository.findAllByTaskStatusInAndAssigneesContainingOrderByDeadlineAsc(statuses, user, PageRequest.of(page, PAGE_SIZE));
            case "priority":
                if(desc){
                    return taskRepository.findAllByTaskStatusInAndAssigneesContainingOrderByPriorityDesc(statuses, user, PageRequest.of(page, PAGE_SIZE));
                }
                return taskRepository.findAllByTaskStatusInAndAssigneesContainingOrderByPriorityAsc(statuses, user, PageRequest.of(page, PAGE_SIZE));
            case "modified":
                if(desc){
                    return taskRepository.findAllByTaskStatusInAndAssigneesContainingOrderByLastChangedDesc(statuses, user, PageRequest.of(page, PAGE_SIZE));
                }
                return taskRepository.findAllByTaskStatusInAndAssigneesContainingOrderByDeadlineAsc(statuses, user, PageRequest.of(page, PAGE_SIZE));
            default:
                return taskRepository.findAllByTaskStatusInAndAssigneesContaining(statuses, user, PageRequest.of(page, PAGE_SIZE));
        }
    }

}
