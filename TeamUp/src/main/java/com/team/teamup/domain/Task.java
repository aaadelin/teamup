package com.team.teamup.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.team.teamup.domain.enums.Department;
import com.team.teamup.domain.enums.TaskType;
import com.team.teamup.utils.query.annotations.SearchEntity;
import com.team.teamup.utils.query.annotations.SearchField;
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
@SearchEntity
public class Task implements HasNameAndDescription{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @SearchField
    private String summary;

    @SearchField
    private String description;

    @SearchField(name = "created")
    private LocalDateTime createdAt;

    @SearchField(name = "done")
    private LocalDateTime doneAt;

    @SearchField
    private LocalDateTime lastChanged;

    @SearchField
    private LocalDateTime deadline;

    @SearchField
    private int difficulty;

    private float storyPoints;

    @SearchField
    private Integer priority;

    @SearchField(name = "type")
    private TaskType taskType;

    @ManyToOne
    @SearchField(name = "status")
    private TaskStatus taskStatus;

    @SearchField
    private Department department;

    @ManyToOne
    @JoinColumn(name = "REPORTER_ID")
    @SearchField(attribute = "username")
    private User reporter;

    @ManyToMany
    @JoinColumn(name = "ASSIGNEE_ID")
    @SearchField
    private List<User> assignees;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    @ToString.Exclude
    @SearchField(attribute = "name")
    private Project project;

    public String getName(){
        return summary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id &&
                difficulty == task.difficulty &&
                Float.compare(task.storyPoints, storyPoints) == 0 &&
                Objects.equals(summary, task.summary) &&
                Objects.equals(description, task.description) &&
                Objects.equals(createdAt, task.createdAt) &&
                Objects.equals(doneAt, task.doneAt) &&
                Objects.equals(lastChanged, task.lastChanged) &&
                Objects.equals(deadline, task.deadline) &&
                Objects.equals(priority, task.priority) &&
                taskType == task.taskType &&
                Objects.equals(taskStatus, task.taskStatus) &&
                department == task.department &&
                Objects.equals(reporter, task.reporter) &&
                Objects.equals(assignees, task.assignees) &&
                Objects.equals(project, task.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, summary, description, createdAt, doneAt, lastChanged, deadline, difficulty, storyPoints, priority, taskType, taskStatus, department, reporter, assignees, project);
    }
}
