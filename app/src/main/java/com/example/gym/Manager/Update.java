package com.example.gym.Manager;

import java.util.Date;
import java.util.HashMap;

public class Update {

    public String id;
    public Date date;
    public String content;

    public Update(){
    }

    public Update(Date date, String content){
        this.date = date;
        this.content = content;
    }

    public Update(HashMap<String, Object> hashMap){
        HashMap<String, Integer> d = (HashMap<String, Integer>) hashMap.get("date");
        long dateEpoch = d.get("_seconds") * 1000L;
        this.date = new Date(dateEpoch);
        this.content = (String) hashMap.get("content");
        this.id = (String) hashMap.get("id");
    }
}
