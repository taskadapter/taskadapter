package com.taskadapter.webui.pageset;

import com.taskadapter.webui.Header;
import com.taskadapter.webui.TAPageLayout;
import com.taskadapter.webui.Tracker;
import com.taskadapter.webui.license.LicenseFacade;
import com.taskadapter.webui.pages.LoginPage;
import com.taskadapter.webui.pages.SupportPage;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * Pageset available for all users.
 */
public final class WelcomePageset {

    /**
     * Global (app-wide) services.
     */
    private final Preservices services;

    /**
     * License facade.
     */
    private final LicenseFacade license;

    /**
     * Callback to use on operations.
     */
    private final LoginPage.Callback callback;

    /**
     * Usage tracer.
     */
    private final Tracker tracker;

    /**
     * Ui component.
     */
    private final Component ui;

    /**
     * Area for the current page.
     */
    private final HorizontalLayout currentComponentArea = new HorizontalLayout();

    /**
     * Creates a new pageset.
     * 
     * @param services
     *            used services.
     * @param tracker
     *            usage tracker.
     * @param callback
     *            callback to use.
     */
    private WelcomePageset(Preservices services, Tracker tracker,
            LoginPage.Callback callback) {
        this.services = services;
        this.tracker = tracker;
        this.callback = callback;
        this.license = new LicenseFacade(services.licenseManager);

        final Component header = Header.render(new Runnable() {
            @Override
            public void run() {
                showLogin();
            }
        }, createMenu(), new HorizontalLayout(), license.isLicensed());

        ui = TAPageLayout.layoutPage(header, currentComponentArea);
    }

    /**
     * Creates a menu panel.
     * 
     * @return menu panel.
     */
    private Component createMenu() {
        final HorizontalLayout menu = new HorizontalLayout();
        final Button supportButton = new Button("Support");
        supportButton.setStyleName(BaseTheme.BUTTON_LINK);
        supportButton.addStyleName("menu");
        supportButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                showSupport();
            }
        });
        menu.addComponent(supportButton);
        return menu;
    }

    /**
     * Shows a login page.
     */
    private void showLogin() {
        tracker.trackPage("login");
        applyUI(LoginPage.createUI(callback));
    }

    /**
     * Shows a support page.
     */
    private void showSupport() {
        tracker.trackPage("support");
        applyUI(SupportPage.render(services.currentTaskAdapterVersion, license));
    }

    /**
     * Applies a new content.
     * 
     * @param ui
     *            new content.
     */
    private void applyUI(Component ui) {
        currentComponentArea.removeAllComponents();
        ui.setSizeUndefined();
        currentComponentArea.addComponent(ui);
        currentComponentArea.setComponentAlignment(ui, Alignment.TOP_LEFT);
    }

    /**
     * Creates a new welcome pageset.
     * 
     * @param services
     *            global services.
     * @param tracker
     *            usage tracker.
     * @param callback
     *            callback to invoke.
     * @param isLicensed
     *            "licensed" flag.
     * @return welcompe pageset.
     */
    public static Component createPageset(Preservices services,
            Tracker tracker, LoginPage.Callback callback) {
        final WelcomePageset ctl = new WelcomePageset(services, tracker,
                callback);
        ctl.showLogin();
        return ctl.ui;
    }
}
