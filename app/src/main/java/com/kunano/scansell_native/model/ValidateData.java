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

}
