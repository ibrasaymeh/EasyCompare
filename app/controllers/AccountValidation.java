package controllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.mvc.Controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;


public class AccountValidation extends Controller {

    
/*
 * Validates that the user input is a valid email, through the use of a regular expression.
 */
    public static boolean isValidEmail(String email){
        if ((email == null) || (email.length()>100) || (email.isEmpty())) {
            return false;
        }

        final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();        

    }

// Validates that the user's passwords are valid and equivalent.    
    public static boolean isValidPasswords(String password, String repassword){
        if ((password == null) || (repassword == null) || (!password.equals(repassword)) || (password.isEmpty()) || (password.length()>30)) {
            return false;
        }
        return true;
    }

//When a user attempts to login, this method will validate that the provided email/password combination does exist
    
public static boolean validAccountCredentials(String email, String password) {

        try {
            DBCollection collection = Main.getCollection("accounts");
            BasicDBObject query = new BasicDBObject();
            query.put("email", email);
            BasicDBObject dbObj = (BasicDBObject) collection.findOne(query);

            if ((dbObj != null) && dbObj.getString("password").equals(password)){
                return true;           
            }
            else {
                return false;
            }        
        }
        catch (Exception e) {
            return false;
        }
    }



}
