package com.taskadapter.webui;

import com.taskadapter.web.SettingsManager;
import com.taskadapter.webui.auth.PermissionViolationException;
import com.taskadapter.webui.auth.Permissions;
import com.taskadapter.webui.pages.Navigator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.RouterLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PreserveOnRefresh
public abstract class BasePage extends VerticalLayout implements BeforeEnterObserver,
                                                                 AfterNavigationObserver,
                                                                 HasDynamicTitle {

    @Override
    public String getPageTitle() {
        return getPageName(getClass()).map(name -> "TaskAdapter - " + name).orElse("TaskAdapter");
    }

    @Override
    public final void beforeEnter(final BeforeEnterEvent event) {
        Location location = event.getLocation();
        try {
            if (!location.getFirstSegment().equals(Navigator.LOGIN)) {
                Permissions.check(Permissions.LOGGED_IN);
            }
        } catch (PermissionViolationException e) {
            event.rerouteTo(Navigator.LOGIN);
            return;
        }

        if (!SettingsManager.isLicenseAgreementAccepted()
                && !location.getFirstSegment().equals(Navigator.LICENSE)
                && !location.getFirstSegment().equals(Navigator.LOGIN)) {
            event.rerouteTo(Navigator.LICENSE);
            return;
        }

        beforeEnter();
    }

    @Override
    public final void afterNavigation(final AfterNavigationEvent event) {
    }

    protected void beforeEnter() {
    }

    private static Optional<String> getPageName(final Class<? extends Component> aClass) {
        String className = aClass.getSimpleName();
        if (className.endsWith("Page")) {
            int length = className.length();
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length - 4 /* 'Page' suffix */; i++) {
                char charAt = className.charAt(i);
                if (i > 0 && Character.isUpperCase(charAt)) {
                    sb.append(' ');
                }
                sb.append(charAt);
            }
            return Optional.of(sb.toString());
        }
        return Optional.empty();
    }
}