package com.scott.transer;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.scott.transer.dao.DaoHelper;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.manager.ITaskManager;
import com.scott.transer.manager.ITaskProcessCallback;
import com.scott.annotionprocessor.ProcessType;
import com.scott.transer.manager.ITaskProcessor;
import com.scott.transer.manager.TaskProcessorProxy;
import com.scott.transer.manager.TaskDbProcessor;
import com.scott.transer.manager.TaskManager;
import com.scott.transer.manager.TaskManagerProxy;
import com.scott.transer.manager.TaskProcessor;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.handler.DefaultDownloadFactory;
import com.scott.transer.handler.DefaultUploadFactory;
import com.scott.transer.manager.dynamicproxy.ProcessorDynamicProxy;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-14 15:45</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class TranserService extends Service implements ITaskProcessCallback{

    ITaskManager mTaskManagerProxy;

    public static final String ACTION_EXECUTE_CMD = "_CMD";

    static Context mContext;

    private static TranserConfig mConfig;

    public static synchronized void init(Context context,TranserConfig config) {
        if(mConfig != null) {
            throw new IllegalStateException("Transer is already inited!");
        }

        if(!(context instanceof Application)) {
            throw new IllegalStateException("Context must be a application context!");
        }

        if(config == null) {
            throw new IllegalStateException("TranserConfig can not be a null value.");
        }

        mConfig = config;
        context.startService(new Intent(context,TranserService.class));
    }

    static Context getContext() {
        return mContext;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return Service.START_STICKY;
        }
        String action = intent.getAction();
        if(action == null) {
            return Service.START_STICKY;
        }
        switch (action) {
            case ACTION_EXECUTE_CMD:
                ITaskCmd cmd = TaskEventBus.getDefault().getDispatcher().getTaskCmd();
                if(cmd != null) {
                    mTaskManagerProxy.process(cmd);
                }
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TaskEventBus.init(getApplicationContext());
        DaoHelper.init(getApplicationContext());
        mContext = getApplicationContext();

        mTaskManagerProxy = new TaskManagerProxy();
        mTaskManagerProxy.setProcessCallback(this);

        //内存中的任务列表处理器
        ITaskProcessor memoryProcessor;
        if(mConfig.mBuilder.mMemoryProcessor != null) {
            memoryProcessor = mConfig.mBuilder.mMemoryProcessor;
        } else {
            memoryProcessor = new TaskProcessor();
        }

        //任务持久化处理器
        ITaskProcessor databaseProcessor;
        if(mConfig.mBuilder.mDatabaseProcessor != null) {
            databaseProcessor = mConfig.mBuilder.mDatabaseProcessor;
        } else {
            databaseProcessor = new TaskDbProcessor();
        }
        //设置一个代理处理器
        mTaskManagerProxy.setTaskProcessor(new TaskProcessorProxy(memoryProcessor,databaseProcessor));

        //设置manager
        if(mConfig.mBuilder.mTaskManager != null) {
            mTaskManagerProxy.setManager(mConfig.mBuilder.mTaskManager);
        } else {
            mTaskManagerProxy.setManager(new TaskManager());
        }

        //设置handler的工厂类
        if(mConfig.mBuilder.mHanlderCreators.isEmpty()) {
            mTaskManagerProxy.addHandlerCreator(TaskType.TYPE_HTTP_DOWNLOAD, new DefaultDownloadFactory());
            mTaskManagerProxy.addHandlerCreator(TaskType.TYPE_HTTP_UPLOAD, new DefaultUploadFactory());
        } else {
            for(TaskType taskType : mConfig.mBuilder.mHanlderCreators.keySet()) {
                mTaskManagerProxy.addHandlerCreator(taskType,mConfig.mBuilder.mHanlderCreators.get(taskType));
            }
        }

        //设置下载线程池
        ThreadPoolExecutor downloadThreadPool;
        if(mConfig.mBuilder.mDownloadThreadPool == null) {
            //并发数
            int corePoolSize = 3;
            if(mConfig.mBuilder.mDownloadConcurrentSize > 0) {
                corePoolSize = mConfig.mBuilder.mDownloadConcurrentSize;
            }
            downloadThreadPool = new ThreadPoolExecutor(corePoolSize, corePoolSize,
                    6000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10000));
        } else {
            downloadThreadPool = mConfig.mBuilder.mDownloadThreadPool;
        }
        mTaskManagerProxy.addThreadPool(TaskType.TYPE_HTTP_DOWNLOAD, downloadThreadPool);

        //设置上传线程池
        ThreadPoolExecutor uploadThreadPool;
        if(mConfig.mBuilder.mUploadThreadPool == null) {
            int corePoolSize = 3;
            if(mConfig.mBuilder.mUploadConcurrentThreadSize > 0) {
                corePoolSize = mConfig.mBuilder.mUploadConcurrentThreadSize;
            }
            uploadThreadPool = new ThreadPoolExecutor(corePoolSize,corePoolSize,
                    6000, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(10000));
        } else {
            uploadThreadPool = mConfig.mBuilder.mUploadThreadPool;
        }

        if(mConfig.mBuilder.isSupportProcessorDynamicProxy) {
            ITaskProcessor processor = ProcessorDynamicProxy.getInstance().create();
            processor.setTaskManager(mTaskManagerProxy);
        }
        mTaskManagerProxy.addThreadPool(TaskType.TYPE_HTTP_DOWNLOAD,uploadThreadPool);
    }

    @Override
    public void onFinished(TaskType taskType, ProcessType type,List<ITask> tasks) {
        TaskEventBus.getDefault().getDispatcher().dispatchTasks(taskType,type,tasks);
        //注册了TYPE_DEFAULT 会收到任何变更
        TaskEventBus.getDefault().getDispatcher().dispatchTasks(taskType,ProcessType.TYPE_DEFAULT,tasks);
    }


}
