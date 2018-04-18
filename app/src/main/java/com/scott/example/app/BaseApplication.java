package com.scott.example.app;

import android.app.Application;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.scott.annotionprocessor.TaskType;
import com.scott.example.example.MyUploadHandlerFactory;
import com.scott.transer.TranserConfig;
import com.scott.transer.TranserService;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.handler.DefaultDownloadFactory;
import com.shilec.xlogger.Config;
import com.shilec.xlogger.XLogger;

/**
 * Created by shijiale on 2017/12/16.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xlogger,不是必须的，transer service 中有默认初始化
        Config logConfig = new Config.Builder()
                .enableDebug()
                .setFormater(new XLoggerFormaterImpl())
                .build();
        XLogger.init(logConfig);

        //初始化TranserService 如果不使用任务管理则不需要初始化该服务
        TranserConfig config = new TranserConfig.Builder()
                .setDownloadConcurrentThreadSize(3)
                .setUploadConcurrentThreadSize(3)
                .setSupportProcessorDynamicProxy(true)
                //.addCmdInterceptor(new LogInterceptor())
                .build();
        TranserService.init(this,config);

        //初始化TaskEventBus,如果不需要使用EventDispatcher
        //和TaskSubscriber 则不需要初始化，transer Service
        //中会初始化
        TaskEventBus.init(this);
    }
}
