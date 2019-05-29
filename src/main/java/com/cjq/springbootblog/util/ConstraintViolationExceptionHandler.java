package com.cjq.springbootblog.util;


import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * 约束违规异常处理器，用于处理持久化类的规则约束出现的异常
 */
public class ConstraintViolationExceptionHandler {

    public static String getMessage(ConstraintViolationException exception) {
        List<String> msgList = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : exception.getConstraintViolations()) {
            msgList.add(constraintViolation.getMessage());
        }

        String messages = StringUtils.join(msgList.toArray(), ";");
        return messages;
    }
}
