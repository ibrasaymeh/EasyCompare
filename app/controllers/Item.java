package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import models.ListAction;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.comparison;
import views.html.coupons;
import views.html.description;
import views.html.errorPage;
import views.html.favorites;
import views.html.login;
import views.html.myTags;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;


public class Item extends Controller {

    public static String opname = "";    
    public static ArrayList<String> para = null;
    public static Form<ListAction> submitForm = Form.form(ListAction.class);


    /*
     * Fetches the product description from the DB and renders it for the web app.
     */
    @SuppressWarnings("unchecked")
    public static Result description(String id) throws Exception {

        Result valSession = valSession();
        if (valSession != null) {
            return valSession;
        }

        DBCollection collection = Main.getCollection("products");        
        BasicDBObject query = new BasicDBObject();
        query.put("_id",id);
        BasicDBObject dbObj = (BasicDBObject) collection.findOne(query);        

        HashMap<String,String> m = (HashMap<String, String>) dbObj.toMap(); 

        return ok(description.render(m));

    }


    /*
     * Fetches the product tags list from the DB and renders it.
     */
    @SuppressWarnings("unchecked")
    public static Result myTags() throws Exception {

        Result valSession = valSession();
        if (valSession != null) {
            return valSession;
        }
        String email = AccountMain.emailAddr;

        DBCollection collection = Main.getCollection("lists");
        BasicDBObject query = new BasicDBObject();
        query.put("email", email);
        BasicDBObject dbObj = (BasicDBObject) collection.findOne(query);        
        ArrayList<String> item = (ArrayList<String>) dbObj.get("all");        
        Collections.reverse(item);
        collection = Main.getCollection("products");      

        ArrayList<HashMap<String,String>> items = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<item.size();i++){
            query = new BasicDBObject();
            query.put("_id",item.get(i));
            dbObj = (BasicDBObject) collection.findOne(query);
            HashMap<String,String> m = (HashMap<String, String>) dbObj.toMap(); 
            items.add(m);
        }


            return ok(myTags.render(items));
    }

    /*
     * Fetches the favorites tags list from the DB and renders it.
     */

    @SuppressWarnings("unchecked")
    public static Result favorites() throws Exception {

        Result valSession = valSession();
        if (valSession != null) {
            return valSession;
        }
        String email = AccountMain.emailAddr;

        DBCollection collection = Main.getCollection("lists");
        BasicDBObject query = new BasicDBObject();
        query.put("email", email);
        BasicDBObject dbObj = (BasicDBObject) collection.findOne(query);        
        ArrayList<String> item = (ArrayList<String>) dbObj.get("fav");        
        Collections.reverse(item);

        collection = Main.getCollection("products");      

        ArrayList<HashMap<String,String>> items = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<item.size();i++){
            query = new BasicDBObject();
            query.put("_id",item.get(i));
            dbObj = (BasicDBObject) collection.findOne(query);
            HashMap<String,String> m = (HashMap<String, String>) dbObj.toMap(); 
            items.add(m);
        }

            return ok(favorites.render(items));

    }

    /*
     * Fetches the coupons tags list from the DB and renders it.
     */

    @SuppressWarnings("unchecked")
    public static Result coupons() throws Exception {

        Result valSession = valSession();
        if (valSession != null) {
            return valSession;
        }
        String email = AccountMain.emailAddr;

        DBCollection collection = Main.getCollection("lists");
        BasicDBObject query = new BasicDBObject();
        query.put("email", email);
        BasicDBObject dbObj = (BasicDBObject) collection.findOne(query);        
        ArrayList<String> item = (ArrayList<String>) dbObj.get("cpn");        
        Collections.reverse(item);

        collection = Main.getCollection("coupons");      

        ArrayList<HashMap<String,String>> items = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<item.size();i++){
            query = new BasicDBObject();
            query.put("_id",item.get(i));
            dbObj = (BasicDBObject) collection.findOne(query);
            HashMap<String,String> m = (HashMap<String, String>) dbObj.toMap(); 
            items.add(m);
        }


            return ok(coupons.render(items));

    }

    /*
     * Renders the action for the user, based on the selected item(s).
     * Handles 5 operations: Delete, Delete coupon, Remove from favorites, Add to favorites, and Compare.
     */

    @SuppressWarnings("unchecked")
    public static Result doAction() throws Exception {

        Result valSession = valSession();
        if (valSession != null) {
            return valSession;
        }
        String email = AccountMain.emailAddr;

        DBCollection collection;
        BasicDBObject query = new BasicDBObject();


            Form<ListAction> form = submitForm.bindFromRequest();
            ListAction data = form.get();

            if (data.getSubmit().equalsIgnoreCase("Delete")){

        		// OLD CODE WHERE TAGS WERE ABLE TO BE DELETED... i have disabled it so that the publicly available account can't have its tags deleted
            	/*
                collection = Main.getCollection("lists");

                query.put("email", email);

                for (String del: data.getCheckBox()){
                    if (del != null) {
                        BasicDBObject replace = new BasicDBObject("all",del);
                        collection.update(collection.findOne(query), new BasicDBObject("$pull", replace));
                    }
                }
                */

                return redirect("/myTags");
            }
            else if (data.getSubmit().equalsIgnoreCase("Delete Coupon")){

        		// OLD CODE WHERE COUPONS WERE ABLE TO BE DELETED... i have disabled it so that the publicly available account can't have its COUPONS deleted
            	/*
                collection = Main.getCollection("lists");

                query.put("email", email);

                for (String del: data.getCheckBox()){
                    if (del != null) {
                        BasicDBObject replace = new BasicDBObject("cpn",del);
                        collection.update(collection.findOne(query), new BasicDBObject("$pull", replace));
                    }
                }
                */

                return redirect("/coupons");
            }
            else if (data.getSubmit().equalsIgnoreCase("Remove from Favorites")){

                collection = Main.getCollection("lists");

                query.put("email", email);

                for (String del: data.getCheckBox()){
                    if (del != null) {
                        BasicDBObject replace = new BasicDBObject("fav",del);
                        collection.update(collection.findOne(query), new BasicDBObject("$pull", replace));
                    }
                }

                return redirect("/favorites");
            }
            else if (data.getSubmit().equalsIgnoreCase("Add to Favorites")){

                collection = Main.getCollection("lists");            
                query.put("email", email);
                BasicDBObject dbObj = (BasicDBObject) collection.findOne(query);        
                ArrayList<String> item = (ArrayList<String>) dbObj.get("fav");   

                for (String del: data.getCheckBox()){
                    if (del != null) {                    
                        if(!item.contains(del)){
                            BasicDBObject append = new BasicDBObject("fav",del);
                            collection.update(collection.findOne(query), new BasicDBObject("$push", append));

                        }
                    }
                }

                return redirect("/favorites");
            } else { //if (data.getSubmit().equalsIgnoreCase("Compare")){               MUST BE THIS CASE

                collection = Main.getCollection("products");
                ArrayList<String> item = new ArrayList<String>();
                BasicDBObject dbObj;
                ArrayList<HashMap<String,String>> items = new ArrayList<HashMap<String,String>>();

                for (String del: data.getCheckBox()){
                    if (del != null) {
                        item.add(del);

                    }
                }

                String separator = "X";
                String itemIdentifier = StringUtils.substringBefore(item.get(0), separator);
                for (String str : item){
                    if(!itemIdentifier.equals(StringUtils.substringBefore(str, separator))){
                        return ok(errorPage.render("Invalid comparison. The items must be related."));
                    }
                }


                for(int i=0;i<item.size();i++){
                    query = new BasicDBObject();
                    query.put("_id",item.get(i));
                    dbObj = (BasicDBObject) collection.findOne(query);
                    HashMap<String,String> m = (HashMap<String, String>) dbObj.toMap(); 
                    items.add(m);
                }

                return ok(comparison.render(items));
            }

        

    }

/*
 * Filters the provided item ID (from an NFC tag, via the mobile device) and adds it to the appropriate list,
 * either coupon or products.
 * Returns an error message where appropriate.
 */
    @SuppressWarnings("unchecked")
    public static Result addItem() throws Exception {


        //check first char to determine if it's a coupon
        JsonNode json = request().body().asJson();
        if(json == null) {
            return badRequest("Expecting Json data");
        }

        String email = json.findValue("email").asText();
        String password = json.findValue("password").asText();
        String id = json.findValue("id").asText();

        if(!AccountValidation.isValidEmail(email) || !AccountValidation.isValidPasswords(password, password)) {
            JSONObject msg = new JSONObject();
            msg.put("0", "Invalid Credentials");                
            return ok(Json.toJson(msg.toString()));
        }
        else{
            if(!AccountValidation.validAccountCredentials(email, password)) {
                JSONObject msg = new JSONObject();
                msg.put("0", "Invalid Credentials");                    
                return ok(Json.toJson(msg.toString()));
            }
        }

        DBCollection collection = Main.getCollection("lists");        
        BasicDBObject query = new BasicDBObject();
        query.put("email", email);
        BasicDBObject dbObj = (BasicDBObject) collection.findOne(query);        

        String list = "";
        if (id.charAt(0) == 'C') {
            list = "cpn";
        }
        else {
            list = "all";
        }

        ArrayList<String> item = (ArrayList<String>) dbObj.get(list);   

        if(!item.contains(id)){
            BasicDBObject append = new BasicDBObject(list,id);
            collection.update(collection.findOne(query), new BasicDBObject("$push", append));

        }

        JSONObject msg = new JSONObject();
        if (id.charAt(0) == 'C') {
            msg.put("1", "Coupon Successfully Added");  
        }
        else {
            msg.put("1", "Product Successfully Added");  
        }

        return ok(Json.toJson(msg.toString()));

    }


/*
 * Validates that the user is currently logged in (active session)
 */
    public static Result valSession() throws Exception {

            AccountMain.emailAddr = session("email");
            if (AccountMain.emailAddr == null) {
                return ok(login.render("You must sign in to continue."));
            }


        return null;       

    }
    

}
