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
import com.scott.transer.manager.interceptor.ReNameDownloadFileInterceptor;
import com.scott.transer.manager.interceptor.ChainImpl;
import com.scott.transer.manager.interceptor.CheckParamInterceptor;
import com.scott.transer.manager.interceptor.ICmdInterceptor;
import com.shilec.xlogger.XLogger;

import java.util.ArrayList;
import java.util.List;
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

public class TaskManagerProxy implements ITaskManager, ITaskProcessCallback,ITaskHandlerCallback {

    private ITaskManager mManager;
    private ITaskInternalProcessor mProcessor; //processor proxy
    private ExecutorService mCmdThreadPool; //cmd thread pool
    private ITaskProcessCallback mProcessCallback;
    private final String TAG = TaskManagerProxy.class.getSimpleName();
    private List<ICmdInterceptor> mInterceptors = new ArrayList<>();

    public void addInterceptor(ICmdInterceptor interceptor) {
        synchronized (mInterceptors) {
            mInterceptors.add(interceptor);
        }
    }

    public TaskManagerProxy() {
        mCmdThreadPool = Executors.newSingleThreadExecutor();
        mInterceptors.add(new CheckParamInterceptor());
        mInterceptors.add(new ReNameDownloadFileInterceptor(this));
    }

    private void interceptCmd(TaskCmd cmd) {
        ICmdInterceptor.Chain chain = new ChainImpl(0,mInterceptors);
        TaskCmd process = chain.process(cmd);
        mManager.process(process);
    }

    @Override
    public void setManager(ITaskManager manager) {
        mManager = manager;
        mProcessor.setTaskManager(this);
        mManager.setProcessCallback(this);
        mManager.setTaskProcessor(mProcessor);
    }

    @Override
    public ITaskManager getManager() {
        return this;
    }

    @Override
    public void process(final TaskCmd cmd) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (mProcessor) {
                    interceptCmd(cmd);
                }
            }
        };
        mCmdThreadPool.execute(runnable);
    }


    @Override
    public void setTaskProcessor(ITaskInternalProcessor operation) {
        mProcessor = operation;
    }

    @Override
    public void setProcessCallback(ITaskProcessCallback callback) {
        mProcessCallback = callback;
    }

    @Override
    public void addThreadPool(TaskType taskType, ThreadPoolExecutor threadPool) {
        mManager.addThreadPool(taskType,threadPool);
    }


    @Override
    public ThreadPoolExecutor getTaskThreadPool(TaskType type) {
        return mManager.getTaskThreadPool(type);
    }

    @Override
    public ITaskHandlerFactory getTaskHandlerCreator(ITask task) {
        ITaskHandlerFactory creator = mManager.getTaskHandlerCreator(task);
        creator.setTaskHandlerCallback(this);
        return creator;
    }

    @Override
    public void addHandlerCreator(TaskType type, ITaskHandlerFactory handlerCreator) {
        mManager.addHandlerCreator(type,handlerCreator);
    }


    @Override
    public List<ITaskHolder> getTasks() {
        return mManager.getTasks();
    }


    @Override
    public void onFinished(String userId,TaskType taskType, ProcessType processType, List<ITask> tasks) {
        List<ITask> taskList = new ArrayList<>();
        for(ITaskHolder holder : mManager.getTasks()) {
            //handler callback 的 task 不携带 userId
            if(userId != null) {
                if(taskType == holder.getType() &&
                        TextUtils.equals(userId,holder.getTask().getUserId())) {
                    taskList.add(holder.getTask());
                }
            } else {
                if(taskType == holder.getType()) {
                    taskList.add(holder.getTask());
                }
            }
        }

        //copy
//        ITask[] objects = (ITask[]) taskList.toArray();
//        ITask[] objects1 = Arrays.copyOf(objects, objects.length);
//        tasks = Arrays.asList(objects1);
        mProcessCallback.onFinished(userId,taskType,processType,taskList);
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
