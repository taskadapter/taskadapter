package com.taskadapter.webui.auth;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;

public class ExceptionPage extends VerticalLayout implements HasUrlParameter<String> {

    public ExceptionPage() {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setWidthFull();
    }

    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter final String parameter) {
        // ignored, but we need this to overcome some issues with "Refresh" on error page
    }

    protected void showError(final Exception exception) {
        showError(VaadinIcon.WARNING, "An unexpected exception occurred", exception);
    }

    protected void showError(final VaadinIcon icon, final String message) {
        showError(icon, message, (String) null);
    }

    protected void showError(final VaadinIcon icon, final String message, final Exception exception) {
        showError(icon, message, exception != null ? Throwables.getStackTraceAsString(exception) : null);
    }

    protected void showError(final VaadinIcon icon, final String message, final String stackTrace) {
        removeAll();
        add(new Icon(icon));
        if (!Strings.isNullOrEmpty(message)) {
            final Span span = new Span(message);
            span.setId("exception-message");
            add(span);
        }
        if (stackTrace != null) {
            final TextArea textArea = new TextArea();
            textArea.setId("exception-stacktrace");
            textArea.setWidth("80%");
            textArea.setValue(stackTrace);
            textArea.setReadOnly(true);
            add(textArea);
        }
    }
}
