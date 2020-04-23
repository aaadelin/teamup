package com.team.teamup.utils.query.comparator;

import com.team.teamup.utils.query.comparator.AbstractComparator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;

@Slf4j
public class DateCompare extends AbstractComparator {

    public DateCompare(Stack<Class<?>> classes, Queue<Method> methods){
        super(classes, methods);
    }

    public Predicate<Object> compare(Field field, String remainingCondition) {
        List<String> dateOperators = List.of("=", "!=", ">=", "<=", "<", ">");
        for (String operator : dateOperators) {
            if (remainingCondition.startsWith(operator)) {
                LocalDateTime rightOperand = getDateFromRightOperand(remainingCondition.replaceFirst(operator, ""));
                try {
                    return simpleDateOperator(field, operator, rightOperand);
                } catch (NoSuchMethodException e) {
                    log.info(e.getMessage());
                }
            }
        }
        return t -> true;
    }

    private Predicate<Object> simpleDateOperator(Field field, String operator, LocalDateTime rightOperand) throws NoSuchMethodException {
        List<Method> methodsCopy = getMethods(field);
        return t -> {
            try {
                LocalDateTime leftOperand;
                if (field.getType().getSimpleName().equals("LocalDate")) {
                    leftOperand = ((LocalDate) invokeAllMethods(t, methodsCopy)).atStartOfDay();
                } else {
                    leftOperand = (LocalDateTime) invokeAllMethods(t, methodsCopy);
                }

                switch (operator) {
                    case "=":
                        return checkDateEquality(leftOperand, rightOperand);
                    case "!=":
                        return !checkDateEquality(leftOperand, rightOperand);
                    case ">=":
                        return leftOperand.isAfter(rightOperand) || checkDateEquality(leftOperand, rightOperand);
                    case "<=":
                        return leftOperand.isBefore(rightOperand) || checkDateEquality(leftOperand, rightOperand);
                    case "<":
                        return leftOperand.isBefore(rightOperand);
                    case ">":
                        return leftOperand.isAfter(rightOperand);
                    default:
                        return true;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                return true;
            }
        };
    }

    private boolean checkDateEquality(LocalDateTime leftOperand, LocalDateTime rightOperand) {
        if (rightOperand.getHour() == 0 && rightOperand.getMinute() == 0 && rightOperand.getSecond() == 0) {
            //no time provided
            return leftOperand.getYear() == rightOperand.getYear() &&
                    leftOperand.getMonthValue() == rightOperand.getMonthValue() &&
                    leftOperand.getDayOfMonth() == rightOperand.getDayOfMonth();
        }
        return leftOperand.equals(rightOperand);
    }

    private LocalDateTime getDateFromRightOperand(String operator) {
        if (operator.contains(":")) {
            //contains hour
            String[] components = operator.strip().split(" ");
            String date = components[0].strip();
            String time = components[1].strip();
            return LocalDateTime.of(getDateFromString(date), getTimeFromString(time));
        }

        return LocalDateTime.of(getDateFromString(operator), LocalTime.of(0, 0));
    }

    private LocalDate getDateFromString(String date) {
        Integer[] components = Arrays.stream(date.split("-")).map(String::strip).map(Integer::parseInt).toArray(Integer[]::new);
        return LocalDate.of(components[0], components[1], components[2]);
    }

    private LocalTime getTimeFromString(String time) {
        Integer[] components = Arrays.stream(time.split(":")).map(Integer::parseInt).toArray(Integer[]::new);
        if (components.length == 2) {
            return LocalTime.of(components[0], components[1]);
        }
        return LocalTime.of(components[0], components[1], components[2]);
    }

}
