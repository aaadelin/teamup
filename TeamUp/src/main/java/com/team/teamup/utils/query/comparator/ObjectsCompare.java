package com.team.teamup.utils.query.comparator;

import com.team.teamup.utils.query.NoAttributeException;
import com.team.teamup.utils.query.exceptions.NoClassRemainingException;
import com.team.teamup.utils.query.exceptions.RecursivePredicate;
import com.team.teamup.utils.query.annotations.SearchField;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Predicate;

import static com.team.teamup.utils.query.ReflectionQueryLanguageParser.getSearchFields;

@Slf4j
public class ObjectsCompare extends AbstractComparator {

    public ObjectsCompare(Deque<Class<?>> classes, Queue<Method> methods) {
        super(classes, methods);
    }

    private RecursivePredicate recursiveMethod;
    public void setRecursiveMethod(RecursivePredicate method){
        this.recursiveMethod = method;
    }

    /**
     *
     * @param field
     * @param remainingCondition
     * @param searchTerms
     * @return
     */
    public Predicate<Object> compare(Field field, String remainingCondition, Map<Integer, String> searchTerms) {
        Map<String, Field> fieldMap = getSearchFields(field.getType());
        SearchField annotation = field.getAnnotation(SearchField.class);

        if (remainingCondition.startsWith(".")) {
            return pushAttributeToStack(field, remainingCondition.substring(1), searchTerms, fieldMap, findNextAttribute(remainingCondition.substring(1)));
        }

        String attribute = annotation.attribute();
        if (attribute.equalsIgnoreCase("")) {
            //should be one
            throw new NoAttributeException();
        }

        if (fieldMap.containsKey(attribute)) {
            //compare directly to that key
            return pushAttributeToStack(field, attribute + remainingCondition, searchTerms, fieldMap, attribute);
        }

        return t -> true;
    }

    /**
     * in case of multiple level attribute, find the next one (task.owner.name -> task; owner.name -> name )
     * @param substring substring containing the attribute list and condition
     * @return string with the next attribute
     */
    private String findNextAttribute(String substring) {
        //separator: spatiu . = < > !
        List<String> separators = List.of(" ", ".", "<", ">", "=", "!");

        int minIndex = substring.length();
        String minSeparator = "";
        for (String separator : separators) {
            int idx = substring.indexOf(separator);
            if (idx < minIndex && idx != -1) {
                minIndex = idx;
                minSeparator = separator;
            }
        }
        if (minSeparator.equals(".")) {
            minSeparator = "\\.";
        }
        return substring.split(minSeparator)[0];
    }

    /**
     *
     * @param field class's field
     * @param remainingCondition string containing the operator and right operand
     * @param searchTerms quoted strings from query
     * @param fieldMap fields available in the class
     * @param attribute attribute to search for in the field object
     * @return predicate
     */
    private Predicate<Object> pushAttributeToStack(Field field, String remainingCondition, Map<Integer, String> searchTerms, Map<String, Field> fieldMap, String attribute) {
        Field field1 = fieldMap.get(attribute);

        String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        Method method;
        try {
            if(classes.peek() == null){
                throw new NoClassRemainingException();
            }
            method = classes.peek().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            log.info(e.getMessage());
            return t -> true;
        }
        methods.add(method);

        Class<?> type;
        if (field.getType().getSimpleName().equalsIgnoreCase("list")) {
            try {
                type = Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
            } catch (ClassNotFoundException e) {
                return t -> true;
            }
        } else {
            type = field.getType();
        }

        classes.push(type);
        Predicate<Object> predicate = recursiveMethod.execute(remainingCondition, field1, attribute, searchTerms);
        classes.pop();
        return predicate;
    }

}
