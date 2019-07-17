package com.team.TeamUp.domain;

import com.team.TeamUp.domain.enums.Department;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Team {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    private String description;

    private String location;

    private Department department;

    @OneToOne
    @JoinColumn(name = "LEADER_ID")
    private User leader;

//    @JsonManagedReference(value = "userTeam")
//    @JoinColumn(name = "teamId")
    @OneToMany(mappedBy = "team")
    private List<User> members;

    public Team() {
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return id == team.id &&
                Objects.equals(location, team.location) &&
                Objects.equals(name, team.name) &&
                Objects.equals(description, team.description) &&
                Objects.equals(leader, team.leader) &&
                Objects.equals(members, team.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location, name, description, leader, members);
    }
}
