package com.team.teamup.utils.query;

import com.team.teamup.utils.Pair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractLanguageParser<T, I> {
    protected final JpaRepository<T, I> repository;
    protected static final String WHERE = "where";

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
        Map<Integer, String> searchTerms = getQuotedSearchTerms(conditions).getValue();

        conditions = getQuotedSearchTerms(conditions).getKey();
        List<Predicate<Object>> andPredicates = new ArrayList<>();
        for (String andCondition : conditions.split("and")) {
            andPredicates.add(reduceOrPredicates(searchTerms, andCondition.strip()));
        }
        return getFilteredTasks(limit, andPredicates);
    }

    protected static Pair<String, Map<Integer, String>> getQuotedSearchTerms(final String condition){
        Matcher matcher = Pattern.compile("\".*?\"").matcher(condition);
        StringBuffer stringBuffer = new StringBuffer();
        Map<Integer, String> aliases = new HashMap<>();
        int i=1;
        while (matcher.find()){
            String key = "{" + i + "}";
            aliases.put(i++, matcher.group().replace("\"", ""));
            matcher.appendReplacement(stringBuffer, key);
        }
        matcher.appendTail(stringBuffer);
        return new Pair<>(stringBuffer.toString(), aliases);
    }

    protected abstract Predicate<Object> reduceOrPredicates(Map<Integer, String> searchTerms, String andCondition);

    private List<T> getFilteredTasks(int limit, List<Predicate<Object>> andPredicates) {
        List<T> allTasks = repository.findAll();
        limit = limit <= 0 ? allTasks.size() : limit;
        if (!andPredicates.isEmpty()) {
            Predicate<Object> predicate = andPredicates.stream().reduce(andPredicates.get(0), Predicate::and);
            return allTasks.stream().filter(predicate).limit(limit).collect(Collectors.toList());
        } else {
            return allTasks.stream().limit(limit).collect(Collectors.toList());
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

    public static boolean isNumber(String word) {
        try {
            Double.parseDouble(word);
            return true;
        } catch (NumberFormatException n) {
            return false;
        }
    }
}
