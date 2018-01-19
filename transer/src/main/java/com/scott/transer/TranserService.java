package com.scott.transer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.scott.transer.dao.DaoHelper;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.manager.ITaskManagerProxy;
import com.scott.transer.manager.ITaskProcessCallback;
import com.scott.annotionprocessor.ProcessType;
import com.scott.transer.manager.TaskProcessorProxy;
import com.scott.transer.manager.TaskDbProcessor;
import com.scott.transer.manager.TaskManager;
import com.scott.transer.manager.TaskManagerProxy;
import com.scott.transer.manager.TaskProcessor;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.handler.DefaultDownloadCreator;
import com.scott.transer.handler.DefaultUploadCreator;

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

    ITaskManagerProxy mTaskManagerProxy;

    public static final String ACTION_EXECUTE_CMD = "_CMD";

    static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        mTaskManagerProxy.setTaskProcessor(new TaskProcessorProxy(new TaskProcessor(),new TaskDbProcessor()));
        mTaskManagerProxy.setTaskManager(new TaskManager());
        mTaskManagerProxy.addHandlerCreator(TaskType.TYPE_HTTP_DOWNLOAD, new DefaultDownloadCreator());
        mTaskManagerProxy.addHandlerCreator(TaskType.TYPE_HTTP_UPLOAD, new DefaultUploadCreator());

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3,3,
                6000, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(10000));
        mTaskManagerProxy.setThreadPool(TaskType.TYPE_HTTP_UPLOAD, threadPool);

        threadPool = new ThreadPoolExecutor(3,3,
                6000, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(10000));
        mTaskManagerProxy.setThreadPool(TaskType.TYPE_HTTP_DOWNLOAD,threadPool);
    }

    @Override
    public void onFinished(TaskType taskType, ProcessType type,List<ITask> tasks) {
        TaskEventBus.getDefault().getDispatcher().dispatchTasks(taskType,type,tasks);
        TaskEventBus.getDefault().getDispatcher().dispatchTasks(taskType,ProcessType.TYPE_DEFAULT,tasks);
    }


}
