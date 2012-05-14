package com.taskadapter.license;

import com.taskadapter.license.LicenseManager.Product;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.taskadapter.license.LicenseFormatDescriptor.LICENSE_DATE_FORMAT;

public class License {
    private SimpleDateFormat licenseDateFormatter = new SimpleDateFormat(LICENSE_DATE_FORMAT);

    private Product product;
    private Type type;
    private String customerName;
    private String email;
    private String createdOn;
    private Date expiresOn;

    private static final String SINGLE_TEXT = "local / single user";
    private static final String MULTI_TEXT = "server / many users";

    public enum Type {
        SINGLE("s", SINGLE_TEXT),
        MULTI("m", MULTI_TEXT);

        private String code;
        private String text;
        private static Map<String, Type> lookupCode = new HashMap<String, Type>(2) {{
            put("s", SINGLE);
            put("m", MULTI);
        }};
        private static Map<String, Type> lookupText = new HashMap<String, Type>(2) {{
            put(SINGLE_TEXT, SINGLE);
            put(MULTI_TEXT, MULTI);
        }};

        Type(String code, String text) {
            this.code = code;
            this.text = text;
        }

        public String getCode() {
            return code;
        }

        public String getText() {
            return text;
        }

        public static Type getByCode(String code) {
            return lookupCode.get(code);
        }

        public static Type getByText(String str) {
            return lookupText.get(str);
        }
    }

    public Product getProduct() {
        return product;
    }

    public Type getType() {
        return type;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getEmail() {
        return email;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public Date getExpiresOn() {
        return expiresOn;
    }

    // TODO refactor this parameters list!!
    public License(Product product, Type type, String customerName, String email, String createdOn, Date expiresOn) {
        super();
        this.product = product;
        this.type = type;
        this.customerName = customerName;
        this.email = email;
        this.createdOn = createdOn;
        this.expiresOn = expiresOn;
    }

    @Override
    public String toString() {
        return "License{" +
                "product='" + product + '\'' +
                ", license type='" + type.getText() + '\'' +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", createdOn='" + createdOn +
                ", expiresOn='" + expiresOn +
                "'}";
    }

    public void validate() throws LicenseException {
        if (isExpired()) {
            throw new LicenseExpiredException("This license has expired. Today is " + licenseDateFormatter.format(new Date())
                    + " and the license expiration date is " + licenseDateFormatter.format(expiresOn)
                    + " (Date format is " + LICENSE_DATE_FORMAT + ")");
        }
    }

    public boolean isExpired() {
        Calendar expirationDateCalendar = Calendar.getInstance();
        expirationDateCalendar.setTime(expiresOn);
        Calendar now = Calendar.getInstance();
        return now.after(expirationDateCalendar);
    }
}
