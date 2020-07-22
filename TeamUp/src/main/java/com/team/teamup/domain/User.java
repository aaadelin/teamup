package com.team.teamup.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.teamup.domain.enums.UserStatus;
import com.team.teamup.utils.query.annotations.SearchEntity;
import com.team.teamup.utils.query.annotations.SearchField;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SearchEntity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String photo;

    private String newPhoto;

    private String mail;

    private String firstName;

    private String lastName;

    private Date joinedAt;

    private UserStatus status;

    @JsonBackReference(value = "userTeamGroups")
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    @ToString.Exclude
    private Team team;

    @OneToOne
    @JoinColumn(name = "AUTH_ID")
    private UserAuthentication authentication;

    @OneToOne
    @JoinColumn(name = "PREF_ID")
    private UserPreferences preferences;

    @OneToMany(mappedBy = "creator")
    @ToString.Exclude
    private List<UserEvent> history;

    public UserAuthentication getAuthentication(){
        if(authentication == null ){
            authentication = new UserAuthentication();
        }
        return authentication;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(photo, user.photo) &&
                Objects.equals(newPhoto, user.newPhoto) &&
                Objects.equals(mail, user.mail) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(joinedAt, user.joinedAt) &&
                status == user.status &&
                Objects.equals(team, user.team) &&
                Objects.equals(authentication, user.authentication) &&
                Objects.equals(preferences, user.preferences) &&
                Objects.equals(history, user.history);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, photo, newPhoto, mail, firstName, lastName, joinedAt, status, team, authentication, preferences, history);
    }
}

