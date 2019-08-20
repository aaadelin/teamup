package com.team.TeamUp.utils;

import com.team.TeamUp.domain.Task;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.domain.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class TaskSpecifications {

    public static Specification<Task> taskStatusIsMet(TaskStatus taskStatus) {
        return (Specification<Task>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("taskStatus"), taskStatus);
    }
    
    public static Specification<Task> taskIsReportedBy(User reporter) {
        return new Specification<Task>() {
            @Override
            public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("reporter"), reporter);
            }
        } ;
    }
}