package com.taskadapter.app;

public class TADevLauncher {
    public static void main(String[] args) throws Exception {
        System.setProperty(ProdModeDetector.LOCAL_DEV_RUN, "true");
        TALauncher.main(args);
    }
}
