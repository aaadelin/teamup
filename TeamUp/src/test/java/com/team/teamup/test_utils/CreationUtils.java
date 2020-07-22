package com.team.teamup.test_utils;

import com.team.teamup.domain.Project;
import com.team.teamup.domain.Task;
import com.team.teamup.domain.User;
import com.team.teamup.domain.enums.Department;
import com.team.teamup.domain.TaskStatus;
import com.team.teamup.persistence.ProjectRepository;
import com.team.teamup.persistence.TaskRepository;
import com.team.teamup.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CreationUtils {

    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;

    private List<User> users = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();

    @Autowired
    public CreationUtils(UserRepository userRepository, TaskRepository taskRepository, ProjectRepository projectRepository) {

        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public void createUsers(int numberOfUsers){
        for (int i = 0; i < numberOfUsers; i++) {
            User user = new User();
            user.getAuthentication().setMinutesUntilLogout(80);
            user.getAuthentication().setPassword("a");
            user.getAuthentication().setHashKey("a"+LocalDateTime.now());
            user.getAuthentication().setUsername("a"+LocalDateTime.now());
            user.getAuthentication().setActive(true);
            user.getAuthentication().setLastActive(LocalDateTime.now());
            //todo testul crapa pentru ca UserAuthentication nu e salvat
            users.add(userRepository.save(user));
        }
    }

    public void createTasks(int numberOfTasks){
        for (int i = 0; i < numberOfTasks; i++) {
            Task task = new Task();
            task.setDescription("");
            task.setSummary("");
            task.setDeadline(LocalDateTime.now().plusDays(1));
            task.setTaskStatus(new TaskStatus("OPEN", 1));
            task.setProject(getLastProject());
            task.setReporter(getLastUser());
            task.setCreatedAt(LocalDateTime.now().minusMinutes(10));
            task.setDepartment(Department.DEVELOPMENT);
            task.setDifficulty(1);
            task.setAssignees(List.of(getLastUser()));
            task.setLastChanged(LocalDateTime.now());

            tasks.add(taskRepository.save(task));
        }
    }

    public void createProjects(int numberOfProjects){
        for (int i = 0; i < numberOfProjects; i++) {
            Project project = new Project();
            project.setVersion("0.0.1");
            project.setDeadline(LocalDateTime.now().plusMinutes(6));
            project.setDescription("");
            project.setName("a");
            project.setOwner(getLastUser());

            projects.add(projectRepository.save(project));
        }
    }

    public void createData(){
        createUsers(5);
        createProjects(3);
        createTasks(3);
    }

    public void createData(int userNumber, int projectNumber, int taskNumber){
        createUsers(userNumber);
        createProjects(projectNumber);
        createTasks(taskNumber);
    }

    public int getNumberOfProjects(){
        return projects.size();
    }

    public int getNumberOfUsers(){
        return users.size();
    }

    public int getNumberOfTasks(){
        return tasks.size();
    }

    public Project getLastProject() {
        if(projects.isEmpty()){
            createProjects(1);
        }
        return projects.get(projects.size()-1);
    }

    public User getLastUser() {
        if(users.isEmpty()){
            createUsers(1);
        }
        return users.get(users.size()-1);
    }

    public Task getLastTask(){
        if(tasks.isEmpty()){
            createTasks(1);
        }
        return tasks.get(tasks.size()-1);
    }

    public Task getTaskWithProject(){
        for(Task task: tasks){
            if(task.getProject() != null){
                return task;
            }
        }
        getLastTask().setProject(getLastProject());
        taskRepository.save(getLastTask());
        return getTaskWithProject();
    }

}
