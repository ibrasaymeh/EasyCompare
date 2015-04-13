package controllers;

import java.util.ArrayList;

import views.html.register;
import models.Account;

import org.json.JSONObject;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.accountSettings;
import views.html.login;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import java.util.ArrayList;

import models.Account;

import org.json.JSONObject;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;


public class AccountMain extends Controller {


	public static Form<Account> userForm = Form.form(Account.class);
	public static String emailAddr = "";


	/*
	 * Renders the login page in the web app.
	 * Returns: If previously logged in, returns the myTags page
	 *          Else, returns login page (if browser != chrome, displays a warning message)
	 */

	public static Result getLogin() {
		String email = session("email");
		if (email == null) {
			String msg = "";
			String userAgent = request().getHeader("User-Agent");
			if (!userAgent.toLowerCase().contains("chrome")) {
				msg = "Warning: Your browser is not supported. As a result, some features may not function properly. This site is optimized for the Google Chrome desktop browser.";
			}
			return ok(login.render(msg));
		}
		return redirect("/myTags");   

	}


	/*
	 * Logs into web app using the credentials provided.
	 * Returns: If valid credentials, logs user into web app and redirects to the myTags page.
	 *          Else, displays an error message.
	 */
	public static Result login() throws Exception {

		String email = "";
		String password = "";

		if (request().accepts("text/html")) { 

			Form<Account> form = userForm.bindFromRequest();
			Account data = form.get();

			email = data.getEmail();
			password = data.getPassword();

			if(AccountValidation.isValidEmail(email) && AccountValidation.isValidPasswords(password, password)) {
				if(AccountValidation.validAccountCredentials(email,password)) {                
					session().clear();
					session("email", email);
					return redirect("/myTags");           
				}
			}
			return ok(login.render("Invalid Email or Password. Please try again."));
		}
		else {

			JsonNode json = request().body().asJson();
			if(json == null) {
				return badRequest("Expecting Json data");
			}

			email = json.findValue("email").asText();
			password = json.findValue("password").asText();

			JSONObject msg = new JSONObject();
			if(AccountValidation.isValidEmail(email) && AccountValidation.isValidPasswords(password, password)) {
				if(AccountValidation.validAccountCredentials(email,password)) {       
					msg.put("1", "Successful Login");
					return ok(Json.toJson(msg.toString()));
				}
			}
			msg.put("0", "Invalid Credentials");                
			return ok(Json.toJson(msg.toString()));

		}
	}


	// Logs the user out of the web app, and clears the session
	public static Result logout() {

		String email = session("email");
		if (email == null) {
			return ok(login.render(""));            
		}
		session().clear();
		return ok(login.render("You have successfully signed out."));
	}


	/*
	 * Renders the account settings page.
	 * Passes email to template (pass by value) for rendering.
	 */

	public static Result getAccountSettings() throws Exception {

        Result valSession = Item.valSession();
        if (valSession != null) {
            return valSession;
        }
        String email = AccountMain.emailAddr;
		return ok(accountSettings.render(email, "", ""));

	}

	/*
	 * Changes the user's password, via the account settings page.
	 * Requires that the two passwords match, and verifies it.
	 * Displays an error message when appropriate, else: changes the password.
	 */
	public static Result accountSettings() throws Exception {

		String newPassword = "";
		String newRePassword = "";
        Result valSession = Item.valSession();
        if (valSession != null) {
            return valSession;
        }
        String email = AccountMain.emailAddr;

		return ok(accountSettings.render(email, "Your password was not changed. Please try again.", ""));

		
		// OLD CODE WHERE PASSWORDS WERE ABLE TO GET CHANGED... i have disabled it so that the publicly available account can't have its password changed
/*
		Form<Account> form = userForm.bindFromRequest();
		Account data = form.get();

		newPassword = data.getPassword();
		newRePassword =   data.getRepassword();

		if (!AccountValidation.isValidPasswords(newPassword, newRePassword)) {
			return ok(accountSettings.render(email, "Your password was not changed. Please try again.", ""));
		}


		DBCollection collection = Main.getCollection("accounts");

		BasicDBObject query = new BasicDBObject();
		query.put("email", email);
		BasicDBObject dbObj = (BasicDBObject) collection.findOne(query);

		BasicDBObject replace = new BasicDBObject("password", newPassword);
		collection.update(dbObj, new BasicDBObject("$set", replace));

		return ok(accountSettings.render(email, "", "Your password has been successfully changed."));  
*/
	}

	
	
	
	
    /*registration method
     * Registers an account using the credentials provided by the user.
     * Initializes the tags, favs, and coupons lists to empty.
     */
    public static Result reg() throws Exception {

    	Form<Account> form = userForm.bindFromRequest();
    	Account data = form.get();

    	String email = data.getEmail();
        String password = data.getPassword();
        String repassword = data.getRepassword();
        
		
        if(!AccountValidation.isValidEmail(email) || !AccountValidation.isValidPasswords(password, repassword)) {
			return ok(register.render("Invalid Email or Password.", ""));
        }

        DBCollection collection = Main.getCollection("accounts");

        BasicDBObject query = new BasicDBObject();
        query.put("email", email);
        BasicDBObject dbObj = (BasicDBObject) collection.findOne(query);

        if (dbObj != null){
			return ok(register.render("An account already exists with that email.", ""));
        }

        BasicDBObject document = new BasicDBObject();
        document.put("email", email);
        document.put("password", password);
        collection.insert(document);

        collection = Main.getCollection("lists");
        document = new BasicDBObject();
        ArrayList<String> arr = new ArrayList<String>();
        document.put("email", email);
        document.put("fav", arr);
        document.put("cpn", arr);
        document.put("all", arr);
        collection.insert(document);

		return ok(register.render("", "Your account has been successfully created."));  

    }
    
    



}
