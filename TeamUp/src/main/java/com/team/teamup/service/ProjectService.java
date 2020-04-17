package com.team.teamup.service;

import com.team.teamup.domain.Project;
import com.team.teamup.domain.User;
import com.team.teamup.dtos.ProjectDTO;
import com.team.teamup.persistence.ProjectRepository;
import com.team.teamup.utils.DTOsConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
public class ProjectService {
    private static final int PAGE_SIZE = 10;

    private ProjectRepository projectRepository;
    private DTOsConverter dtOsConverter;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          DTOsConverter dtOsConverter){
        this.projectRepository = projectRepository;
        this.dtOsConverter = dtOsConverter;
    }

    public Project getByID(int id){
        return projectRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public ProjectDTO getDTOByID(int id){
        return dtOsConverter.getDTOFromProject(getByID(id));
    }

    public List<ProjectDTO> findByTermInPage(String search, int page){
        if(search != null){
            return projectRepository.findAllByNameContainingOrDescriptionContaining(search, search, PageRequest.of(page, PAGE_SIZE))
                    .stream().map(dtOsConverter::getDTOFromProject).collect(Collectors.toList());
        }else{
            return projectRepository.findAll(PageRequest.of(page, PAGE_SIZE)).stream().map(dtOsConverter::getDTOFromProject).collect(Collectors.toList());
        }
    }

    public List<Project> getByOwner(User owner){
        return projectRepository.findAllByOwner(owner);
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }
}
