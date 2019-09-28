package com.team.TeamUp.utils;

import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.dtos.TaskDTO;
import com.team.TeamUp.persistence.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TaskUtils {
    
    private static final int PAGE_SIZE = 10;

    private TaskRepository taskRepository;
    private DTOsConverter dtOsConverter;

    @Autowired
    public TaskUtils(TaskRepository taskRepository, DTOsConverter dtOsConverter){
        this.taskRepository = taskRepository;
        this.dtOsConverter = dtOsConverter;
    }

    /**
     *
     * @param user user to be found in assigned or reporter
     * @param term term to be found in summary or description of the task
     * @param type String, only 'assignedby' and 'assignedto'
     * @param page pare of the repository
     * @param statuses list of task status
     * @return list of tasks with the user in assignees or reporting or both, with the specified term (if existing) from the page with one of the specified statuses
     */
    public List<TaskDTO> getFilteredTasksByType(User user, String term, String type, int page, List<TaskStatus> statuses){
        List<Task> tasks = new ArrayList<>();

        //get only one type of task
        if(type != null && type.toLowerCase().equals("assignedto")){
            // get tasks assigned to the user
            tasks = taskRepository.findAllByAssigneesContainingAndSummaryContainingOrDescriptionContainingAndTaskStatusIn(user.getId(), term, statuses.stream().map(Enum::ordinal)
                    .collect(Collectors.toList()), PageRequest.of(page, PAGE_SIZE));
        }else if(type != null && type.toLowerCase().equals("assignedby")){
            //get tasks assigned by the user
            tasks = taskRepository.findAllByReporterAndSummaryContainingOrDescriptionContainingAndTaskStatusIn(user.getId(), term, statuses.stream().map(Enum::ordinal)
                    .collect(Collectors.toList()), PageRequest.of(page, PAGE_SIZE));
        }

        //get both assigned to and by the user
        if(term == null && (statuses == null || statuses.isEmpty())){
            //find all tasks from that page
            tasks = taskRepository.findAll(PageRequest.of(page, PAGE_SIZE)).getContent();
        }else if(term == null){
            //find tasks with user assigned and with the specified task statuses
            tasks = taskRepository.findAllByAssigneesContainingAndTaskStatusIn(user, statuses, PageRequest.of(page, PAGE_SIZE));
        }

        //convert the retult to dtos and return
        return tasks.stream().map(task -> dtOsConverter.getDTOFromTask(task))
                .collect(Collectors.toList());
    }

    /**
     * Method to get the last N tasks updated in the repository
     * @param numberOfTasksToReturn number of tasks to get in reverse order
     * @return last numberOfTasks tasks from the repository
     */
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

    /**
     *
     * @return all tasks from the repository converted to DTOs
     */
    public List<TaskDTO> getAllTasksConvertedToDTOs(){
        return taskRepository.findAll().stream()
                .map(dtOsConverter::getDTOFromTask)
                .collect(Collectors.toList());
    }

    /**
     * Method to filter tasks by the term in summary or description
     * @param filterWord term to be found in summary or description
     * @param tasks list of tasks to be filtered
     * @return tasks list filtered by the specified term
     */
    public List<TaskDTO> filterTasks(String filterWord, List<TaskDTO> tasks){
        if(filterWord != null && !filterWord.strip().equals("")) {
            return tasks.stream().filter(task -> task.getSummary().toLowerCase().contains(filterWord) || task.getDescription().toLowerCase().contains(filterWord)).collect(Collectors.toList());
        }
        return tasks;
    }

    /**
     * Generic method to fund assigned tasks by parameters
     * If statuses is null, all statuses are taken into consideration
     * If sort is null, tasks will be sorted by id
     * @param user User the task is assigned to
     * @param page repository's page
     * @param statuses list of task statuses
     * @param search term to be found in description  or summary
     * @param sort task's property to sort by
     * @param descending boolean to sort descending or ascending
     * @return list of taskDTOs sorted and filtered by parameters
     */
    public List<TaskDTO> findAssignedTasksByParameters(User user, Integer page, List<TaskStatus> statuses, String search, String sort, Boolean descending){
        List<Task> tasks;
        log.info("Filter method preferred: by multiple statuses");
        if(statuses == null){
            statuses = List.of(TaskStatus.values());
        }

        if(sort.equals("")){
            tasks = taskRepository.findAllByTaskStatusInAndAssigneesContaining(statuses,
                    user,
                    PageRequest.of(page, PAGE_SIZE));
        }else{
            tasks = getAssignedSortedTasks(user, statuses, page, sort, descending);
        }
        List<TaskDTO> taskDTOS = tasks.stream().map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());
        taskDTOS = filterTasks(search, taskDTOS);
        log.info(String.format("Returning list of tasks: %s", taskDTOS));
        return taskDTOS;
    }

    /**
     * Generic method to get tasks by the specified parameters
     * @param page repository's page
     * @param statuses list of task statuses
     * @param search keyword to be found in task's summary or description
     * @param sort task's property to sort the results by
     * @param descending boolean to sort tasks descending or ascending
     * @return list of tasks filtered and sorted by parameters
     */
    public List<TaskDTO> findTasksByParameters(Integer page, List<TaskStatus> statuses, String search, String sort, Boolean descending){
        log.info(String.format("Entering method to find tasks with statuses %s in page %s containing string %s sorted %s %s", statuses, page, search, sort, descending));
        List<Task> tasks;

        if(search == null){
            search = "";
        }
        if(sort == null){
            sort = "";
        }
        if(page == null){
            page = 0;
        }
        if (statuses == null || statuses.isEmpty()){
            statuses = Arrays.asList(TaskStatus.values());
        }
        tasks = findAllTasksWithTaskStatusInAndSummaryOrDescriptionContainingSordedBy(statuses.stream().map(Enum::ordinal).collect(Collectors.toList()), search, sort, descending, page);

        List<TaskDTO> taskDTOS = tasks.stream().map(dtOsConverter::getDTOFromTask).collect(Collectors.toList());

        log.info(String.format("Exiting with tasks: %s", taskDTOS));
        return taskDTOS;
    }

    /**
     *
     * @param statuses list of task statuses
     * @param search seatch term to be found in task's summary or description
     * @param sort task's property to be sorted by, if empty they will be sorted by id
     * @param descending boolean, if true they will be sorted descending, otherwise ascending
     * @param page repository's page
     * @return List of tasks from specified page, sorted and filtered
     */
    private List<Task> findAllTasksWithTaskStatusInAndSummaryOrDescriptionContainingSordedBy(List<Integer> statuses, String search, String sort, Boolean descending, Integer page) {
        if(sort.equals("")){
            return taskRepository.findAllByTaskStatusInAndDescriptionContainingOrSummaryContaining(statuses, search, search, PageRequest.of(page, PAGE_SIZE));
        }
        if(sort.equals("modified")){
            sort = "lastChanged";
        }
        return taskRepository.findAllByTaskStatusInAndDescriptionContainingOrSummaryContaining(statuses, search, search, PageRequest.of(page, PAGE_SIZE, Sort.by(descending ? Sort.Direction.DESC : Sort.Direction.ASC, sort)));
    }

    /**
     *
     * @param user User that is assigned to the tasks
     * @param statuses Statuses of the tasks
     * @param page Page number
     * @param sort Task's property, if empty will be sorted by id
     * @param desc boolean to check if ascending or descending
     * @return list of tasks assigned to user, with specified statuses from page sorted by the property descending or arcending
     */
    private List<Task> getAssignedSortedTasks(User user, List<TaskStatus> statuses, Integer page, String sort, Boolean desc){
        if(sort.equals("")){
            return taskRepository.findAllByTaskStatusInAndAssigneesContaining(statuses, user, PageRequest.of(page, PAGE_SIZE));
        }
        if(sort.equals("modified")){
            sort = "lastChanged";
        }
        return taskRepository.findAllByTaskStatusInAndAssigneesContaining(statuses, user,
                PageRequest.of(page, PAGE_SIZE, Sort.by(desc ? Sort.Direction.DESC : Sort.Direction.ASC, sort)));
    }
}
