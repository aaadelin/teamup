package com.team.TeamUp.dtos;

import com.team.TeamUp.domain.Task;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectDTO {

    private int id;

    private String name;

    private String description;

    private LocalDateTime deadline;

    private List<Integer> tasksIDs;

    private int ownerID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public List<Integer> getTasksIDs() {
        return tasksIDs;
    }

    public void setTasksIDs(List<Integer> tasksIDs) {
        this.tasksIDs = tasksIDs;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }
}
