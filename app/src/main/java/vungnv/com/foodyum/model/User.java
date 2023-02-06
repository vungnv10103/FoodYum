package vungnv.com.foodyum.model;

import java.util.HashMap;

public class User {
    public int stt;
    public String id;
    public String img;
    public String name;
    public String email;
    public String pass;
    public String phoneNumber;
    public String address;
    public String searchHistory;
    public String coordinates;
    public String feedback;


    public User(int stt, String id, String img, String name, String email, String pass, String phoneNumber, String searchHistory, String address, String coordinates, String feedback) {
        this.stt = stt;
        this.id = id;
        this.img = img;
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.phoneNumber = phoneNumber;
        this.searchHistory = searchHistory;
        this.address = address;
        this.coordinates = coordinates;
        this.feedback = feedback;
    }

    public User(String email, String pass) {
        this.email = email;
        this.pass = pass;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("pass", pass);
        return result;
    }

    public User() {
    }
}
