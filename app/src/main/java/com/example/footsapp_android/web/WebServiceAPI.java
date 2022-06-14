package com.example.footsapp_android.web;

import com.example.footsapp_android.entities.Contact;

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
    Call<ResponseBody> login(@Query("username") String username, @Query("password") String password);

    @GET("Login")
    Call<ResponseBody> getFoo();

    @GET("Contacts")
    Call<List<Contact>> getContacts();

    @POST("Contacts")
    Call<Void> createContact(@Body Contact contact);

    @DELETE("Contacts/{id}")
    Call<Void> deleteContact(@Path("id") String id);

}