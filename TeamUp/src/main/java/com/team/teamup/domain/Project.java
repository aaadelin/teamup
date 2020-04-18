package com.team.teamup.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Project implements HasNameAndDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    private LocalDateTime deadline;

    @JsonManagedReference
    @ManyToOne
    private User owner;

    @JsonManagedReference
    @OneToMany
    @JoinColumn(name = "PROJECT_ID")
    private List<Task> tasks;

    @Column (columnDefinition = "varchar(50) default '0.0.1'")
    private String version;

    private boolean archived;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id == project.id &&
                Objects.equals(name, project.name) &&
                Objects.equals(description, project.description) &&
                Objects.equals(deadline, project.deadline) &&
                Objects.equals(tasks, project.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, deadline, tasks);
    }
}
