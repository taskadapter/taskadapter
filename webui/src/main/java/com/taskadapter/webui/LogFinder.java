package com.taskadapter.webui;

import ch.qos.logback.core.rolling.RollingFileAppender;

import java.io.File;

public class LogFinder {
    public static String getLogFileLocation() {
        var logger = org.apache.log4j.Logger.getRootLogger();
        var allAppenders = logger.getAllAppenders();
        while (allAppenders.hasMoreElements()) {
            var e = allAppenders.nextElement();
            if (e instanceof RollingFileAppender) {
                // found it
                var filePath = ((RollingFileAppender) e).getFile();
                return new File(filePath).getAbsolutePath();
            }
        }
        return "";
    }
}
