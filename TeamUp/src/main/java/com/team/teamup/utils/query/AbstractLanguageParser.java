package com.team.teamup.utils.query;

import com.team.teamup.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractLanguageParser<T, I> {
    protected final JpaRepository<T, I> repository;
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
    public AbstractLanguageParser(JpaRepository<T, I> repository) {
        this.repository = repository;
    }

    public List<T> getAllByQuery(final String condition) {
        final String lwrCondition = condition.toLowerCase();

        int limit = 0;
        String[] words = lwrCondition.split(" ");
        if (words[words.length - 2].contains("limit") && isInteger(words[words.length - 1])) {
            limit = Integer.parseInt(words[words.length - 1]);
        }

        if (!lwrCondition.contains(WHERE) || lwrCondition.split(WHERE).length == 1) {
            return repository.findAll();
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
        List<Predicate<T>> andPredicates = new ArrayList<>();
        for (String andCondition : conditions.split("and")) {
            andPredicates.add(reduceOrPredicates(searchTerms, andCondition.strip(), aliases));
        }
        return getFilteredTasks(limit, andPredicates);
    }

    protected abstract Predicate<T> reduceOrPredicates(Map<Integer, String> searchTerms, String andCondition, Map<String, String> aliases);

    private List<T> getFilteredTasks(int limit, List<Predicate<T>> andPredicates) {
        List<T> allTasks = repository.findAll();
        limit = limit <= 0 ? allTasks.size() : limit;
        if (!andPredicates.isEmpty()) {
            Predicate<T> predicate = andPredicates.stream().reduce(andPredicates.get(0), Predicate::and);
            return allTasks.stream().filter(predicate).limit(limit).collect(Collectors.toList());
        } else {
            return allTasks.stream().limit(limit).collect(Collectors.toList());
        }
    }

    abstract Map<String, String> extractAliases(final String condition);

    public static boolean isInteger(String word) {
        try {
            Integer.parseInt(word);
            return true;
        } catch (NumberFormatException n) {
            return false;
        }
    }

    public static boolean isNumber(String word) {
        try {
            Double.parseDouble(word);
            return true;
        } catch (NumberFormatException n) {
            return false;
        }
    }
}
