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
import com.scott.transer.manager.ITaskInternalProcessor;
import com.scott.transer.manager.TaskProcessorProxy;
import com.scott.transer.manager.TaskDbProcessor;
import com.scott.transer.manager.TaskManager;
import com.scott.transer.manager.TaskManagerProxy;
import com.scott.transer.manager.TaskProcessor;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.handler.DefaultDownloadFactory;
import com.scott.transer.handler.DefaultUploadFactory;
import com.scott.transer.manager.dynamicproxy.ProcessorDynamicProxyFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-14 15:45</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 * 为什么要使用动态代理 或者 TaskEventBus 去间接操作任务，而不是 直接使用 TaskProcessor
 * 去操作任务？
 *
 * 因为通过TaskEventBus 也好，通过ITaskProcessor 的动态代理类也好，去操作任务。 发出的命令
 * 是添加到了一个命令队列中，一个一个被执行的。直接操作任务是耗时操作，需要放到线程中，而直接操作势必会出现 操作行为并发的情况，这样就会导致
 * TaskList 的 同步问题，和数据库同步的问题。
 *
 * 这里用了TaskManager 统一维护一个命令队列，保证了同一时刻，只会执行同一个命令（只会在一个线程中修改任务列表或数据库）。
 * </p>
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

        //使用ITaskProcessor 动态代理执行任务操作 将不会走这里。
        switch (action) {
            case ACTION_EXECUTE_CMD:
                TaskCmd cmd = TaskEventBus.getDefault().getDispatcher().getTaskCmd();
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
        ITaskInternalProcessor memoryProcessor;
        if(mConfig.mBuilder.mMemoryProcessor != null) {
            memoryProcessor = mConfig.mBuilder.mMemoryProcessor;
        } else {
            memoryProcessor = new TaskProcessor();
        }

        //任务持久化处理器
        ITaskInternalProcessor databaseProcessor;
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
        mTaskManagerProxy.addThreadPool(TaskType.TYPE_HTTP_UPLOAD,uploadThreadPool);

        if(mConfig.mBuilder.isSupportProcessorDynamicProxy) {
            ITaskInternalProcessor processor = (ITaskInternalProcessor) ProcessorDynamicProxyFactory.getInstance().create();
            processor.setTaskManager(mTaskManagerProxy);
        }
        mTaskManagerProxy.addThreadPool(TaskType.TYPE_HTTP_DOWNLOAD,uploadThreadPool);
    }

    @Override
    public void onFinished(String userId,TaskType taskType, ProcessType type,List<ITask> tasks) {
        TaskEventBus.getDefault().getDispatcher().dispatchTasks(taskType,type,tasks);
        //注册了TYPE_DEFAULT 会收到任何变更
        if(type != ProcessType.TYPE_DEFAULT) {
            TaskEventBus.getDefault().getDispatcher().
                    dispatchTasks(taskType, ProcessType.TYPE_DEFAULT, tasks);
        }
    }


}
