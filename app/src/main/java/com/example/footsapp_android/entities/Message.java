package com.example.footsapp_android.entities;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Message {


    @PrimaryKey(autoGenerate=true)
    private int id;
    private String content;
    private String time;
    private boolean sender;

    public Message(int id, String content, boolean sender) {
        this.id = id;
        this.content = content;
        //TODO this.time = time;
        time = "12:00";
        this.sender = sender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSender() {
        return sender;
    }

    public void setSender(boolean sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", sender=" + sender +
                '}';
    }
}
