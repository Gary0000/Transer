package com.scott.transer;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.scott.transer.dao.DaoHelper;
import com.scott.transer.event.EventDispatcher;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.manager.ITaskManager;
import com.scott.transer.manager.ITaskProcessCallback;
import com.scott.annotionprocessor.ProcessType;
import com.scott.transer.manager.ITaskInternalProcessor;
//import com.scott.transer.manager.TaskManager;
import com.scott.transer.manager.TaskProcessorProxy;
import com.scott.transer.manager.TaskDbProcessor;
import com.scott.transer.manager.TaskManager;
import com.scott.transer.manager.TaskProcessor;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.handler.DefaultDownloadFactory;
import com.scott.transer.handler.DefaultUploadFactory;
import com.scott.transer.manager.dynamicproxy.ProcessorDynamicProxyFactory;
import com.shilec.xlogger.Config;
import com.shilec.xlogger.XLogger;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-14 15:45</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *
 *      传输核心服务，持有TaskManager 管理任务。
 *      通过TranserConfig 配置
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
                TaskCmd cmd = ((EventDispatcher)TaskEventBus.getDefault().getDispatcher()).getTaskCmd();
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
        XLogger.init(new Config.Builder().enableDebug().build());
        TaskEventBus.init(getApplicationContext());
        DaoHelper.init(getApplicationContext());
        mContext = getApplicationContext();

        initTaskManager();
    }

    private void initTaskManager() {

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
        //创建任务管理器
        mTaskManagerProxy = new TaskManager(new TaskProcessorProxy(memoryProcessor,databaseProcessor),
                mConfig.mBuilder.interceptors);
        mTaskManagerProxy.setProcessCallback(this);
        //设置一个代理处理器
        //mTaskManagerProxy.setTaskProcessor();

        //设置manager
        if(mConfig.mBuilder.mTaskManager != null) {
            ///mTaskManagerProxy.setManager(mConfig.mBuilder.mTaskManager);
        } else {
           // mTaskManagerProxy.setManager(new TaskManager());
        }

        //设置handler的工厂类
        mTaskManagerProxy.addHandlerCreator(TaskType.TYPE_HTTP_DOWNLOAD, new DefaultDownloadFactory());
        mTaskManagerProxy.addHandlerCreator(TaskType.TYPE_HTTP_UPLOAD, new DefaultUploadFactory());
        if(!mConfig.mBuilder.mHanlderCreators.isEmpty()) {
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

            BlockingQueue blockingQueue = null;
            //是否支持小文件优先上传
            if(mConfig.mBuilder.isSupportSmallFileFirstUpload) {
                blockingQueue = new SmallTaskFirstDequeueBlockingQueue(10000);
            } else {
                blockingQueue = new ArrayBlockingQueue(10000);
            }
            uploadThreadPool = new ThreadPoolExecutor(corePoolSize,corePoolSize,
                    6000, TimeUnit.MILLISECONDS,blockingQueue);
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
