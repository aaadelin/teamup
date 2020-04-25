package com.team.teamup.utils.query.comparator;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

@Slf4j
public class DateCompare extends AbstractComparator {

    public DateCompare(Deque<Class<?>> classes, Queue<Method> methods){
        super(classes, methods);
    }

    /**
     *
     * @param field class field
     * @param remainingCondition string containing the operator and the date
     * @return predicate
     */
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

    /**
     *
     * @param field class field, should be date or datetime
     * @param operator boolean binary operator
     * @param rightOperand date time to compare to (provided by user)
     * @return predicate
     * @throws NoSuchMethodException if field doesn't have a getter
     */
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

    /**
     * if right operand (provided by user) doesn't have time, compare only year, month and day, otherwise use equals
     * @param leftOperand date time object
     * @param rightOperand date time object
     * @return if the parameters are equal
     */
    private boolean checkDateEquality(LocalDateTime leftOperand, LocalDateTime rightOperand) {
        if (rightOperand.getHour() == 0 && rightOperand.getMinute() == 0 && rightOperand.getSecond() == 0) {
            //no time provided
            return leftOperand.getYear() == rightOperand.getYear() &&
                    leftOperand.getMonthValue() == rightOperand.getMonthValue() &&
                    leftOperand.getDayOfMonth() == rightOperand.getDayOfMonth();
        }
        return leftOperand.equals(rightOperand);
    }

    /**
     * convert string to date time
     * @param operator string containing a date and [optional] a time
     * @return date time object
     */
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

    /**
     * convert string to date
     * @param date string of date year-month-day
     * @return localdate object from string
     */
    private LocalDate getDateFromString(String date) {
        Integer[] components = Arrays.stream(date.split("-")).map(String::strip).map(Integer::parseInt).toArray(Integer[]::new);
        return LocalDate.of(components[0], components[1], components[2]);
    }

    /**
     * convert string to localtime
     * @param time string of time containing hour:minute:second
     * @return localtime object from string
     */
    private LocalTime getTimeFromString(String time) {
        Integer[] components = Arrays.stream(time.split(":")).map(Integer::parseInt).toArray(Integer[]::new);
        if (components.length == 2) {
            return LocalTime.of(components[0], components[1]);
        }
        return LocalTime.of(components[0], components[1], components[2]);
    }

}
