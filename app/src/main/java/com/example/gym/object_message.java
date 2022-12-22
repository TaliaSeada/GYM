package com.example.gym;

public class object_message {

    String id;
    String send;
    String recv;
    String body;
    String title;
    String answer;
    String date;
    boolean status;

    public object_message(String id, String title, String send, String recv, String body, String answer, String date) {
        this.id = id;
        this.title = title;
        this.send = send;
        this.recv = recv;
        this.body = body;
        this.answer = answer;
        this.date=date;
        this.status = answer.isEmpty();
    }

    public boolean GetStatus(){
        return status;
    }

    @Override
    public String toString() {
        String ans = "title: "+ title +'\n' +
                    "send: " + send + '\n' +
                    "body: " + body + '\n' +
                    "date: " + date +'\n';
        if (!status) {
           ans+=  "answer: " + answer;
        }
     return ans;
    }
}
