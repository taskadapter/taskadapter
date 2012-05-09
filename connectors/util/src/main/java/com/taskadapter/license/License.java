package com.taskadapter.license;

import com.taskadapter.license.LicenseManager.Product;

import java.util.HashMap;
import java.util.Map;

public class License {
    private Product product;
    private Type type;
    private String customerName;
    private String email;
    private String createdOn;
    private String completeText;

    private static final String SINGLE_TEXT = "local / single user";
    private static final String MULTI_TEXT = "server / many users";

    public enum Type {
        SINGLE("s", SINGLE_TEXT),
        MULTI("m", MULTI_TEXT);

        private String code;
        private String text;
        private static Map<String, Type> lookupCode = new HashMap<String, Type>(2){{
            put("s", SINGLE);
            put("m", MULTI);
        }};
        private static Map<String, Type> lookupText = new HashMap<String, Type>(2){{
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

    public String getCompleteText() {
        return completeText;
    }

    public License(Product product, Type type, String customerName, String email, String createdOn, String completeText) {
        super();
        this.product = product;
        this.type = type;
        this.customerName = customerName;
        this.email = email;
        this.createdOn = createdOn;
        this.completeText = completeText;
    }

    @Override
    public String toString() {
        return "License{" +
                "product='" + product + '\'' +
                ", license type='" + type.getText() + '\'' +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", createdOn='" + createdOn +
                "'}";
    }
}
