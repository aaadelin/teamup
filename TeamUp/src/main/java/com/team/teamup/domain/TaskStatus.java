package com.team.teamup.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
public class TaskStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, length = 100)
    private String status;

    @Column(name = "status_order")
    private int order;

    public TaskStatus(){}
    public TaskStatus(String status, int order) {
        this.status = status; this.order = order;
    }

    public boolean canChange(TaskStatus other){
        return order == other.order - 1 || order == other.order + 1 || order == other.order || order == 0;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof TaskStatus)) {
            return false;
        }
        TaskStatus that = (TaskStatus) o;
        return that.status.trim().equalsIgnoreCase(this.status.trim());
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return status;
    }
}

//public enum TaskStatus {
//    OPEN, IN_PROGRESS, UNDER_REVIEW, APPROVED, REOPENED, CLOSED
//}
