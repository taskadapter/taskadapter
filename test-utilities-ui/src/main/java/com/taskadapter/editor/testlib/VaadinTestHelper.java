package com.taskadapter.editor.testlib;

import com.vaadin.flow.server.DefaultDeploymentConfiguration;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VaadinTestHelper {
    public static void initVaadinSession(Class clazz) {
        VaadinSession.setCurrent(new TestVaadinSession(new VaadinServletService(
                new VaadinServlet(), new DefaultDeploymentConfiguration(
                clazz, new Properties()))));
    }

    static class TestVaadinSession extends VaadinSession {

        public TestVaadinSession(VaadinService service) {
            super(service);
            lock();
        }

        @Override
        public Lock getLockInstance() {
            return lock;
        }

        private ReentrantLock lock = new ReentrantLock();
    }
}
