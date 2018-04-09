package com.scott.transer.http;

import com.shilec.xlogger.XLogger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-04-09 14:05</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */
public class LogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        XLogger.getDefault().e("request = " + chain.request());
        XLogger.getDefault().e("request header = " + chain.request().headers());
        return chain.proceed(chain.request());
    }
}
