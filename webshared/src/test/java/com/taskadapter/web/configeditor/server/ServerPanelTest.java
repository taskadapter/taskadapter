package com.taskadapter.web.configeditor.server;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.vaadin.flow.data.binder.Binder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerPanelTest {
    /**
     * regression test to ensure that Vaadin panels can find setters on WebConnectorSetup.
     * if you change setters on a java class to return "this" (for chain-style), Java Beans won't find them,
     * and thus Vaadin will think those fields are read-only.
     */
    @Test
    public void canUnderstandSettersOnWebConnectorSetup() {
        var binder = new ServerPanel("id", "caption", new WebConnectorSetup()).getBinder();
        assertSetterFound(binder, "label");
        assertSetterFound(binder, "userName");
        assertSetterFound(binder, "host");
        assertSetterFound(binder, "password");
    }

    private static void assertSetterFound(Binder<WebConnectorSetup> binder, String property) {
        var binding = binder.getBinding(property).get();
        assertThat(binding.getSetter()).isNotNull();
    }
}
