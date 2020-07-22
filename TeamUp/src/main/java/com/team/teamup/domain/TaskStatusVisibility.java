package com.team.teamup.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusVisibility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "TASK_STATUS_ID")
    private TaskStatus taskStatus;

    private Boolean isVisible;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskStatusVisibility)) return false;
        TaskStatusVisibility that = (TaskStatusVisibility) o;
        return id == that.id &&
                Objects.equals(taskStatus, that.taskStatus) &&
                Objects.equals(isVisible, that.isVisible);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskStatus, isVisible);
    }
}