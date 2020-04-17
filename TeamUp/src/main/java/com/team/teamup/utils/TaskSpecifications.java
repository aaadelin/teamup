package com.team.teamup.utils;

import com.team.teamup.domain.Task;
import com.team.teamup.domain.User;
import com.team.teamup.domain.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Specifications utilitary class
 */
public class TaskSpecifications {

    /**
     *
     * @param taskStatus task status to be contained
     * @return The specification of the evaluation
     */
    public static Specification<Task> taskStatusIsMet(TaskStatus taskStatus) {
        return (Specification<Task>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("taskStatus"), taskStatus);
    }

    /**
     *
     * @param reporter user to be found in reporter of the tasks
     * @return Specification if the user is contained in the list
     */
    public static Specification<Task> taskIsReportedBy(User reporter) {
        return new Specification<Task>() {
            @Override
            public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("reporter"), reporter);
            }
        } ;
    }
}