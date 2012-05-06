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

    public enum Product {
        TASK_ADAPTER
    }

    public static final int TRIAL_TASKS_NUMBER_LIMIT = 10;

    // TODO this is not very secure, but should be OK for the prototype
    static final String PASSWORD = "z823nv_sz84";

    static final String LINE_DELIMITER = "\n";

    static final String PREFIX_PRODUCT = "Product: ";
    static final String PREFIX_REGISTERED_TO = "Registered to: ";
    static final String PREFIX_EMAIL = "Email: ";
    static final String PREFIX_DATE = "Date: ";
    static final String KEY_STR = "-----Key-----" + LINE_DELIMITER;


    public static final String TRIAL_MESSAGE =
            "=== TRIAL limit: will only process UP TO " + TRIAL_TASKS_NUMBER_LIMIT + " tasks ===";

    private static final String USAGE_TEXT =
            "Usage:\n" +
            "  ./licenses [license_file_to_install] [-clean]\n\n" +
            "  [license_file_to_install]     Install license from this file (full or relative path)\n" +
            "  -clean                        Clean all installed licenses from this computer.";


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

        //---FORMAT START-----------
        //0: Product: TASK_ADAPTER
        //1: Registered to: alex
        //2: Email: mail@
        //3: Date: 2010-07-25
        //4: -----Key-----
        //5: 12313123..........
        //---FORMAT END-------------

        String lines[] = licenseText.split("\\r?\\n");

        if (lines.length < 6) {
            throw new LicenseValidationException();
        }

        String productName = lines[LINE_PRODUCT].substring(PREFIX_PRODUCT.length());

        Product product;

        if (productName.equals(Product.TASK_ADAPTER.toString())) {
            product = Product.TASK_ADAPTER;
        } else {
            // we used to support Redmine API as a product with its own license. not anymore.
            throw new RuntimeException("Unknown product: " + productName);
        }

        String customerName = lines[LINE_CUSTOMER_NAME].substring(PREFIX_REGISTERED_TO.length());

        String email = lines[LINE_EMAIL].substring(PREFIX_EMAIL.length());

        String createdOn = lines[LINE_DATE].substring(PREFIX_DATE.length());

        String key = lines[LINE_KEY];

        String decodedBase64Text = new String(Base64.decodeBase64(key.getBytes()));
        String xoredText = chiper(decodedBase64Text, PASSWORD);
        String mergedStr = customerName + email + createdOn;

        License license;

        if (mergedStr.equals(xoredText)) {
            license = new License(product, customerName, email, createdOn, licenseText);
        } else {
            throw new LicenseValidationException();
        }

        return license;
    }

    // secret description:
    // proksorit vse naher i vsego delov :)
    public static String chiper(String text, String key) {
        String result = null;
        byte[] strBuf = text.getBytes();
        byte[] keyBuf = key.getBytes();
        int c = 0;
        int z = keyBuf.length;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(strBuf.length);
        for (byte bS : strBuf) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static List<License> getInstalledLicenses() {
        List<License> licenses = new ArrayList<License>();

        try {
            licenses.add(loadLicense(Product.TASK_ADAPTER));
        } catch (LicenseValidationException e) {
//			e.printStackTrace();
        }

        return licenses;
    }

    public static void installLicense(Product product, String fullLicenseText) {
        Preferences preferences = Preferences.userNodeForPackage(LicenseManager.class);
        preferences.put(product.toString(), fullLicenseText);
        notifyListeners();
    }

    private static void notifyListeners() {
        for (LicenseChangeListener listener : listeners) {
            listener.licenseInfoUpdated();
        }
    }

    /**
     * @param product Product type
     * @return NULL if the license is not found or is invalid
     * @throws LicenseValidationException the license does not exist or is invalid
     */
    private static License loadLicense(Product product) throws LicenseValidationException {
        Preferences preferences = Preferences.userNodeForPackage(LicenseManager.class);
        String licenseText = preferences.get(product.toString(), null);

        return LicenseManager.checkLicense(licenseText);
    }

    /**
     * @return NULL if the license is not found or is invalid
     * @throws LicenseValidationException the license does not exist or is invalid
     */
    public static License getTaskAdapterLicense() throws LicenseValidationException {
        return loadLicense(Product.TASK_ADAPTER);
    }

    public static boolean isTaskAdapterLicenseOk() {
        boolean result;
        try {
            getTaskAdapterLicense();
            result = true;
        } catch (LicenseValidationException e) {
            result = false;
        }
        return result;
    }

    public static void addLicenseChangeListener(LicenseChangeListener listener) {
        listeners.add(listener);
    }

    //------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        printInstalledLicenses();

        if (args.length < 1) {
            System.out.println(USAGE_TEXT);
            return;
        }

        String command = args[0];

        if (COMMAND_CLEAN.equals(command)) {
            removeTaskAdapterLicenseFromThisComputer();
            System.out.println("TaskAdapter license removed from this computer.");

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
        String existingLicenses = "--none";

        if (!licenses.isEmpty()) {
            existingLicenses = "";

            for (License license : licenses) {
                existingLicenses += license.toString() + "\n";
            }
        }

        System.out.println("Found installed licenses:\n" + existingLicenses);
    }

    public static void removeTaskAdapterLicenseFromThisComputer() {
        Preferences preferences = Preferences.userNodeForPackage(LicenseManager.class);
        preferences.remove(Product.TASK_ADAPTER.toString());
        notifyListeners();
    }

    private static void installLicenseFile(String fileName) throws LicenseValidationException {
        try {
            String licenseFileText = MyIOUtils.loadFile(fileName);
            System.out.println("Loaded file: " + fileName);

            License license = LicenseManager.checkLicense(licenseFileText);
            System.out.println("Installing license:" + DASHES + licenseFileText + DASHES);

            installLicense(license.getProduct(), licenseFileText);
            System.out.println("The license is installed to this computer.");

        } catch (FileNotFoundException e) {
            System.out.println("Can't find file: " + fileName + "\n" + USAGE_TEXT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
