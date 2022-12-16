package com.example.gym;

public class object_massege {

    String id;
    String send;
    String recv;
    String body;
    String title;
    String answer;
    boolean status;

    public object_massege(String id, String title, String send, String recv, String body, String answer) {
        this.id = id;
        this.title = title;
        this.send = send;
        this.recv = recv;
        this.body = body;
        this.answer = answer;
        this.status = answer.isEmpty();
    }

    public boolean GetStatus(){
        return status;
    }

    @Override
    public String toString() {
        String ans = "title: "+ title +'\n' +
                    "send: " + send + '\n' +
                    "body: " + body + '\n';
        if (!status) {
           ans+=  "answer: " + answer;
        }
     return ans;
    }
}
