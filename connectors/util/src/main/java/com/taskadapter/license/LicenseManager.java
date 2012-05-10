package com.taskadapter.license;

import com.taskadapter.util.MyIOUtils;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.prefs.Preferences;

/**
 * @author Alexey Skorokhodov
 */
public class LicenseManager {
    public enum Product {
        TASK_ADAPTER_WEB
    }

    public static final String LICENSE_DATE_FORMAT = "yyyy-MM-dd";
    public static final int TRIAL_TASKS_NUMBER_LIMIT = 10;

    // TODO this is not very secure, but should be OK for the prototype
    static final String PASSWORD = "$z823nV_sz#84";

    static final String LINE_DELIMITER = "\n";

    static final String PREFIX_PRODUCT = "Product: ";
    static final String PREFIX_LICENSE_TYPE = "License type: ";
    static final String PREFIX_REGISTERED_TO = "Registered to: ";
    static final String PREFIX_EMAIL = "Email: ";
    static final String PREFIX_DATE = "Date: ";
    static final String KEY_STR = "-------------- Key --------------" + LINE_DELIMITER;


    public static final String TRIAL_MESSAGE =
            "=== TRIAL VERSION LIMIT: will only process UP TO " + TRIAL_TASKS_NUMBER_LIMIT + " tasks ===";

    private static final String USAGE_TEXT =
            "Usage:\n" +
            "  ./licenses [license_file_to_install] [-clean]\n\n" +
            "  [license_file_to_install]     Install license from this file (full or relative path)\n" +
            "  -clean                        Clean all installed licenses from this computer.";


    private static final String DASHES = "\n------------------------------\n";
    private static final String COMMAND_CLEAN = "-clean";

    private static final int LINE_PRODUCT = 0;
    private static final int LINE_LICENSE_TYPE = 1;
    private static final int LINE_CUSTOMER_NAME = 2;
    private static final int LINE_EMAIL = 3;
    private static final int LINE_DATE = 4;
    private static final int LINE_KEY = 6;

    private static Collection<LicenseChangeListener> listeners = new HashSet<LicenseChangeListener>();

    private License license;
    private boolean isValid = true;

    public LicenseManager() {
        try {
            license = getTaskAdapterLicense();
        } catch (LicenseValidationException e) {
            isValid = false;
        }
    }

    public LicenseManager(String licenseText) {
        setNewLicense(licenseText);
    }

    public void setNewLicense(String licenseText) {
        try {
            license = checkLicense(licenseText);
        } catch (LicenseValidationException e) {
            isValid = false;
        }
    }

    public License getLicense() {
        return license;
    }

    /**
     * @param licenseText license as text
     * @return the valid License object
     * @throws LicenseValidationException if license is not valid
     */
    private static License checkLicense(String licenseText) throws LicenseValidationException {
        if (licenseText == null) {
            throw new LicenseValidationException();
        }

        //---FORMAT START-----------
        //0: Product: TASK_ADAPTER_WEB
        //1: License type: local / single user
        //2: Registered to: Alexey Skorokhodov
        //3: Email: mail@server.com
        //4: Date: 2010-07-25
        //5: -----Key-----
        //6: 12313123..........
        //---FORMAT END-------------

        String lines[] = licenseText.split("\\r?\\n");

        if (lines.length < 7) {
            throw new LicenseValidationException();
        }

        String productName = lines[LINE_PRODUCT].substring(PREFIX_PRODUCT.length());

        Product product;

        if (productName.equals(Product.TASK_ADAPTER_WEB.toString())) {
            product = Product.TASK_ADAPTER_WEB;
        } else {
            // we used to support Redmine API as a product with its own license. not anymore.
            throw new RuntimeException("Unknown product: " + productName);
        }

        String licenseTypeStr = lines[LINE_LICENSE_TYPE].substring(PREFIX_LICENSE_TYPE.length());
        String customerName = lines[LINE_CUSTOMER_NAME].substring(PREFIX_REGISTERED_TO.length());
        String email = lines[LINE_EMAIL].substring(PREFIX_EMAIL.length());
        String createdOn = lines[LINE_DATE].substring(PREFIX_DATE.length());
        String key = lines[LINE_KEY];

        String decodedBase64Text = new String(Base64.decodeBase64(key.getBytes()));
        String xoredText = chiper(decodedBase64Text, PASSWORD);
        String mergedStr = licenseTypeStr + customerName + email + createdOn;

        License license;

        if (mergedStr.equals(xoredText)) {
            license = new License(product, License.Type.getByText(licenseTypeStr), customerName, email, createdOn, licenseText);
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

    public void installLicense() {
        Preferences preferences = Preferences.userNodeForPackage(LicenseManager.class);
        preferences.put(license.getProduct().toString(), license.getCompleteText());
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
    private static License getTaskAdapterLicense() throws LicenseValidationException {
        return loadLicense(Product.TASK_ADAPTER_WEB);
    }

    /**
     * @param product Product type
     * @return NULL if the license is not found or is invalid
     * @throws LicenseValidationException the license does not exist or is invalid
     */
    private static License loadLicense(Product product) throws LicenseValidationException {
        Preferences preferences = Preferences.userNodeForPackage(LicenseManager.class);
        String licenseText = preferences.get(product.toString(), null);

        return checkLicense(licenseText);
    }

    public boolean isTaskAdapterLicenseOk() {
        return isValid;
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
            installLicenseFromFile(command);
        }
    }

    private static void printInstalledLicense(LicenseManager licenseManager) {
        System.out.println("Found installed license:\n" + licenseManager.getLicense().toString());
    }

    public void removeTaskAdapterLicenseFromThisComputer() {
        Preferences preferences = Preferences.userNodeForPackage(LicenseManager.class);
        preferences.remove(Product.TASK_ADAPTER_WEB.toString());
        notifyListeners();
    }

    private static void installLicenseFromFile(String fileName) {
        try {
            String licenseFileText = MyIOUtils.loadFile(fileName);
            System.out.println("Loaded file: " + fileName);

            System.out.println("Installing license: " + DASHES + licenseFileText + DASHES);

            LicenseManager licenseManager = new LicenseManager(licenseFileText);

            if (licenseManager.isTaskAdapterLicenseOk()) {
                licenseManager.installLicense();
                System.out.println("The license was successfully installed to this computer.");

            } else {
                System.out.println("Invalid license file:\n" + fileName);
            }

        } catch (IOException e) {
            System.out.println("Cannot find file: " + fileName + "\n\n" + USAGE_TEXT);
        }
    }
}
