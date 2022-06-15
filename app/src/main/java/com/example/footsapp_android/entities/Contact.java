package com.example.footsapp_android.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Contact {

    @PrimaryKey(autoGenerate=true)
    @SerializedName("UserID")
    private int id;
    @SerializedName("id")
    private String username;
    @SerializedName("name")
    private String nickname;
    private String server;
    @SerializedName("last")
    private String lastMessage;
    @SerializedName("lastdate")
    private String time;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Contact(int id, String username, String nickname, String server) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.server = server;
        this.lastMessage = "** No messages **";
        this.time = "just now";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", server='" + server + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
