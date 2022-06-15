package com.example.footsapp_android.web;

import androidx.lifecycle.MutableLiveData;

import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.MyApplication;
import com.example.footsapp_android.R;
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

public class ContactAPI implements Runnable{
    private MutableLiveData<List<Contact>> contactListData;
    private ContactDao dao;
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public ContactAPI(ContactDao dao, String token) {
        this.dao = dao;

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();


        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void get() {
        Call<List<Contact>> call = webServiceAPI.getContacts();
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                List<Contact> contacts = response.body();
                if (contacts != null) {
                    for (Contact c: contacts) {
                        dao.insert(c);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
            }
        });
    }

    @Override
    public void run() {
        get();
    }
}

