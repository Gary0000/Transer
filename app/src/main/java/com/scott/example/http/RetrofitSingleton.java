package com.scott.example.http;

import com.scott.example.utils.Contacts;
import com.scott.transer.http.OkHttpProxy;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2018/2/3</P>
 * <P>Email: shilec@126.com</p>
 */

public class RetrofitSingleton {

    private static RetrofitSingleton sInstance;
    private Retrofit mRetrofit;

    private RetrofitSingleton() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://" + Contacts.TEST_HOST + "/WebDemo/")
                .client(OkHttpProxy.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public static RetrofitSingleton getInstance() {
        synchronized (RetrofitSingleton.class) {
            if(sInstance == null) {
                sInstance = new RetrofitSingleton();
            }
        }
        return sInstance;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }
}
