package com.scott.transer.manager;

import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.ITask;
import com.scott.transer.TaskCmd;
import com.scott.transer.TaskState;
import com.scott.transer.handler.ITaskHandlerCallback;
import com.scott.transer.handler.ITaskHandlerFactory;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.handler.ITaskHolder;
import com.scott.transer.utils.Debugger;

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
 *      manager 责任连的顶端，当前只有 TaskManagerProxy, TaskManager 两个节点
 * </p>
 */

public class TaskManagerProxy implements ITaskManager, ITaskProcessCallback,ITaskHandlerCallback {

    private ITaskManager mManager;
    private ITaskInternalProcessor mProcessor; //processor proxy
    private ExecutorService mCmdThreadPool; //cmd thread pool
    private ITaskProcessCallback mProcessCallback;
    private final String TAG = TaskManagerProxy.class.getSimpleName();

    public TaskManagerProxy() {
        mCmdThreadPool = Executors.newSingleThreadExecutor();
    }

    @Override
    public void setManager(ITaskManager manager) {
        mManager = manager;
        mManager.setProcessCallback(this);
        mManager.setTaskProcessor(mProcessor);
        mProcessor.setTaskManager(this);
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
                    mManager.process(cmd);
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
    public void onFinished(TaskType taskType, ProcessType processType, List<ITask> tasks) {
        List<ITask> taskList = new ArrayList<>();
        for(ITaskHolder holder : mManager.getTasks()) {
            if(taskType == holder.getType()) {
                taskList.add(holder.getTask());
            }
        }

        //copy
//        ITask[] objects = (ITask[]) taskList.toArray();
//        ITask[] objects1 = Arrays.copyOf(objects, objects.length);
//        tasks = Arrays.asList(objects1);
        mProcessCallback.onFinished(taskType,processType,taskList);
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
        //Debugger.error(TAG,"start = " + params);
        TaskCmd cmd = new TaskCmd.Builder()
                .setTask(task)
                .setProcessType(ProcessType.TYPE_UPDATE_TASK)
                .build();
        process(cmd);
    }

    @Override
    public void onStop(ITask task) {
        //Debugger.error(TAG,"stop = " + params);
        TaskCmd cmd = new TaskCmd.Builder()
                .setTask(task)
                .setProcessType(ProcessType.TYPE_UPDATE_TASK)
                .build();
        process(cmd);
    }

    @Override
    public void onError(int code, ITask task) {
        //Debugger.error(TAG,"error = " + params);
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
        Debugger.error(TAG,"speed == " + task.getSpeed());
    }

    @Override
    public void onPiceSuccessful(ITask task) {
        Debugger.error(TAG," PICE STATE = " + task.getState());
        TaskCmd cmd = new TaskCmd.Builder()
                .setTask(task)
                .setProcessType(ProcessType.TYPE_UPDATE_TASK)
                .setState(TaskState.STATE_RUNNING)
                .build();
        process(cmd);
    }

    @Override
    public void onFinished(ITask task) {
        //Debugger.error(TAG,"finished = " + task);
        TaskCmd cmd = new TaskCmd.Builder()
                .setTask(task)
                .setProcessType(ProcessType.TYPE_UPDATE_TASK)
                .setState(TaskState.STATE_FINISH)
                .build();
        process(cmd);
    }

}
