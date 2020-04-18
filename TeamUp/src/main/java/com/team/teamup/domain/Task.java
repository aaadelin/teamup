package com.team.teamup.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.team.teamup.domain.enums.Department;
import com.team.teamup.domain.enums.TaskStatus;
import com.team.teamup.domain.enums.TaskType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task implements HasNameAndDescription{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @ToString.Exclude
    private Project project;

    public String getName(){
        return summary;
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

}
