package com.rubengees.filetransfer.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
public class Validator {

    private static final String IP_PATTERN =
            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

    public static boolean validateIP(String ip) {
        Pattern pattern = Pattern.compile(IP_PATTERN);
        Matcher matcher = pattern.matcher(ip);

        return matcher.matches();
    }

}
