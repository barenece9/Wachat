package com.wachat.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
    /**
     * Check if the email address is in a valid email format
     *
     * @param emailAddress
     * @return
     */
    public static boolean isValidEmailAddress(String emailAddress) {
        String emailRegEx, emailRegExCOX, emailRegExXCOX, emailRegExXCOXX, emailRegExXXCOXX;
        Pattern pattern, patternCOX, patternXCOX, patternXCOXX, patternXXCOX;
        boolean isValid = false;
        // Regex for a valid email address
        emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
        emailRegExCOX = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$+\\.[A-Za-z]{2,4}$";
        emailRegExXCOX = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$+\\.[A-Za-z]{2,4}$";
        emailRegExXCOXX = "^[A-Za-z0-9._%+\\-]+^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$+\\.[A-Za-z]{2,4}$ +\\.[A-Za-z]{2,4}$+\\.[A-Za-z]{2,4}$";
        emailRegExXXCOXX = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$+\\.[A-Za-z]{2,4}$ +\\.[A-Za-z]{2,4}$+\\.[A-Za-z]{2,4}$";
        // Compare the regex with the email address
        pattern = Pattern.compile(emailRegEx);
        patternCOX = Pattern.compile(emailRegExCOX);
        patternXCOX = Pattern.compile(emailRegExXCOX);
        patternXCOXX = Pattern.compile(emailRegExXCOXX);
        patternXXCOX = Pattern.compile(emailRegExXXCOXX);

        Matcher matcher = pattern.matcher(emailAddress);
        Matcher matcherCOX = patternCOX.matcher(emailAddress);
        Matcher matcherXCOX = patternXCOX.matcher(emailAddress);
        Matcher matcherXCOXX = patternXCOXX.matcher(emailAddress);
        Matcher matcherXXCOXX = patternXXCOX.matcher(emailAddress);

        if (matcher.find()) {

            isValid = true;
        }
        if (matcherCOX.find() || matcherXCOX.find()) {
            isValid = true;
        }
        if (matcherXCOXX.find() || matcherXXCOXX.find()) {
            isValid = true;
        }

        return isValid;
    }

    /**
     * <h1>This method returns true if a given email address is valid</h1>
     *
     * @param emailAddress
     * @return true if email address is valid
     */
    public final static boolean isValidEmail(CharSequence emailAddress) {
        if (emailAddress == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress)
                .matches();
    }

    /**
     * <h1>This method returns true if a given phone number is valid</h1>
     *
     * @param phoneNumber
     * @return true if phone number is valid
     */
//	public final static boolean isValidPhoneNumber(String phoneNumber) {
//		if (phoneNumber == null)
//			return false;
//
//		return android.util.Patterns.PHONE.matcher(phoneNumber).matches();
//	}

    /**
     * Check if the phone number is a valid number
     * <p/>
     * //	 * @param phNo
     *
     * @return
     */
    public static boolean isValidPhoneNumber(String phNo) {
        String phNoRegEx;
        Pattern pattern;
        // Regex for a valid email address
        phNoRegEx = "^[0-9]{5,16}$";
        // Compare the regex with the email address
        pattern = Pattern.compile(phNoRegEx);
        Matcher matcher = pattern.matcher(phNo);
        return matcher.find();
    }

    /**
     * Check if the phone number is a valid number
     * <p/>
     * //	 * @param phNo
     *
     * @return
     */
    public static boolean containOnlyZero(String phNo) {
        String phNoRegEx;
        Pattern pattern;
        // Regex for a valid email address
        phNoRegEx = "^[0]{5,15}$";
        // Compare the regex with the email address
        pattern = Pattern.compile(phNoRegEx);
        Matcher matcher = pattern.matcher(phNo);
        return matcher.find();
    }

    public static boolean isValidAadharNo(String aadharNo) {
        return TextUtils.isDigitsOnly(aadharNo) && (aadharNo.length() == 12);
    }

    public static boolean isAlphaNumericAndSpecialCharPassword(String new_pass) {
        if (TextUtils.isEmpty(new_pass)) {
            return false;
        }

        String n = ".*[0-9].*";
        String a = ".*[a-zA-Z].*";
        boolean hasSpecialChar = false;
        try {
            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(new_pass);
            hasSpecialChar = m.find();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new_pass.matches(n) && new_pass.matches(a) && hasSpecialChar;
    }

    public static boolean containsXmppDomainName(String chatId){

        return chatId.contains("@") && chatId.endsWith(".com");

        //        String xmppDomainRegEx;
//        Pattern pattern;
//        xmppDomainRegEx = "@[A-Za-z]+\\.com$";
//        // Compare the regex with the email address
//        pattern = Pattern.compile(chatId);
//        Matcher matcher = pattern.matcher(chatId);
//        if (!matcher.find()) {
//            return false;
//        }
//        return true;

    }
}
