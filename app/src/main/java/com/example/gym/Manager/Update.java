package com.example.gym.Manager;

import java.util.Date;

public class Update {

    public Date date;
    public String content;

    public Update(){
    }

    public Update(Date date, String content){
        this.date = date;
        this.content = content;
    }
}
