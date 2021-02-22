package com.taskadapter.app;

public class ProdModeDetector {

    public static final String LOCAL_DEV_RUN = "LOCAL_DEV_RUN";

    public static boolean isProdRunMode() {
        return !isLocalDevMode();
    }

    public static boolean isLocalDevMode() {
        return System.getProperty(LOCAL_DEV_RUN) != null;
    }
}
