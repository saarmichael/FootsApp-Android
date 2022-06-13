package com.example.footsapp_android.web;

import androidx.lifecycle.MutableLiveData;

import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.MyApplication;
import com.example.footsapp_android.R;
import com.example.footsapp_android.entities.Contact;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactAPI {
    private MutableLiveData<List<Contact>> contactListData;
    private ContactDao dao;
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public ContactAPI(MutableLiveData<List<Contact>> ContactListData, ContactDao dao) {
        this.contactListData = ContactListData;
        this.dao = dao;

        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
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
                /*new Thread(() -> {
                    dao.clear();
                    dao.insertList(response.body());
                    contactListData.ContactValue(dao.get());
                }).start();*/
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
            }
        });
    }
}

