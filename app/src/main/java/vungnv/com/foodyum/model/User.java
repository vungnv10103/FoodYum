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
    public int status;
    public String searchHistory;
    public String favouriteRestaurant;
    public String feedback;


    public User(String email, String name, String phoneNumber, String img, String pass) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.img = img;
        this.pass = pass;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("name", name);
        result.put("phoneNumber", phoneNumber);
        result.put("img", img);
        result.put("pass", pass);
        return result;
    }

    public User() {
    }
}
