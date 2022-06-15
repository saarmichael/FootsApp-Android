package com.example.footsapp_android.web;

import android.util.Log;

import com.example.footsapp_android.MyApplication;
import com.example.footsapp_android.R;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginAPI implements Runnable{

    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    private static String token;
    private final String username;
    private final String password;


    public LoginAPI(String username, String password) {
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

        this.username = username;
        this.password = password;
    }

    public void post(String username, String password) {
        Call<ResponseBody> call = webServiceAPI.login(username, password);
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                token = response.body().string();
            } else { // catching wrong login
                Log.e("LoginAPI", "onResponse: " + response.errorBody().string());
                token = null;
            }
        } catch (IOException e) { // catching request error
            e.printStackTrace();
        }
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

    @Override
    public void run() {
        post(this.username, this.password);
    }

    public static String getToken() {
        return token;
    }
}
