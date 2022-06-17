package com.example.footsapp_android.entities;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Message {


    @PrimaryKey(autoGenerate=true)
    private int id;
    private String content;
    @SerializedName("created")
    private String time;
    @SerializedName("sent")
    private boolean sender;
    private String sentFrom;

    public Message(int id, String content, String time, boolean sender, String sentFrom) {
        this.id = id;
        this.content = content;
        this.time = time;
        this.sender = sender;
        this.sentFrom = sentFrom;
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

    public String getSentFrom() {
        return sentFrom;
    }

    public void setSentFrom(String sentFrom) {
        this.sentFrom = sentFrom;
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
