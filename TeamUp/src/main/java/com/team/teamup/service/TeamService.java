package com.team.teamup.service;

import com.team.teamup.domain.Team;
import com.team.teamup.domain.User;
import com.team.teamup.dtos.TeamDTO;
import com.team.teamup.persistence.TeamRepository;
import com.team.teamup.utils.DTOsConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
public class TeamService {

    private final TeamRepository teamRepository;
    private final DTOsConverter dtOsConverter;

    @Autowired
    public TeamService (TeamRepository teamRepository,
                        DTOsConverter dtOsConverter){
        this.teamRepository = teamRepository;
        this.dtOsConverter = dtOsConverter;
    }

    public List<Team> getAllTeams(){
        return teamRepository.findAll();
    }

    public List<TeamDTO> getAllTeamDTO(){
        return getAllTeams().stream().map(dtOsConverter::getDTOFromTeam).collect(Collectors.toList());
    }

    public Team getTeamByID(int id){
        return teamRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public TeamDTO getTeamDTOByID(int id){
        return dtOsConverter.getDTOFromTeam(getTeamByID(id));
    }

    public List<Team> getByLeader(User leader){
        return teamRepository.findAllByLeader(leader);
    }

    public Team save(Team newTeam) {
        return teamRepository.save(newTeam);
    }
}
