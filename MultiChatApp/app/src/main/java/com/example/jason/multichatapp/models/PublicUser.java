package com.example.jason.multichatapp.models;

import java.util.Map;

/**
 * Created by jason on 10/14/17.
 *
 * This class will be used as a public representation of a
 * users data. Since Firebase doesn't provide a simple way to get
 * a list of registered users.
 *
 */

public class PublicUser {
    public String email; // TODO: omit this from being shared publicly instaed share a unique username
    public String uid;
    public String language;
    public String country;
    public String states;


    public PublicUser() {

    }
    public PublicUser(String uid, String email, String language, String country, String states) {
        this.uid= uid;
        this.email = email;
        this.language = language;
        this.country = country;
        this.states = states;
    }
    public PublicUser fromObject(Map<String, String> message) {
        this.uid = message.get("uid");
        this.email = message.get("email");
        this.language = message.get("language");
        this.country = message.get("country");
        this.states = message.get("states");
        return this;
    }

    public String toString() {
        return "User: " +
            "{uid: " + this.uid +
            ", email: " + this.email +
            ", language: " + this.language +
            ", country: " + this.country +
                ", states: " + this.states + "}";
    }
}
