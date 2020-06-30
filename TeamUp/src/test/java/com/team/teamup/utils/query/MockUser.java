package com.team.teamup.utils.query;

import com.team.teamup.utils.query.annotations.SearchEntity;
import com.team.teamup.utils.query.annotations.SearchField;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@Entity
@SearchEntity
public class MockUser {

    @Id
    private int id;

    @SearchField
    private Integer age;

    @SearchField
    private String name;

    @SearchField
    private LocalDateTime bornDate;

    @SearchField(attribute = "address")
    private MockLocation location;

}
