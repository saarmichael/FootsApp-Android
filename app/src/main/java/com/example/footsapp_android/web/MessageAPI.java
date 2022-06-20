package com.example.footsapp_android.web;

import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.MessageDao;
import com.example.footsapp_android.MyApplication;
import com.example.footsapp_android.R;
import com.example.footsapp_android.entities.Message;
import com.example.footsapp_android.entities.Transfer;

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

public class MessageAPI implements Runnable {
    private ContactDao contactDao;
    private MessageDao messageDao;
    Retrofit retrofit;
    Retrofit transferRetrofit;
    WebServiceAPI webServiceAPI;
    WebServiceAPI transferWebServiceAPI;
    private String contactName;

    public MessageAPI(MessageDao messageDao, ContactDao contactDao, String token, String contactName, String server) {
        this.contactDao = contactDao;
        this.messageDao = messageDao;
        this.contactName = contactName;
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
        if (server != null) {
            transferRetrofit = new Retrofit.Builder()
                    .baseUrl(server)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            transferWebServiceAPI = transferRetrofit.create(WebServiceAPI.class);
        }
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void get(String id) {
        Call<List<Message>> call = webServiceAPI.getMessages(id);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                List<Message> messages = response.body();
                if (messages != null) {
                    for (Message m: messages) {
                        m.setSentFrom(id);
                        // formatting the time to show hours and minutes
                        m.setTime(m.getTime().substring(11, 16));
                        messageDao.insert(m);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
            }
        });
    }

    public void post(String content, Transfer transfer) {
        Call<Void> call = webServiceAPI.createMessage(contactName, content);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });

        Call<Void> callTransfer = transferWebServiceAPI.transfer(transfer);
        callTransfer.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> callTransfer, Response<Void> response) {
            }

            @Override
            public void onFailure(Call<Void> callTransfer, Throwable t) {
            }
        });
    }


    @Override
    public void run() {
        get(contactName);
    }

}
