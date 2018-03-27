package com.scott.example.app;

import android.app.Application;
import android.content.Intent;

import com.scott.annotionprocessor.TaskType;
import com.scott.example.example.MyUploadHandlerFactory;
import com.scott.transer.TranserConfig;
import com.scott.transer.TranserService;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.handler.DefaultDownloadFactory;

/**
 * Created by shijiale on 2017/12/16.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TranserConfig config = new TranserConfig.Builder()
                .setDownloadConcurrentThreadSize(3)
                .setUploadConcurrentThreadSize(3)
                .setSupportProcessorDynamicProxy(true)
                //.addHandlerFactory(TaskType.TYPE_HTTP_UPLOAD,new MyUploadHandlerFactory())
                .build();
        TranserService.init(this,config);

        TaskEventBus.init(this);
    }
}
