package com.team.teamup.dtos;

import com.team.teamup.domain.enums.Department;

import java.util.List;

public class TeamDTO {

    private int id;

    private String name;

    private String description;

    private int locationId;

    private Department department;

    private int leaderID;

    private List<Integer> members;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocation() {
        return locationId;
    }

    public void setLocation(int location) {
        this.locationId = location;
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

    public int getLeaderID() {
        return leaderID;
    }

    public void setLeaderID(int leaderID) {
        this.leaderID = leaderID;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Integer> getMembers() {
        return members;
    }

    public void setMembers(List<Integer> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "TeamDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + locationId + '\'' +
                ", department=" + department +
                ", leaderID=" + leaderID +
                ", members=" + members +
                '}';
    }
}
