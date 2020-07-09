package com.team.teamup.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.teamup.domain.enums.Department;
import com.team.teamup.domain.enums.TaskType;

import java.time.LocalDateTime;
import java.util.List;

public class
TaskDTO {

    private int id;

    private String summary;

    private String description;

    private int difficulty;

    private int priority;

    private Department department;

    private LocalDateTime createdAt;

    private LocalDateTime doneAt;

    private LocalDateTime lastChanged;

    private LocalDateTime deadline;

    private String taskStatus;

    private TaskType taskType;

    private int project;

    private List<Integer> assignees;

    private int reporterID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getDoneAt() {
        return doneAt;
    }

    public void setDoneAt(LocalDateTime doneAt) {
        this.doneAt = doneAt;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(LocalDateTime lastChanged) {
        this.lastChanged = lastChanged;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Integer> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<Integer> assignees) {
        this.assignees = assignees;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public int getReporterID() {
        return reporterID;
    }

    public void setReporterID(int reporterID) {
        this.reporterID = reporterID;
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
                "id=" + id +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", difficulty=" + difficulty +
                ", priority=" + priority +
                ", department=" + department +
                ", createdAt=" + createdAt +
                ", doneAt=" + doneAt +
                ", lastChanged=" + lastChanged +
                ", deadline=" + deadline +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                ", project=" + project +
                ", assignees=" + assignees +
                ", reporterID=" + reporterID +
                '}';
    }
}
