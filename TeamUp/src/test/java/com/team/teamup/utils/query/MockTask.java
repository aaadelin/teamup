package com.team.teamup.utils.query;

import com.team.teamup.utils.query.annotations.SearchEntity;
import com.team.teamup.utils.query.annotations.SearchField;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.mockito.Mock;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SearchEntity
@Entity
@Builder
@Getter
@ToString
public class MockTask {

    @Id
    private Integer id;
    @SearchField
    private Integer priority;
    @SearchField
    private Integer difficulty;
    @SearchField
    private String summary;
    @SearchField
    private String description;
    @SearchField
    private LocalDateTime lastChanged;
    @SearchField(name = "sfarsit")
    private LocalDate deadline;
    @SearchField
    private LocalDateTime created;
    @SearchField(attributes = {"name", "location"})
    private MockUser owner;
}
