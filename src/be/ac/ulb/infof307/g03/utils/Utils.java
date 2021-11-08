package be.ac.ulb.infof307.g03.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utils class called to check validity of strings following our standards.
 */
public class Utils {
    /**
     * Check if all information given while attempting to register are conform to the format.
     * @param username the username to be check
     * @param password the password to be check
     * @param passwordConfirm the password confirmation to be check
     * @param email the email to be check
     * @param firstName the firstname to be check
     * @param lastName the lastname to be check
     * @return String that contains all error messages that occurred.
     */
    public static String checkValidity(String username, String password, String passwordConfirm, String email,
                                       String firstName, String lastName) {

        String logsValidity = checkLogsValidity(username, password, passwordConfirm);
        String logicalValidity = checkLogicalValidity(username, firstName, lastName);
        String emailValidity = checkEmailValidity(email);

        // Return all error messages
        return logsValidity + logicalValidity + emailValidity;
    }

    /**
     * Verify if our fields that need to be compose only from letters are well-formed.
     * @param username String
     * @param firstName String
     * @param lastName String
     * @return Distinct errors message for each parameter; else blank String.
     */
    public static String checkLogicalValidity(String username, String firstName, String lastName) {
        String error = "";
        if (!(Pattern.matches("[a-zA-Z]+", username))) {
            error += "Username must contain only letters.\n";
        }
        if (!(firstName.equals(""))) {
            if (!(Pattern.matches("[a-zA-Z]+", firstName))) {
                error += "First Name must contain only letters.\n";
            }
        }
        if (!(lastName.equals(""))) {
            if (!(Pattern.matches("[a-zA-Z]+", lastName))) {
                error += "Last Name must contain only letters.\n";
            }
        }

        return error;
    }

    /**
     * Verify if required fields are given and if the passwords matches.
     * @param username String
     * @param password String
     * @param passwordConfirm String
     * @return Distinct error messages for required fields empty or passwords mismatch; blank string if no errors.
     */
    public static String checkLogsValidity(String username, String password, String passwordConfirm) {
        // Are required field filed
        String errorMessage = "";
        if (!(username.length() != 0 &&
                password.length() != 0 &&
                passwordConfirm.length() != 0)) {
            errorMessage += "At least one required field is empty.\n";
        }
        // Compare password
        if (!password.equals(passwordConfirm)) {
            errorMessage += "Passwords doesn't match.\n";
        }
        return errorMessage;
    }

    /**
     * Verify that the email format given is well-formed.
     * @param email string that must match an email format
     * @return error message if not well-formed email; blank string else
     */
    public static String checkEmailValidity(String email) {
        String regex = "^[A-Za-z0-9_\\-.]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(email);
        if (email.equals("")) {  // Allow user not to enter an email
            return "";
        } else if (!(matcher.matches())) {
            return "Email not valid.\n";
        }

        return "";
    }

    /**
     * Function used to initiate null values of our db to blank values in our class constructors.
     * @param s String
     * @return blank string if s is null; else s
     */
    public static String blankIfNull(String s) { return s == null ? "" : s; }


    /**
     * Convert a map into a string
     * @param map the map to convert
     * @return string containing map information (key = value)
     */
    public static String convertWithStream(Map<String, String> map) {
        return map.keySet().stream()
                .map(key -> key + " = " + map.get(key))
                .collect(Collectors.joining("\n\t", "\n", "\n"));
    }
}

