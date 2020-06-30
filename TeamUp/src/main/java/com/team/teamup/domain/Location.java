package com.team.teamup.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String city;

    private String country;

    private String address;

    @OneToMany(mappedBy = "id")
    @JsonBackReference(value = "teamLocation")
    private List<Team> teams;
}
