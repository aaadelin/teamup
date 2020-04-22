package com.team.teamup.utils.query;

import com.team.teamup.domain.HasNameAndDescription;
import com.team.teamup.domain.Task;
import com.team.teamup.domain.User;
import com.team.teamup.domain.enums.Department;
import com.team.teamup.domain.enums.TaskStatus;
import com.team.teamup.domain.enums.TaskType;
import com.team.teamup.persistence.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class QueryLanguageParser extends AbstractLanguageParser<Task, Integer> {

    // "SELECT TASK AS T WHERE (T.NAME=\"\" AND T.DESCRIPTION = \"\")OR (T.DEADLINE=2020-04-14 OR (T.ASSIGNEES=[] AND T.STATUS=INPROGRESS)) AND (T.STATUS=)"


    @Autowired
    public QueryLanguageParser(TaskRepository taskRepository) {
        super(taskRepository);
    }

    Map<String, String> extractAliases(final String condition) {
        String[] aliases = condition.split(WHERE)[0].replace("select", "").split(",");

        Map<String, String> aliasesMap = new HashMap<>();
        aliasesMap.put("task", "task.");
        aliasesMap.put(PROJECT, "project.");

        switch (aliases.length) {
            case 1:
                String[] alias = aliases[0].split(" as ");
                if (alias.length > 1) {
                    aliasesMap.put(alias[0].strip(), alias[1].strip() + ".");
                }
                return aliasesMap;
            case 2:
                String[] alias1 = aliases[0].split(" as ");
                String[] alias2 = aliases[1].split(" as ");
                aliasesMap.put(alias1[0].strip(), alias1[1].strip() + ".");
                aliasesMap.put(alias2[0].strip(), alias2[1].strip() + ".");
                return aliasesMap;
            default:
                return aliasesMap;
        }
    }

    public Predicate<Task> reduceOrPredicates(final Map<Integer, String> quotes,
                                              final String andCondition) {
        List<Predicate<Task>> orPredicates = new ArrayList<>();
        Map<String, String> aliases = extractAliases(andCondition);
        for (String orCondition : andCondition.split(" or ")) {
            orCondition = orCondition.strip();
            if (orCondition.contains("{")) {
                Integer key = Integer.parseInt(orCondition.split("\\{")[1].split("}")[0]);
                String quoted = "\"" + quotes.get(key) + "\"";
                orCondition = orCondition.replace("{" + key + "}", quoted);
            }
            Predicate<Task> predicate;
            if (orCondition.startsWith(aliases.get("task"))) {
                orCondition = orCondition.replace(aliases.get("task"), "");
                predicate = evaluateSimpleTaskCondition(orCondition);
            } else {
                orCondition = orCondition.replace(aliases.get(PROJECT), "");
                predicate = evaluateSimpleProjectCondition(orCondition);
            }
            orPredicates.add(predicate);
        }
        if (!orPredicates.isEmpty()) {
            return orPredicates.stream().reduce(orPredicates.get(0), Predicate::or);
        }
        return defaultPredicate;
    }

    public Predicate<Task> evaluateSimpleProjectCondition(String inputCondition) {
        //name= like, description= like, deadline= <= >=, owner= in, version= in, archived=

        final String condition = inputCondition.toLowerCase();
        boolean containsDate = Stream.of(DEADLINE).anyMatch(condition::contains);
        if (condition.contains("<=") && containsDate) {
            return lessOrEqualConditionProject(condition);
        }
        if (condition.contains(">=") && containsDate) {
            return greaterOrEqualConditionProject(condition);
        }
        if (condition.split("\"")[0].contains("=")) {
            return equalConditionProject(condition);
        }

        boolean canSearchInList = Stream.of(OWNER, VERSION).anyMatch(condition::contains);
        if (condition.contains(" in [") && canSearchInList) {
            return inListConditionProject(condition);
        }
        if (condition.split("\"")[0].contains(LIKE)) {
            // summary, description
            return likeCondition(condition, Task::getProject);
        }
        return defaultPredicate;
    }

    private Predicate<Task> greaterOrEqualConditionProject(String condition) {
        return compareProjectDates(condition, ">=", (projectDate, compareDate) -> projectDate.isAfter(compareDate) || projectDate.isEqual(compareDate));
    }

    private Predicate<Task> lessOrEqualConditionProject(String condition) {
        return compareProjectDates(condition, "<=", (projectDate, compareDate) -> projectDate.isBefore(compareDate) || projectDate.isEqual(compareDate));
    }

    private Predicate<Task> equalConditionProject(String condition) {
        final String[] conditionOperands = condition.split("=");
        final String leftOperand = conditionOperands[0].strip();
        final String rightOperand = conditionOperands[1].strip();

        switch (leftOperand) {
            case NAME:
                String searchItem = condition.split("\"")[1];
                return task -> task.getProject().getName().equalsIgnoreCase(searchItem.strip());
            case DESCRIPTION:
                searchItem = condition.split("\"")[1];
                return task -> task.getProject().getDescription().equalsIgnoreCase(searchItem.strip());
            case ARCHIVED:
                return task -> task.getProject().isArchived() == Boolean.parseBoolean(rightOperand);
            case DEADLINE:
                return getTaskDateFieldEquality(rightOperand, task -> task.getProject().getDeadline());
            case VERSION:
                return task -> task.getProject().getVersion().equals(rightOperand);
            case OWNER:
                return task -> task.getProject().getOwner().getUsername().equals(rightOperand);
            default:
                return defaultPredicate;
        }
    }

    private Predicate<Task> inListConditionProject(String condition) {
        final String[] operands = Arrays.stream(condition.split("in \\[")).map(String::strip).filter(s -> !s.isEmpty()).toArray(String[]::new);
        final String leftOperand = operands[0].strip();
        final String rightOperand = operands[1].split("]")[0].strip();
        final String[] comparableArray = Arrays.stream(rightOperand.split(",")).map(String::strip).toArray(String[]::new);

        switch (leftOperand) {
            case VERSION:
                return task -> Arrays.asList(comparableArray).contains(task.getProject().getVersion());
            case OWNER:
                return task -> Arrays.asList(comparableArray).contains(task.getProject().getOwner().getUsername());

            default:
                return defaultPredicate;
        }
    }


    public Predicate<Task> evaluateSimpleTaskCondition(String inputCondition) {
        //simple conditions like t.status=1, p.owner="user", t.status=

        final String condition = inputCondition.toLowerCase();
        boolean containsDate = Stream.of(CREATED, LAST_CHANGED, DEADLINE).anyMatch(condition::contains);
        if (condition.contains("<=") && containsDate) {
            return lessOrEqualCondition(condition);
        }
        if (condition.contains(">=") && containsDate) {
            return greaterOrEqualCondition(condition);
        }
        if (condition.split("\"")[0].contains("=")) {
            return equalCondition(condition);
        }

        boolean canSearchInList = Stream.of(DIFFICULTY, PRIORITY, TYPE, STATUS, ASSIGNEES, REPORTER).anyMatch(condition::contains);
        if (condition.contains(" in [") && canSearchInList) {
            return inListCondition(condition);
        }
        if (condition.split("\"")[0].contains(LIKE)) {
            // summary, description
            return likeCondition(condition, task -> task);
        }
        return defaultPredicate;
    }

    private Predicate<Task> equalCondition(final String condition) {
        final String[] conditionOperands = condition.split("=");
        final String leftOperand = conditionOperands[0].strip();
        final String rightOperand = conditionOperands[1].strip();

        switch (leftOperand) {
            case SUMMARY:
                String searchItem = condition.split("\"")[1];
                return task -> task.getSummary().equalsIgnoreCase(searchItem.strip());
            case DESCRIPTION:
                searchItem = condition.split("\"")[1];
                return task -> task.getDescription().equalsIgnoreCase(searchItem.strip());
            case CREATED:
                return getTaskDateFieldEquality(rightOperand, Task::getCreatedAt);
            case LAST_CHANGED:
                return getTaskDateFieldEquality(rightOperand, Task::getLastChanged);
            case DEADLINE:
                return getTaskDateFieldEquality(rightOperand, Task::getDeadline);
            case DIFFICULTY:
                return task -> task.getDifficulty() == Integer.parseInt(rightOperand.strip());
            case PRIORITY:
                return task -> task.getPriority() == Integer.parseInt(rightOperand.strip());
            case TYPE:
                return task -> task.getTaskType().equals(TaskType.valueOf(rightOperand.toUpperCase().strip()));
            case STATUS:
                return task -> task.getTaskStatus().equals(TaskStatus.valueOf(rightOperand.toUpperCase().strip()));
            case DEPARTMENT:
                return task -> task.getDepartment().equals(Department.valueOf(rightOperand.toUpperCase().strip()));
            case REPORTER:
                return task -> task.getReporter().getUsername().equals(rightOperand);
            case ASSIGNEES:
                List<String> usernames = Arrays.stream(rightOperand.split(",")).map(String::strip).collect(Collectors.toList());
                return task -> task.getAssignees().stream().map(User::getUsername).collect(Collectors.toList()).containsAll(usernames);
            default:
                return defaultPredicate;
        }
    }

    private Predicate<Task> getTaskDateFieldEquality(String rightOperand, Function<Task, LocalDateTime> getDate) {
        try {
            Integer[] inputDate = Arrays.stream(rightOperand.split("-")).map(Integer::parseInt).toArray(Integer[]::new);

            return task -> {
                LocalDateTime time = getDate.apply(task);
                return time.getYear() == inputDate[0] &&
                        time.getMonthValue() == inputDate[1] &&
                        time.getDayOfMonth() == inputDate[2];
            };
        } catch (NumberFormatException e) {
            return defaultPredicate;
        }
    }

    private Predicate<Task> lessOrEqualCondition(String condition) {
        //created, lastchanged, deadline
        return compareDates(condition, "<=", (taskDate, compareDate) -> taskDate.isBefore(compareDate) || taskDate.isEqual(compareDate));
    }

    private Predicate<Task> greaterOrEqualCondition(String condition) {
        //created, lastchanged, deadline
        return compareDates(condition, ">=", (taskDate, compareDate) -> taskDate.isAfter(compareDate) || taskDate.isEqual(compareDate));
    }

    private Predicate<Task> compareDates(String expression, String operator, BiPredicate<LocalDateTime, LocalDateTime> comparator) {
        final String[] parts = Arrays.stream(expression.split(operator)).map(String::strip).filter(s -> !s.isEmpty()).toArray(String[]::new);

        //convert date string to sql date
        try {
            Integer[] inputDate = Arrays.stream(parts[1].split("-")).map(Integer::parseInt).toArray(Integer[]::new);
            LocalDateTime date = LocalDateTime.of(inputDate[0], inputDate[1], inputDate[2], 0, 0);

            if (parts[0].contains(DEADLINE)) {
                return task -> comparator.test(task.getDeadline(), date);
            }
            if (parts[0].contains(LAST_CHANGED)) {
                return task -> comparator.test(task.getLastChanged(), date);
            }
            if (parts[0].contains(CREATED)) {
                return task -> comparator.test(task.getCreatedAt(), date);
            }
            return task -> comparator.test(task.getDeadline(), date);
        } catch (NumberFormatException exception) {
            return defaultPredicate;
        }
    }

    private Predicate<Task> compareProjectDates(String expression, String operator, BiPredicate<LocalDateTime, LocalDateTime> comparator) {
        final String[] parts = Arrays.stream(expression.split(operator)).map(String::strip).filter(s -> !s.isEmpty()).toArray(String[]::new);

        //convert date string to sql date
        try {
            Integer[] inputDate = Arrays.stream(parts[1].split("-")).map(Integer::parseInt).toArray(Integer[]::new);
            LocalDateTime date = LocalDateTime.of(inputDate[0], inputDate[1], inputDate[2], 0, 0);

            if (parts[0].contains(DEADLINE)) {
                return task -> comparator.test(task.getProject().getDeadline(), date);
            }
            return task -> comparator.test(task.getDeadline(), date);
        } catch (NumberFormatException exception) {
            return defaultPredicate;
        }
    }

    private Predicate<Task> inListCondition(String condition) {
        final String[] operands = Arrays.stream(condition.split("in \\[")).map(String::strip).filter(s -> !s.isEmpty()).toArray(String[]::new);
        final String leftOperand = operands[0].strip();
        final String rightOperand = operands[1].split("]")[0].strip();
        final String[] comparableArray = Arrays.stream(rightOperand.split(",")).map(String::strip).toArray(String[]::new);

        switch (leftOperand) {
            case DIFFICULTY:
                List<Integer> ints = Arrays.stream(comparableArray).map(Integer::parseInt).collect(Collectors.toList());
                return task -> ints.contains(task.getDifficulty());
            case PRIORITY:
                ints = Arrays.stream(comparableArray).map(Integer::parseInt).collect(Collectors.toList());
                return task -> ints.contains(task.getPriority());
            case TYPE:
                //transform from string to taskType
                List<TaskType> types = Arrays.stream(comparableArray).map(String::toUpperCase).map(TaskType::valueOf).collect(Collectors.toList());
                return task -> types.contains(task.getTaskType());
            case STATUS:
                //transform from string to taskStatus
                List<TaskStatus> statuses = Arrays.stream(comparableArray).map(String::toUpperCase).map(TaskStatus::valueOf).collect(Collectors.toList());
                return task -> statuses.contains(task.getTaskStatus());
            case ASSIGNEES:
                // for users it's onlu supported the username
                return task -> task.getAssignees().stream().anyMatch(user -> Arrays.asList(comparableArray).contains(user.getUsername()));
            case REPORTER:
                return task -> Arrays.asList(comparableArray).contains(task.getReporter().getUsername());

            default:
                return defaultPredicate;
        }
    }

    private Predicate<Task> likeCondition(String condition, Function<Task, HasNameAndDescription> getOperator) {
        final String[] conditionStrings = condition.replace(LIKE, " ").split("\""); // summary like "items in a teapot" -> [summary, items in a teapot, ]
        final String leftOperand = conditionStrings[0].strip();
        final String stringToBeContained = conditionStrings[1].strip();

        switch (leftOperand) {
            case SUMMARY:
            case NAME:
                return task -> getOperator.apply(task).getName().toLowerCase().contains(stringToBeContained);
            case DESCRIPTION:
                return task -> getOperator.apply(task).getDescription().toLowerCase().contains(stringToBeContained);
            default:
                return defaultPredicate;
        }
    }
}
