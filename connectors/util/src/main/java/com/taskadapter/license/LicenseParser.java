package com.taskadapter.license;

import org.apache.commons.codec.binary.Base64;

import static com.taskadapter.license.LicenseFormatDescriptor.*;

final class LicenseParser {

    /**
     * @param licenseText license as text
     * @return the valid License object
     * @throws LicenseValidationException if license is not valid
     */
    public License checkLicense(String licenseText) throws LicenseValidationException {
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
        String expiresOn = lines[LINE_EXPIRES_ON_DATE].substring(PREFIX_EXPIRES_ON.length());
        String key = lines[LINE_KEY];

        String decodedBase64Text = new String(Base64.decodeBase64(key.getBytes()));
        String xoredText = new LicenseEncryptor(PASSWORD).chiper(decodedBase64Text);
        String mergedStr = licenseTypeStr + customerName + email + createdOn;

        License license;

        if (mergedStr.equals(xoredText)) {
            license = new License(product, License.Type.getByText(licenseTypeStr), customerName, email, createdOn, expiresOn, licenseText);
        } else {
            throw new LicenseValidationException();
        }

        return license;
    }

}
