package com.team.teamup.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProjectDTO {

    private int id;

    private String name;

    private String description;

    private LocalDateTime deadline;

    private List<Integer> tasksIDs;

    private int ownerID;

    private String version;

    private boolean archived;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getDeadline() {
        return deadline;
    }

    @Override
    public String toString() {
        return "ProjectDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", deadline=" + deadline +
                ", tasksIDs=" + tasksIDs +
                ", ownerID=" + ownerID +
                ", version=" + version +
                '}';
    }
}
