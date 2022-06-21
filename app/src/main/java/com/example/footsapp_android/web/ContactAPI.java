package com.example.footsapp_android.web;

import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.MyApplication;
import com.example.footsapp_android.entities.Contact;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactAPI implements Runnable {
    private ContactDao dao;
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public ContactAPI(ContactDao dao, String token) {
        this.dao = dao;

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();


        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void get() {
        Call<List<Contact>> call = webServiceAPI.getContacts();
        try {
            Response<List<Contact>> response = call.execute();
            List<Contact> contacts = response.body();
            if (contacts != null) {
                dao.nukeTable();
                for (Contact c : contacts) {
                    if (c.getTime() != null) {
                        if (c.getTime().length() != 0) {
                            // formatting the time to show hours and minutes
                            c.setTime(c.getTime().substring(11, 16));
                        }
                    }
                    dao.insert(c);
                }
            }
        } catch (IOException e) { // catching request error
            e.printStackTrace();
        }
    }


    public void post(Contact contact) {
        Call<Void> call = webServiceAPI.createContact(contact);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });

    }

    @Override
    public void run() {
        get();
    }
}

