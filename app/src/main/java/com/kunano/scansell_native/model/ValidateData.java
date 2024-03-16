package com.kunano.scansell_native.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateData {

    public static boolean validateName(String name){
        Pattern pattern = Pattern.compile("^[A-Za-z\\u00C0-\\u017F]{3,30}(?:[\\s][A-Za-z\\u00C0-\\u017F]+)*([\\s]?)$");
        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
    }

    public static boolean validateAddress(String address){
        Pattern pattern = Pattern.compile("^[#.0-9a-zA-Z\\s,-]+$");
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }


    public static boolean validateEmailAddress(String emailAddress){
        Pattern pattern = Pattern.compile( "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*(\\.[a-zA-Z]{2,})$");
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    public static boolean containsDigit(String input) {
        // Regular expression to match any digit
        String regex = ".*\\d.*";

        // Check if input matches the regular expression
        return input.matches(regex);
    }

    public static boolean containsUpperAndLowerCase(String input) {
        // Regular expression to match at least one uppercase and one lowercase letter
        String regex = "(?=.*[A-Z])(?=.*[a-z]).*";

        // Check if input matches the regular expression
        return input.matches(regex);
    }
    public static boolean validatePassword(String input) {
        // Regular expression to match at least one uppercase and one lowercase letter
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";

        // Check if input matches the regular expression
        return input.matches(regex);
    }


}
