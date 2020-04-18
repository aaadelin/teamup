package com.team.teamup.utils.query;

import com.team.teamup.domain.Task;
import com.team.teamup.persistence.TaskRepository;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractLanguageParser {
    protected final TaskRepository taskRepository;
    protected static final String SUMMARY = "summary";
    protected static final String DESCRIPTION = "description";
    protected static final String CREATED = "created";
    protected static final String LAST_CHANGED = "lastchanged";
    protected static final String DEADLINE = "deadline";
    protected static final String DIFFICULTY = "difficulty";
    protected static final String PRIORITY = "priority";
    protected static final String TYPE = "type";
    protected static final String STATUS = "status";
    protected static final String DEPARTMENT = "department";
    protected static final String ASSIGNEES = "assignees";
    protected static final String REPORTER = "reporter";
    protected static final String WHERE = "where";
    protected static final String NAME = "name";
    protected static final String OWNER = "owner";
    protected static final String VERSION = "version";
    protected static final String ARCHIVED = "archived";
    protected static final String LIKE = " like ";
    protected static final String PROJECT = "project";

    protected static final Predicate<Task> defaultPredicate = task -> true;
    public AbstractLanguageParser(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllByQuery(final String condition) {
        final String lwrCondition = condition.toLowerCase();

        int limit = 0;
        String[] words = lwrCondition.split(" ");
        if (words[words.length - 2].contains("limit") && isInteger(words[words.length - 1])) {
            limit = Integer.parseInt(words[words.length - 1]);
        }

        if (!lwrCondition.contains(WHERE) || lwrCondition.split(WHERE).length == 1) {
            return taskRepository.findAll();
        }
        String conditions = lwrCondition.split(WHERE)[1].split("limit")[0];

        String[] delimitQuotes = conditions.replace("\"", "\" ").split("\"");
        Map<Integer, String> searchTerms = new HashMap<>();

        for (int i = 1; i < delimitQuotes.length; i += 2) {
            Integer termPosition = i / 2;
            searchTerms.put(termPosition, delimitQuotes[i]);
            delimitQuotes[i] = "{" + termPosition + "}";
        }

        conditions = String.join("", Arrays.asList(delimitQuotes));
        Map<String, String> aliases = extractAliases(lwrCondition);
        List<Predicate<Task>> andPredicates = new ArrayList<>();
        for (String andCondition : conditions.split("and")) {
            andPredicates.add(reduceOrPredicates(searchTerms, andCondition, aliases));
        }
        return getFilteredTasks(limit, andPredicates);
    }

    protected abstract Predicate<Task> reduceOrPredicates(Map<Integer, String> searchTerms, String andCondition, Map<String, String> aliases);

    private List<Task> getFilteredTasks(int limit, List<Predicate<Task>> andPredicates) {
        List<Task> allTasks = taskRepository.findAll();
        limit = limit <= 0 ? allTasks.size() : limit;
        if (!andPredicates.isEmpty()) {
            Predicate<Task> predicate = andPredicates.stream().reduce(andPredicates.get(0), Predicate::and);
            return allTasks.stream().filter(predicate).limit(limit).collect(Collectors.toList());
        } else {
            return allTasks.stream().limit(limit).collect(Collectors.toList());
        }
    }

    private Map<String, String> extractAliases(final String condition) {
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

    public static boolean isInteger(String word) {
        try {
            Integer.parseInt(word);
            return true;
        } catch (NumberFormatException n) {
            return false;
        }
    }
}
