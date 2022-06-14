package com.example.footsapp_android.web;

import android.util.Log;

import com.example.footsapp_android.MyApplication;
import com.example.footsapp_android.R;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginAPI {

    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    private static String token;

    public LoginAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public String post(String username, String password) {
        Call<ResponseBody> call = webServiceAPI.login(username, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        token = response.body().string();
                    } else {
                        Log.e("LoginAPI", "onResponse: " + response.errorBody().string());
                        token = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("LoginAPI", "onResponse: " + response.body());

                /*new Thread(() -> {
                    dao.clear();
                    dao.insertList(response.body());
                    contactListData.ContactValue(dao.get());
                }).start();*/
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("LoginAPI", "onFailure: " + t.getMessage());
            }
        });
        return token;
    }

    public String getFooFromAPI(){
        Call<ResponseBody> call = webServiceAPI.getFoo();
        final String[] result = {"blar"};
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //result[0] = response.body();
                Log.d("LoginAPI", "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result[0] = "bla";
                Log.d("LoginAPI", "onFailure: " + t.getMessage());
            }
        });
        return result[0];
    }

}
