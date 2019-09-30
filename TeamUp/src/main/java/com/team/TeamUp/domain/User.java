package com.team.TeamUp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.TeamUp.domain.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@ToString
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
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

    private Date joinedAt;

    private UserStatus status;
    @Column(columnDefinition = "integer default 60")
    private int minutesUntilLogout;

    @JsonBackReference(value = "userTeamGroups")
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    @ToString.Exclude
    private Team team;

    @OneToMany(mappedBy = "creator")
    @ToString.Exclude
    private List<UserEvent> history;

    private String mail;

    private boolean locked;

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

