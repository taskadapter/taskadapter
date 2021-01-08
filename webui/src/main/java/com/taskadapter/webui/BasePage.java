package com.taskadapter.webui;

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

//    private HistoryState historyState;

//    public static Supplier<ServiceException> notFound(final Sid sid) {
//        return () -> new NotFoundException(sid);
//    }

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

//        historyState = new HistoryState(location);
        beforeEnter();
    }

    @Override
    public final void afterNavigation(final AfterNavigationEvent event) {
//        final var location = event.getLocation();
//        for (final HasElement element : event.getActiveChain()) {
//            if (element instanceof Layout) {
//                final var breadCrumb = ((Layout) element).getBreadCrumb();
//                breadCrumb.update(getBreadCrumbComponents(location));
//                break;
//            }
//        }
    }

/*    protected List<Component> getBreadCrumbComponents(final Location location) {
        final List<Component> components = new ArrayList<>();

        final var registry = UI.getCurrent().getRouter().getRegistry();
        final var main = registry.getNavigationTarget(location.getFirstSegment());
        main.ifPresent(target -> {
            getPageName(target).ifPresent(pageName -> {
                components.add(new RouterLink(pageName, target));
            });
        });

        if (location.getSegments().size() == 2) {
            final var lastSegment = location.getSegments().get(location.getSegments().size() - 1);
            final var href = String.join("/", location.getSegments());
            components.add(new Anchor(href, lastSegment));
        }

        return components;
    }*/

    protected void beforeEnter() {
        // subclasses could override
    }

//    protected final HistoryState historyState() {
//        return historyState;
//    }

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