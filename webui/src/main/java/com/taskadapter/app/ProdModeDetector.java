package com.taskadapter.app;

public class ProdModeDetector {
    public static boolean isProdRunMode() {
        return !isLocalDevMode();
    }

    public static boolean isLocalDevMode() {
        return System.getenv("LOCAL_DEV_RUN") != null;
    }
}
