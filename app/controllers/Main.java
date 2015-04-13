package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.contact;
import views.html.register;
import views.html.intro;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import controllers.Secure.AccountCredentials;


public class Main extends Controller {

    
//retrieves the 'provided' collection from the database
    public static DBCollection getCollection(String collection) throws Exception{

        String uri= AccountCredentials.getURI();
        MongoClientURI mongoClientURI=new MongoClientURI(uri);        
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        DB db = mongoClient.getDB(mongoClientURI.getDatabase());
        db.authenticate(mongoClientURI.getUsername(), mongoClientURI.getPassword());

        DBCollection clc = db.getCollection(collection);
        return clc;

    }




  //renders the About Project page
    public static Result getContact() throws Exception {

        return ok(contact.render());
    }

    
  //renders the intro Project page
    public static Result getIntro() throws Exception {

        return ok(intro.render());
    }


//renders the registration page
    public static Result getRegister() throws Exception {

        return ok(register.render("",""));
    }



}
