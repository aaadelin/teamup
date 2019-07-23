package com.team.TeamUp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.team.TeamUp.domain.enums.Department;
import com.team.TeamUp.domain.enums.TaskStatus;
import com.team.TeamUp.domain.enums.TaskType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class Task {
    @Id
    @GeneratedValue
    private int id;

    private String summary;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime doneAt;

    private LocalDateTime lastChanged;

    private LocalDateTime deadline;

    private int difficulty;

    private int priority;

    private TaskType taskType;

    private TaskStatus taskStatus;

    private Department department;

    @ManyToOne
    @JoinColumn(name = "REPORTER_ID")
    private User reporter;

    @ManyToMany
    @JoinColumn(name = "ASSIGNEE_ID")
    private List<User> assignees;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;



    public Task() {
    }

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

    @Column(length = 2000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDoneAt() {
        return doneAt;
    }

    public void setDoneAt(LocalDateTime doneAt) {
        this.doneAt = doneAt;
    }

    public LocalDateTime getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(LocalDateTime lastChanged) {
        this.lastChanged = lastChanged;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
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

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public List<User> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<User> assignees) {
        this.assignees = assignees;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(summary, task.summary) &&
                Objects.equals(description, task.description) &&
                Objects.equals(reporter, task.reporter) &&
                Objects.equals(createdAt, task.createdAt) &&
                Objects.equals(doneAt, task.doneAt) &&
                Objects.equals(lastChanged, task.lastChanged) &&
                Objects.equals(deadline, task.deadline) &&
                taskStatus == task.taskStatus &&
                Objects.equals(project, task.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, summary, description, reporter, createdAt, doneAt, lastChanged, deadline, taskStatus, project);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", doneAt=" + doneAt +
                ", lastChanged=" + lastChanged +
                ", deadline=" + deadline +
                ", difficulty=" + difficulty +
                ", priority=" + priority +
                ", taskType=" + taskType +
                ", taskStatus=" + taskStatus +
                ", department=" + department +
                ", reporter=" + reporter +
                ", assignees=" + assignees +
                '}';
    }
}
