package com.costumeshop.info.utils;

import com.costumeshop.info.codes.BasicMessageCode;
import org.slf4j.Logger;

public class CodeMessageUtils {
    public static String getMessage(BasicMessageCode code) {
        return code.name() + ": " + code.getMessage();
    }

    public static String getMessage(String message, Integer option) {
        return message.replace("%1", String.valueOf(option));
    }

    public static String getMessage(BasicMessageCode code, Integer option) {
        String baseMessage = code.name() + ": " + code.getMessage();
        return baseMessage.replace("%1", String.valueOf(option));
    }

    public static String getMessage(BasicMessageCode code, String option) {
        String baseMessage = code.name() + ": " + code.getMessage();
        return baseMessage.replace("%1", option);
    }

    public static String getMessage(BasicMessageCode code, Integer firstOption, Integer secondOption) {
        String baseMessage = code.name() + ": " + code.getMessage();
        return baseMessage
                .replace("%1", String.valueOf(firstOption))
                .replace("%2", String.valueOf(secondOption));
    }

    public static String getMessage(BasicMessageCode code, Throwable cause) {
        return code.name() + ": " + code.getMessage() + " " + cause.getMessage();
    }

    public static String getMessage(BasicMessageCode code, Integer option, Throwable cause) {
        String baseMessage = code.name() + ": " + code.getMessage() + " " + cause.getMessage();
        return baseMessage.replace("%1", String.valueOf(option));
    }

    public static void logMessageAndPrintStackTrace(BasicMessageCode code, Exception e, Logger logger) {
        logger.error(CodeMessageUtils.getMessage(code, e));
        e.printStackTrace();
    }

    public static void logMessageAndPrintStackTrace(BasicMessageCode code, Integer option, Exception e, Logger logger) {
        logger.error(CodeMessageUtils.getMessage(code, option, e));
        e.printStackTrace();
    }

    public static void logMessage(BasicMessageCode code, Logger logger) {
        logger.info(CodeMessageUtils.getMessage(code));
    }

    public static void logMessage(BasicMessageCode code, Integer option, Logger logger) {
        logger.info(CodeMessageUtils.getMessage(code, option));
    }
    public static void logMessage(BasicMessageCode code, String option, Logger logger) {
        logger.info(CodeMessageUtils.getMessage(code, option));
    }

    public static void logMessage(BasicMessageCode code, Integer firstOption, Integer secondOption, Logger logger) {
        logger.info(CodeMessageUtils.getMessage(code, firstOption, secondOption));
    }
}
