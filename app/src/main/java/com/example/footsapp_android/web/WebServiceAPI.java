package com.example.footsapp_android.web;

import com.example.footsapp_android.entities.Contact;
import com.example.footsapp_android.entities.Message;
import com.example.footsapp_android.entities.Transfer;
import com.example.footsapp_android.entities.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebServiceAPI {

    @POST("Login")
    Call<ResponseBody> login(@Query("username") String username, @Query("password") String password,
                             @Query("deviceToken") String deviceToken);

    @POST("Login/Register")
    Call<ResponseBody> register(@Body User user);

    @GET("Contacts")
    Call<List<Contact>> getContacts();

    @POST("Users/addContact")
    Call<Void> createContact(@Body Contact contact);

    @DELETE("Contacts/{id}")
    Call<Void> deleteContact(@Path("id") String id);

    @GET("Contacts/{id}/messages")
    Call<List<Message>> getMessages(@Path("id") String id);

    @POST("Contacts/{id}/messages")
    Call<Void> createMessage(@Path("id") String id, @Body String content);

    @POST("transfer")
    Call<Void> transfer(@Body Transfer transfer);
}