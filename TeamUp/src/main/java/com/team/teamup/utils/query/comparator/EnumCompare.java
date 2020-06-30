package com.team.teamup.utils.query.comparator;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

@Slf4j
public class EnumCompare extends AbstractComparator {

    public EnumCompare(Deque<Class<?>> classes, Queue<Method> methods) {
        super(classes, methods);
    }

    /**
     *
     * @param field class field, should be an enum
     * @param remainingCondition string with operator and values to compare. should be correct values of that enum
     * @return predicate
     */
    public Predicate<Object> compare(Field field, String remainingCondition) {
        List<String> dateOperators = List.of("=", "!=", "in ");
        for (String operator : dateOperators) {
            if (remainingCondition.startsWith(operator)) {
                String rightOperand = remainingCondition.replaceFirst(operator, "").strip();
                try {
                    return enumOperator(field, operator, rightOperand);
                } catch (NoSuchMethodException e) {
                    log.info(e.getMessage());
                }
            }
        }
        //todo throw exception
        return t -> true;
    }

    /**
     *
     * @param field class field
     * @param operator binary operator
     * @param rightOperand enum value or values separated by comma
     * @return predicate
     * @throws NoSuchMethodException if enum doesn't have a getter
     */
    private Predicate<Object> enumOperator(final Field field, final String operator, final String rightOperand) throws NoSuchMethodException {
        List<Method> methodsCopy = getMethods(field);
        return t -> {
            try {
                Object leftOperand = invokeAllMethods(t, methodsCopy);
                if (leftOperand == null) {
                    return true;
                }
                switch (operator) {
                    case "=":
                        return rightOperand.equalsIgnoreCase(leftOperand.toString());
                    case "!=":
                        return !rightOperand.equalsIgnoreCase(leftOperand.toString());
                    case "in ":
                        String[] options = rightOperand.split("\\[")[1].split("]")[0].split(",");
                        return Arrays.stream(options).map(String::strip).anyMatch(s -> s.equalsIgnoreCase(leftOperand.toString()));
                    default:
                        return true;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.info(e.getMessage());
            }
            //todo throw exception
            return true;
        };

    }

}
