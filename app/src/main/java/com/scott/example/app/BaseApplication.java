package com.scott.example.app;

import android.app.Application;
import android.content.Intent;

import com.scott.annotionprocessor.TaskType;
import com.scott.transer.TranserConfig;
import com.scott.transer.TranserService;
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
                .build();
        TranserService.init(this,config);
    }
}
