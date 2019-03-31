package com.scott.transer.manager;

import android.text.TextUtils;

import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.ITask;
import com.scott.transer.TaskCmd;
import com.scott.transer.TaskState;
import com.scott.transer.handler.ITaskHandlerCallback;
import com.scott.transer.handler.ITaskHandlerFactory;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.handler.ITaskHolder;
import com.scott.transer.manager.interceptor.DispatchCmdInterceptor;
import com.scott.transer.manager.interceptor.ReNameDownloadFileInterceptor;
import com.scott.transer.manager.interceptor.ChainImpl;
import com.scott.transer.manager.interceptor.CheckParamInterceptor;
import com.scott.transer.manager.interceptor.Interceptor;
import com.shilec.xlogger.XLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-14 14:42</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      分层设计，最上层为代理对象。
 *      下面一层为拦截器管理器
 *      下面一层为命令分发管理器
 * </p>
 */

public class TaskManager implements ITaskManager, ITaskHandlerCallback {

    private ITaskInternalProcessor mProcessor; //processor proxy
    private ExecutorService mCmdThreadPool; //cmd thread pool
    private ITaskProcessCallback mProcessCallback;
    private final String TAG = TaskManager.class.getSimpleName();
    private List<Interceptor> mInterceptors = new ArrayList<>();
    private Map<TaskType,ThreadPoolExecutor> mThreadPool = new HashMap<>();
    private Map<TaskType,ITaskHandlerFactory> mTaskHandlerCreators = new HashMap<>();
    private List<ITaskHolder> mTasks = new ArrayList<>(); //task list

    private Object mLock = new Object();

    public TaskManager(ITaskInternalProcessor processor, List<Interceptor> interceptors) {
        mCmdThreadPool = Executors.newSingleThreadExecutor();
        mInterceptors.add(new CheckParamInterceptor());
        mInterceptors.add(new ReNameDownloadFileInterceptor(this));
        if(interceptors != null) {
            mInterceptors.addAll(interceptors);
        }
        mInterceptors.add(new DispatchCmdInterceptor(processor));

        mProcessor = processor;
        mProcessor.setTaskManager(this);
    }

    private void interceptCmd(TaskCmd cmd) {
        synchronized (mLock) {
            Interceptor.Chain chain = new ChainImpl(0, mInterceptors);
            TaskCmd taskCmd = chain.process(cmd);
            callExecuteCmdFinished(taskCmd);
        }
    }

    private void callExecuteCmdFinished(TaskCmd cmd) {
        List<ITask> taskList = new ArrayList<>();
        for(ITaskHolder holder : getTasks()) {
            //handler callback 的 task 不携带 userId
            if(cmd.getUserId() != null) {
                if(cmd.getTaskType() == holder.getType() &&
                        TextUtils.equals(cmd.getUserId(),holder.getTask().getUserId())) {
                    taskList.add(holder.getTask());
                }
            } else {
                if(cmd.getTaskType() == holder.getType()) {
                    taskList.add(holder.getTask());
                }
            }
        }
        mProcessCallback.onFinished(cmd.getUserId(),cmd.getTaskType(),cmd.getProceeType(),taskList);
    }

    @Override
    public void process(final TaskCmd cmd) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                interceptCmd(cmd);
            }
        };
        mCmdThreadPool.execute(runnable);
    }

    @Override
    public void setProcessCallback(ITaskProcessCallback callback) {
        mProcessCallback = callback;
    }

    @Override
    public void addThreadPool(TaskType taskType, ThreadPoolExecutor threadPool) {
        synchronized (mThreadPool) {
            mThreadPool.put(taskType, threadPool);
        }
    }


    @Override
    public ThreadPoolExecutor getTaskThreadPool(TaskType type) {
        return mThreadPool.get(type);
    }

    @Override
    public ITaskHandlerFactory getTaskHandlerCreator(ITask task) {
        ITaskHandlerFactory creator = mTaskHandlerCreators.get(task.getType());
        creator.setTaskHandlerCallback(this);
        return creator;
    }

    @Override
    public void addHandlerCreator(TaskType type, ITaskHandlerFactory handlerCreator) {
        synchronized (mTaskHandlerCreators) {
            mTaskHandlerCreators.put(type, handlerCreator);
        }
    }


    @Override
    public List<ITaskHolder> getTasks() {
        return mTasks;
    }

    @Override
    public void onReady(ITask task) {
        TaskCmd cmd = new TaskCmd.Builder()
                .setTask(task)
                .setProcessType(ProcessType.TYPE_UPDATE_TASK)
                .build();
        process(cmd);
    }

    @Override
    public void onStart(ITask task) {
        //XLogger.getDefault().e(TAG,"start = " + params);
        TaskCmd cmd = new TaskCmd.Builder()
                .setTask(task)
                .setProcessType(ProcessType.TYPE_UPDATE_TASK)
                .build();
        process(cmd);
    }

    @Override
    public void onStop(ITask task) {
        //XLogger.getDefault().e(TAG,"stop = " + params);
        TaskCmd cmd = new TaskCmd.Builder()
                .setTask(task)
                .setProcessType(ProcessType.TYPE_UPDATE_TASK)
                .build();
        process(cmd);
    }

    @Override
    public void onError(int code, ITask task) {
        //XLogger.getDefault().e(TAG,"error = " + params);
        TaskCmd cmd = new TaskCmd.Builder()
                .setTask(task)
                .setState(TaskState.STATE_STOP)
                .setProcessType(ProcessType.TYPE_UPDATE_TASK)
                .build();
        process(cmd);
    }

    @Override
    public void onSpeedChanged(long speed, ITask task) {
        TaskCmd cmd = new TaskCmd.Builder()
                .setTask(task)
                .setProcessType(ProcessType.TYPE_UPDATE_TASK_WTIHOUT_SAVE)
                .setState(TaskState.STATE_RUNNING)
                .build();
        process(cmd);
        XLogger.getDefault().e(TAG,"speed == " + task.getSpeed());
    }

    @Override
    public void onPiceSuccessful(ITask task) {
        XLogger.getDefault().e(TAG," PICE STATE = " + task.getState());
        TaskCmd cmd = new TaskCmd.Builder()
                .setTask(task)
                .setProcessType(ProcessType.TYPE_UPDATE_TASK)
                .setState(TaskState.STATE_RUNNING)
                .build();
        process(cmd);
    }

    @Override
    public void onFinished(ITask task) {
        //XLogger.getDefault().e(TAG,"finished = " + task);
        TaskCmd cmd = new TaskCmd.Builder()
                .setTask(task)
                .setProcessType(ProcessType.TYPE_UPDATE_TASK)
                .setState(TaskState.STATE_FINISH)
                .build();
        process(cmd);
    }
}
