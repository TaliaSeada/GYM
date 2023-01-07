package com.example.gym.updates;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
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

    @Exclude
    public String getPrettyDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(this.date).toString();
    }

    public Update(HashMap<String, Object> hashMap){
        HashMap<String, Integer> d = (HashMap<String, Integer>) hashMap.get("date");
        long dateEpoch = d.get("_seconds") * 1000L;
        this.date = new Date(dateEpoch);
        this.content = (String) hashMap.get("content");
        this.id = (String) hashMap.get("id");
    }
}
