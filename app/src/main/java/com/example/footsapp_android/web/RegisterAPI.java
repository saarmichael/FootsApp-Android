package com.example.footsapp_android.web;

import com.example.footsapp_android.MyApplication;
import com.example.footsapp_android.entities.User;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterAPI implements Runnable{
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    private User user;
    private boolean success;


    public RegisterAPI(User user) {

        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);

        this.user = user;
    }

    public void post() {
        Call<ResponseBody> call = webServiceAPI.register(user);
        try {
            Response<ResponseBody> response = call.execute();
            this.success = response.isSuccessful();
        } catch (IOException e) { // catching request error
            e.printStackTrace();
            this.success = false;
        }
    }

    @Override
    public void run() {
        post();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
