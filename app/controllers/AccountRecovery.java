package controllers;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import models.Account;

import org.json.JSONObject;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.recover;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;


public class AccountRecovery extends Controller {

    
/*
 * Responsible for the 'Forgot password' form.
 * Invokes a new thread to send an email to the user, containing their password.
 */
    public static void sendEmail(String destEmail, String body){

//    	Credentials have been removed so that new accounts can't have their inboxes spammed via the public address
        final String email = "easy.compare.2014@gmail.com";
        final String password = "";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destEmail));
            message.setSubject("Your account password");
            message.setContent(body, "text/html");

            (new Thread() {

                private Message message;

                public Thread init(Message message) {
                    this.message = message;
                    return this;
                }

                @Override
                public void run() {
                    try {
                        Transport.send(message);
                        System.out.println("Email has been sent.");
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }).init(message).start();

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

/*
 * Renders the 'forgot password' form, and returns an error message when appropriate.
 * Otherwise, sends a request to the sendEmail method to retrieve the users password.
 */
    public static Result recover() throws Exception {

        String email = "";

            Form<Account> form = AccountMain.userForm.bindFromRequest();
            Account data = form.get();

            email = data.getEmail();
            if (!AccountValidation.isValidEmail(email)) {
                return ok(recover.render("Invalid email address. Please try again.", ""));
            }

            // same in both, START1
            DBCollection collection = Main.getCollection("accounts");

            BasicDBObject query = new BasicDBObject();
            query.put("email", email);
            BasicDBObject dbObj = (BasicDBObject) collection.findOne(query);

            if (dbObj == null) {
                // same in both, END1
                return ok(recover.render("The email address provided does not match any account.", ""));
            }


            // same in both, START3
            String rPassword = dbObj.getString("password");
            String content = "** This is an automated message -- please do not reply as you will not receive a response **<br><br>Hello,<br><br>This email has been sent in response to your recent request.<br><br><b><font color=blue>Your password is: " + rPassword + "</font></b><br><br>To change your password, please visit the Settings page at: <b>www.easy-compare.net</b><br><br>Easy Compare";
            
            
    		// i have disabled it so that new accounts can't have their inboxes spammed via the public address
            /*
            sendEmail(email, content);
            return ok(recover.render("", "Successfully received your request. Please check your email."));  
            */
            return ok(recover.render("", "Error: Please try again later."));  

            
        

    }

//Renders the initial 'forgot password' page.
    public static Result getRecover() throws Exception {

        return ok(recover.render("", ""));
    }


}
