package com.taskadapter.webui.license;

import com.taskadapter.data.MutableState;
import com.taskadapter.data.State;
import com.taskadapter.license.License;
import com.taskadapter.license.LicenseException;
import com.taskadapter.license.LicenseManager;

/**
 * Facade for license operations. It's not a good idea to bind any UI item to a
 * license manager because it is not thread-safe and such subscription requires
 * good amount of lifecycle-management (subscribe on activation, unsubscribe on
 * passivation). This class provides not-so-strict information about license (it
 * updates license information only when user attempts some operation) but
 * allows to create necessary facades for each eligible page.
 * 
 */
public final class LicenseFacade {
    /**
     * License manager.
     */
    private final LicenseManager licenseManager;

    /**
     * "Have valid license" flag.
     */
    private final MutableState<Boolean> haveValidLicense;

    /**
     * "Have any license" flag.
     */
    private final MutableState<License> currentLicense;

    /**
     * Creates a new facade for the given license manager.
     * 
     * @param licenseManager
     *            license manager.
     */
    public LicenseFacade(LicenseManager licenseManager) {
        this.licenseManager = licenseManager;
        this.haveValidLicense = new MutableState<>(
                licenseManager.isSomeValidLicenseInstalled());
        this.currentLicense = new MutableState<>(
                licenseManager.isSomeLicenseInstalled() ? licenseManager
                        .getLicense() : null);
    }

    /**
     * Returns "licensed" state. This state is updated only when user performs
     * operations on this facade but not on the license manager directly.
     * 
     * @return "licensed" state.
     */
    public State<Boolean> isLicensed() {
        return haveValidLicense;
    }

    /**
     * Returns state of current/active license.
     * 
     * @return current licensing state.
     */
    public State<License> getLicense() {
        return currentLicense;
    }

    /**
     * Instanlls a new license.
     * 
     * @param licenseText
     *            license text to install.
     * @throws LicenseException
     *             if license cannot be installed.
     * */
    public void install(String licenseText) throws LicenseException {
        licenseManager.setNewLicense(licenseText.trim());
        licenseManager.copyLicenseToConfigFolder();
        haveValidLicense.set(licenseManager.isSomeValidLicenseInstalled());
        currentLicense
                .set(licenseManager.isSomeLicenseInstalled() ? licenseManager
                        .getLicense() : null);
    }

    /**
     * Uninstalls a license.
     */
    public void uninstall() {
        licenseManager.removeTaskAdapterLicenseFromConfigFolder();
        haveValidLicense.set(Boolean.FALSE);
        currentLicense.set(null);
    }
}
