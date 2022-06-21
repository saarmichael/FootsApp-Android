package com.example.footsapp_android.web;

import com.example.footsapp_android.MyApplication;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginAPI implements Runnable{

    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    private static String token;
    public static boolean validURL;
    private final String username;
    private final String password;
    private final String deviceToken;


    public LoginAPI(String username, String password, String deviceToken) {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        validURL = true;

        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(MyApplication.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            webServiceAPI = retrofit.create(WebServiceAPI.class);
        } catch (Exception e) {
            validURL = false;
        }

        this.username = username;
        this.password = password;
        this.deviceToken = deviceToken;

    }

    public void post(String username, String password, String deviceToken) {
        Call<ResponseBody> call = webServiceAPI.login(username, password, deviceToken);
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                token = response.body().string();
            } else { // catching wrong login
                token = null;
            }
        } catch (IOException e) { // catching request error
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        post(this.username, this.password, this.deviceToken);
    }

    public static String getToken() {
        return token;
    }
}
