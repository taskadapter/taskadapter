package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.prefs.Preferences;

/**
 * @author Alexey Skorokhodov
 */
public class LicenseManager {

    public static enum Product {
        TASK_ADAPTER_WEB
    }

    public static final int TRIAL_TASKS_NUMBER_LIMIT = 10;

    public static final String TRIAL_MESSAGE =
            "=== TRIAL VERSION LIMIT: will only process UP TO " + TRIAL_TASKS_NUMBER_LIMIT + " tasks ===";

    private static final String USAGE_TEXT =
            "Usage:\n" +
                    "  ./licenses [license_file_to_install] [-clean]\n\n" +
                    "  [license_file_to_install]     Install license from this file (full or relative path)\n" +
                    "  -clean                        Clean all installed licenses from this computer.";


    private static final String DASHES = "\n------------------------------\n";
    private static final String COMMAND_CLEAN = "-clean";

    private Collection<LicenseChangeListener> listeners = new HashSet<LicenseChangeListener>();

    private License license;

    public LicenseManager() {
        license = getInstalledTaskAdapterLicense();
    }

    // TODO this all needs to be refactored!!
    public void setNewLicense(String licenseText) throws LicenseException {
        license = new LicenseParser().parseLicense(licenseText);
        license.validate();
    }

    public void applyLicense(License newLicense) throws LicenseException {
        newLicense.validate();
        this.license = newLicense;
    }

    public License getLicense() {
        return license;
    }

    public void installLicense() {
        Preferences preferences = Preferences.userNodeForPackage(LicenseManager.class);
        String licenseText = new LicenseTextGenerator(license).generateLicenseText();
        preferences.put(license.getProduct().toString(), licenseText);
        notifyListeners();
    }

    // TODO this is for debug only
    public void forceInstallLicense(License anyInvalidLicense) {
        Preferences preferences = Preferences.userNodeForPackage(LicenseManager.class);
        String licenseText = new LicenseTextGenerator(anyInvalidLicense).generateLicenseText();
        preferences.put(anyInvalidLicense.getProduct().toString(), licenseText);
        this.license = anyInvalidLicense;
        notifyListeners();
    }

    private void notifyListeners() {
        for (LicenseChangeListener listener : listeners) {
            listener.licenseInfoUpdated();
        }
    }

    /**
     * @return NULL if the license is not found or is invalid
     * @throws LicenseValidationException the license does not exist or is invalid
     */
    private static License getInstalledTaskAdapterLicense() {
        return loadLicense(Product.TASK_ADAPTER_WEB);
    }

    /**
     * @param product Product type
     * @return NULL if the license is not found
     */
    private static License loadLicense(Product product) {
        Preferences preferences = Preferences.userNodeForPackage(LicenseManager.class);
        String licenseText = preferences.get(product.toString(), null);

        License loadedLicense = null;
        try {
            loadedLicense = new LicenseParser().parseLicense(licenseText);
        } catch (LicenseParseException e) {
            System.out.println("can't parse license previously installed on this machine." + e);
        }
        return loadedLicense;
    }

    public boolean isSomeValidLicenseInstalled() {
        if (license != null) {
            try {
                license.validate();
                return true;
            } catch (LicenseException e) {
                // ignore
            }
        }
        return false;
    }

    public boolean isSomeLicenseInstalled() {
        return license != null;
    }

    public boolean isSingleUserLicenseInstalled() {
        return isSomeValidLicenseInstalled() && license.getType().equals(License.Type.SINGLE);
    }

    public void addLicenseChangeListener(LicenseChangeListener listener) {
        listeners.add(listener);
    }

    //------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println(USAGE_TEXT);
            return;
        }

        LicenseManager licenseManager = new LicenseManager();

        printInstalledLicense(licenseManager);

        String command = args[0];

        if (COMMAND_CLEAN.equals(command)) {
            licenseManager.removeTaskAdapterLicenseFromThisComputer();
            System.out.println("TaskAdapter license was removed from this computer.");

        } else {
            try {
                installLicenseFromFile(command);
            } catch (IOException e) {
                System.out.println("Cannot find file: " + command + "\n\n" + USAGE_TEXT);
            }
        }
    }

    private static void printInstalledLicense(LicenseManager licenseManager) {
        System.out.println("Found installed license:\n" + licenseManager.getLicense().toString());
    }

    public void removeTaskAdapterLicenseFromThisComputer() {
        Preferences preferences = Preferences.userNodeForPackage(LicenseManager.class);
        preferences.remove(Product.TASK_ADAPTER_WEB.toString());
        this.license = null;
        notifyListeners();
    }

    private static void installLicenseFromFile(String fileName) throws IOException {
        String licenseFileText = MyIOUtils.loadFile(fileName);
        System.out.println("Loaded file: " + fileName);
        System.out.println("Installing license: " + DASHES + licenseFileText + DASHES);

        LicenseManager licenseManager = new LicenseManager();

        try {
            licenseManager.setNewLicense(licenseFileText);
            licenseManager.installLicense();
            System.out.println("The license was successfully installed to this computer.");

        } catch (LicenseException e) {
            System.out.println("Invalid license file:\n" + fileName);
        }

    }
}
