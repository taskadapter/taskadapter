package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * @author Alexey Skorokhodov
 */
public class LicenseManager {

    public static final String LICENSE_DATE_FORMAT = "yyyy-MM-dd";

    private static Collection<LicenseChangeListener> listeners = new HashSet<LicenseChangeListener>();

    public enum PRODUCT {
        TASK_ADAPTER
    }

    public static final int TRIAL_TASKS_NUMBER_LIMIT = 10;

    // TODO this is not very secure, but should be OK for the prototype
    private static final String PASSWORD = "z823nv_sz84";
    private static final String LINE_DELIMITER = "\n";
    private static final String KEY_STR = "-----Key-----" + LINE_DELIMITER;

    private static final String PREFIX_DATE = "Date: ";
    private static final String PREFIX_EMAIL = "Email: ";
    private static final String PREFIX_REGISTERED_TO = "Registered to: ";
    private static final String PREFIX_PRODUCT = "Product: ";

    public static final String TRIAL_MESSAGE = "=== TRIAL limit: will only process UP TO " + LicenseManager.TRIAL_TASKS_NUMBER_LIMIT +
            " tasks ===";

    private static final String INVALID_ARGS_TEXT = "Usage:"
            + "\n./licenses [license_file_to_install] [-clean]"
            + "\n\n[license_file_to_install]     Install license from this file (full or relative path)"
            + "\n-clean                        Clean all installed licenses from this computer.";
    private static final String DASHES = "\n---------------\n";
    private static final String COMMAND_CLEAN = "-clean";

    private static final int LINE_PRODUCT = 0;
    private static final int LINE_CUSTOMER_NAME = 1;

    private static final int LINE_EMAIL = 2;

    private static final int LINE_DATE = 3;

    private static final int LINE_KEY = 5;

    /**
     * @param licenseText
     * @return the valid License object
     * @throws LicenseValidationException
     */
    public static License checkLicense(String licenseText) throws LicenseValidationException {
        if (licenseText == null) {
            throw new LicenseValidationException();
        }

        // ---format:
        // Product: TASK_ADAPTER
        // Registered to: alex
        // Email: mail@
        // Date: 2010.07.25
        // -----Key-----
        // 12313123..........
//		try {

        String lines[] = licenseText.split("\\r?\\n");
        if (lines.length < 6) {
            throw new LicenseValidationException();
        }
        String productLine = lines[LINE_PRODUCT];
        String productName = productLine.substring(PREFIX_PRODUCT
                .length());
        PRODUCT product;
        if (productName.equals(PRODUCT.TASK_ADAPTER.toString())) {
            product = PRODUCT.TASK_ADAPTER;
        } else {
            // we used to support Redmine API as a product with its own license. not anymore.
            throw new RuntimeException("Unknown product: " + productName);
        }
        String nameLine = lines[LINE_CUSTOMER_NAME];
        String customerName = nameLine.substring(PREFIX_REGISTERED_TO
                .length());

        String emailLine = lines[LINE_EMAIL];
        String email = emailLine.substring(PREFIX_EMAIL.length());

        String dateLine = lines[LINE_DATE];
        String createdOn = dateLine.substring(PREFIX_DATE.length());

        // we ignore line number 4

        String key = lines[LINE_KEY];
        // int i = licenseText.indexOf(KEY_STR);
        // String partBeforeKey = licenseText.substring(0, i - 1);

        // String partKey = licenseText.substring(i + KEY_STR.length());
        // System.out.println("--" + partBeforeKey);
        // System.out.println("--" + partKey);
        String decodedBase64Text = new String(Base64.decodeBase64(key
                .getBytes()));
        String xoredText = chiper(decodedBase64Text, PASSWORD);
        String mergedStr = customerName + email + createdOn;
        License license;
        if (mergedStr.equals(xoredText)) {
            // valid license
//				StringTokenizer tok = new StringTokenizer(partBeforeKey,
//						LINE_DELIMITER);
            license = new License(product, customerName, email, createdOn,
                    licenseText);
        } else {
            throw new LicenseValidationException();
        }
        // } catch (Exception e) {
        // throw new LicenseValidationException();
        // }
        return license;
    }

    // secret description:
    // proksorit vse naher i vsego delov
    public static String chiper(String text, String key) {
        String result = null;
        byte[] strBuf = text.getBytes();
        byte[] keyBuf = key.getBytes();
        int c = 0;
        int z = keyBuf.length;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(strBuf.length);
        for (int i = 0; i < strBuf.length; i++) {
            byte bS = strBuf[i];
            byte bK = keyBuf[c];
            byte bO = (byte) (bS ^ bK);
            if (c < z - 1) {
                c++;
            } else {
                c = 0;
            }
            baos.write(bO);
        }
        try {
            baos.flush();
            result = baos.toString();
            baos.close();
            baos = null;
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
        return result;
    }

    private static List<License> getInstalledLicenses() {
        List<License> licenses = new ArrayList<License>();
        License licenseTA;
        try {
            licenseTA = loadLicense(PRODUCT.TASK_ADAPTER);
            licenses.add(licenseTA);
        } catch (LicenseValidationException e) {
//			e.printStackTrace();
        }
        return licenses;
    }

    public static void main(String[] args) {
        // System.out.println("LicenseManager argument(s): " +
        // Arrays.toString(args));

        printInstalledLicenses();

        if (args.length < 1) {
            System.out.println(INVALID_ARGS_TEXT);
            return;
        }
        String command = args[0];
        if (command.equals(COMMAND_CLEAN)) {
            removeTaskAdapterLicenseFromThisComputer();
            System.out.println("Deleted Task Adapter license from this computer.");
        } else {
            try {
                installLicenseFile(command);
            } catch (LicenseValidationException e) {
                System.out.println("Invalid license file:\n" + command);
            }
        }
    }

    private static void printInstalledLicenses() {
        List<License> licenses = getInstalledLicenses();
        String existingLicensesText = "";
        for (License l : licenses) {
            existingLicensesText += l.toString() + "\n";
        }
        if (licenses.isEmpty()) {
            existingLicensesText = "--none";
        }
        System.out.println("Found installed licenses:\n" + existingLicensesText
                + "\n");
    }

    private static void installLicenseFile(String fileName) throws LicenseValidationException {
        try {
            String licenseFileText = MyIOUtils.loadFile(fileName);
            System.out.println("Loaded file: " + fileName);
            License license = LicenseManager.checkLicense(licenseFileText);
            System.out.println("Installing license:" + DASHES + licenseFileText
                    + DASHES);
            installLicense(license.getProduct(), licenseFileText);
            System.out.println("The license is installed to this computer.");
        } catch (FileNotFoundException e) {
            System.out.println("Can't find file: " + fileName + "\n"
                    + INVALID_ARGS_TEXT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void installLicense(PRODUCT product, String fullLicenseText) {
        Preferences prefs = Preferences
                .userNodeForPackage(LicenseManager.class);
        prefs.put(product.toString(), fullLicenseText);
        notifyListeners();
    }

    private static void notifyListeners() {
        for (LicenseChangeListener listener : listeners) {
            listener.licenseInfoUpdated();
        }
    }

    public static void removeTaskAdapterLicenseFromThisComputer() {
        Preferences prefs = Preferences.userNodeForPackage(LicenseManager.class);
        prefs.remove(PRODUCT.TASK_ADAPTER.toString());
        notifyListeners();
    }

    /**
     * @return NULL if the license is not found or is invalid
     * @throws LicenseValidationException the license does not exist or is invalid
     */
    private static License loadLicense(PRODUCT product) throws LicenseValidationException {
        Preferences prefs = Preferences
                .userNodeForPackage(LicenseManager.class);
        String licenseText = prefs.get(product.toString(), null);
        return LicenseManager.checkLicense(licenseText);
    }

    /**
     * @return NULL if the license is not found or is invalid
     * @throws LicenseValidationException the license does not exist or is invalid
     */
    public static License getTaskAdapterLicense() throws LicenseValidationException {
        return loadLicense(PRODUCT.TASK_ADAPTER);
    }

    public static boolean isTaskAdapterLicenseOK() {
        boolean result = false;
        try {
            getTaskAdapterLicense();
            result = true;
        } catch (LicenseValidationException e) {
            // ignore.
        }
        return result;
    }

    public static void addLicenseChangeListener(LicenseChangeListener listener) {
        listeners.add(listener);
    }
}
