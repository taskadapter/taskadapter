package com.taskadapter.license;

class ArgumentsParser {
    private static final String QUOTE_SYMBOL = "\"";

    RequestedLicense parseArgs(String[] args) {
        String customerName = "";
        String email = "";

        int i;
        for (i = 0; i < args.length; i++) {
            if (!args[i].contains("@")) {
                customerName += args[i] + " ";
            } else {
                email = args[i];
                break;
            }
        }
        customerName = customerName.trim();
        customerName = stripQuotesIfNeeded(customerName);
        RequestedLicense requestedLicense = new RequestedLicense(customerName, email);
        if (i < args.length - 1) {
            i++;
            requestedLicense.setUsersNumber(Integer.parseInt(args[i]));
        }
        if (i < args.length - 1) {
            i++;
            requestedLicense.setMonthsValid(Integer.parseInt(args[i]));
        }
        return requestedLicense;
    }

    String stripQuotesIfNeeded(final String customerName) {
        String nameWithoutQuotes = customerName;
        if (nameWithoutQuotes.startsWith(QUOTE_SYMBOL)) {
            nameWithoutQuotes = nameWithoutQuotes.substring(1);
        }
        if (nameWithoutQuotes.endsWith(QUOTE_SYMBOL)) {
            nameWithoutQuotes = nameWithoutQuotes.substring(0, nameWithoutQuotes.length() - QUOTE_SYMBOL.length());
        }
        return nameWithoutQuotes;
    }

}
