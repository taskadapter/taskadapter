package com.taskadapter.license;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class LicenseManager {

    public static enum Product {
        TASK_ADAPTER_WEB
    }

    public static final int TRIAL_TASKS_NUMBER_LIMIT = 10;

    public static final String TRIAL_MESSAGE =
            "=== TRIAL VERSION LIMIT: will only process UP TO " + TRIAL_TASKS_NUMBER_LIMIT + " tasks ===";

    private static final String LICENSE_FILE_NAME = "taskadapter-license.txt";

    private final File dataRootFolder;

    private String licenseText;

    private Collection<LicenseChangeListener> listeners = new HashSet<LicenseChangeListener>();

    private License license;

    public LicenseManager(File dataRootFolder) {
        this.dataRootFolder = dataRootFolder;
    }

    public void setNewLicense(String licenseText) throws LicenseException {
        this.licenseText = licenseText;
        license = new LicenseParser().parseLicense(licenseText);
        license.validate();
    }

    // TODO Add tests to fix what is returned when no license is installed
    public License getLicense() {
        return license;
    }

    public String getLicenseText() {
        return licenseText;
    }

    public void copyLicenseToConfigFolder() {
        File licenseFile = getLicenseFile();
        try {
            Files.write(licenseText, licenseFile, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error writing the license to " + licenseFile.getAbsolutePath() + " The problem is: " + e.toString());
        }
        notifyListeners();
    }

    private File getLicenseFile() {
        return new File(dataRootFolder, LICENSE_FILE_NAME);
    }

    private void notifyListeners() {
        for (LicenseChangeListener listener : listeners) {
            listener.licenseInfoUpdated();
        }
    }

    public void loadInstalledTaskAdapterLicense() {
        this.license = loadLicense();
    }

    /**
     * @return NULL if the license is not found
     */
    private License loadLicense() {
        String licenseText = null;
        try {
            licenseText = Files.toString(getLicenseFile(), Charsets.UTF_8);
        } catch (FileNotFoundException e) {
            // System.out.println("License file not found: " + getLicenseFile().getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Can't load the license file " + getLicenseFile().getAbsolutePath()
                    + ". The problem is: " + e.toString());
        }
        License loadedLicense = null;
        if (licenseText != null) {
            try {
                loadedLicense = new LicenseParser().parseLicense(licenseText);
            } catch (LicenseParseException e) {
                System.out.println("can't parse license previously installed on this machine." + e);
            }
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

    public void addLicenseChangeListener(LicenseChangeListener listener) {
        listeners.add(listener);
    }

    public void removeTaskAdapterLicenseFromConfigFolder() {
        getLicenseFile().delete();
        this.license = null;
        notifyListeners();
    }
}
