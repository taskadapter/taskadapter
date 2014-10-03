package com.taskadapter.editor.testlib;

import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VaadinTestHelper {
    public static void initVaadinSession(Class clazz) {
        try {
            VaadinSession.setCurrent(new TestVaadinSession(new VaadinServletService(
                    new VaadinServlet(), new DefaultDeploymentConfiguration(
                    clazz, new Properties()))));
        } catch (ServiceException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
