package com.example.gym.auth;

import java.util.HashMap;

public class User {

    public String email;
    public String full_name;
    public String role;

    public User(){
    }

    public User(String email, String full_name, String role){
        this.email = email;
        this.full_name = full_name;
        this.role = role;
    }

    public User(HashMap<String, Object> hashMap){
        this.full_name = (String) hashMap.get("full_name");
        this.role = (String) hashMap.get("role");
        this.email = (String) hashMap.get("email");
    }
}
