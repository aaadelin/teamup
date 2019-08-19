package com.team.TeamUp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.TeamUp.domain.enums.Department;
import com.team.TeamUp.domain.enums.UserStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class User {

    @Id
    @GeneratedValue
    private int id;

    private String username;

    @JsonIgnore
    private String password;

    private String photo;

    private String newPhoto;

    private String firstName;

    private String lastName;

    private LocalDateTime lastActive;

    private boolean isActive;

    private String hashKey;

    private UserStatus status;
    @Column(columnDefinition = "integer default 60")
    private int minutesUntilLogout;

    @JsonBackReference(value = "userTeamGroups")
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getNewPhoto() {
        return newPhoto;
    }

    public void setNewPhoto(String newPhoto) {
        this.newPhoto = newPhoto;
    }

    public int getMinutesUntilLogout() {
        return minutesUntilLogout;
    }

    public void setMinutesUntilLogout(int minutesUntilLogout) {
        this.minutesUntilLogout = minutesUntilLogout;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", photo='" + photo + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", lastActive=" + lastActive +
                ", isActive=" + isActive +
                ", hashKey='" + hashKey + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                isActive == user.isActive &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(photo, user.photo) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(lastActive, user.lastActive) &&
                Objects.equals(hashKey, user.hashKey) &&
                status == user.status &&
                Objects.equals(team, user.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, photo, firstName, lastName, lastActive, isActive, hashKey, status, team);
    }
}

