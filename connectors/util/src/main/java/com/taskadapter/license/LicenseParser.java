package com.taskadapter.license;

import org.apache.commons.codec.binary.Base64;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.taskadapter.license.LicenseFormatDescriptor.*;

final class LicenseParser {

    public static final int LICENSE_BODY_LINES_NUMBER = 7;
    private SimpleDateFormat licenseDateFormatter = new SimpleDateFormat(LICENSE_DATE_FORMAT);

    /**
     * @param licenseText license as text
     * @return the valid License object
     * @throws LicenseValidationException if license is not valid
     */
    public License checkLicense(String licenseText) throws LicenseValidationException {
        if (licenseText == null) {
            throw new LicenseValidationException("license body is NULL");
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

        if (lines.length < LICENSE_BODY_LINES_NUMBER) {
            throw new LicenseValidationException("Please provide the complete license text. License body must have " + LICENSE_BODY_LINES_NUMBER + " lines.");
        }

        String productName = lines[LINE_PRODUCT].substring(PREFIX_PRODUCT.length());

        LicenseManager.Product product;

        if (productName.equals(LicenseManager.Product.TASK_ADAPTER_WEB.toString())) {
            product = LicenseManager.Product.TASK_ADAPTER_WEB;
        } else {
            // we used to support Redmine API as a product with its own license. not anymore.
            throw new RuntimeException("Unknown product: " + productName);
        }

        String licenseTypeStr = lines[LINE_LICENSE_TYPE].substring(PREFIX_LICENSE_TYPE.length());
        String customerName = lines[LINE_CUSTOMER_NAME].substring(PREFIX_REGISTERED_TO.length());
        String email = lines[LINE_EMAIL].substring(PREFIX_EMAIL.length());
        String createdOn = lines[LINE_CREATED_ON_DATE].substring(PREFIX_CREATED_ON.length());
        String expiresOnString = lines[LINE_EXPIRES_ON_DATE].substring(PREFIX_EXPIRES_ON.length());
        String key = lines[LINE_KEY];

        String decodedBase64Text = new String(Base64.decodeBase64(key.getBytes()));
        String xoredText = new LicenseEncryptor(PASSWORD).chiper(decodedBase64Text);
        String mergedStr = licenseTypeStr + customerName + email + createdOn + expiresOnString;

        License license;

        Date expiresOn;
        try {
            expiresOn = licenseDateFormatter.parse(expiresOnString);
        } catch (ParseException e) {
            throw new LicenseValidationException("Invalid license expiration date: " + expiresOnString + ". Valid format: " + LICENSE_DATE_FORMAT);
        }

        Calendar expirationDateCalendar = Calendar.getInstance();
        expirationDateCalendar.setTime(expiresOn);

        if (mergedStr.equals(xoredText)) {
            license = new License(product, License.Type.getByText(licenseTypeStr), customerName, email, createdOn, expiresOnString, licenseText);
        } else {
            throw new LicenseValidationException("License is not recognized");
        }
        Calendar now = Calendar.getInstance();
        if (now.after(expirationDateCalendar)) {
            throw new LicenseExpiredException("This license has expired. Today is " + licenseDateFormatter.format(now.getTime())
                    + " and the license expiration date is " + licenseDateFormatter.format(expiresOn)
                    + " (Date format is " + LICENSE_DATE_FORMAT + ")");
        }

        return license;
    }

}
