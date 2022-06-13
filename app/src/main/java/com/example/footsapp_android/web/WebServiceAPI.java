package com.example.footsapp_android.web;

import com.example.footsapp_android.entities.Contact;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebServiceAPI {

    @POST("/Login")
    @FormUrlEncoded
    Call<String> login(
            @Field("username") String username,
            @Field("password") String password);

    @GET("Contacts")
    Call<List<Contact>> getContacts();

    @POST("Contacts")
    Call<Void> createContact(@Body Contact contact);

    @DELETE("Contacts/{id}")
    Call<Void> deleteContact(@Path("id") String id);

}