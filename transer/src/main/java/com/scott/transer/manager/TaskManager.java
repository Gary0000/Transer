package com.scott.transer.manager;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.ITaskCmd;
import com.scott.transer.TaskState;
import com.scott.transer.handler.ITaskHandlerFactory;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.handler.ITaskHolder;
import com.scott.transer.utils.Debugger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 16:13</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class TaskManager implements ITaskManager {

    private ITaskProcessor mProcessorProxy;
    private ITaskProcessCallback mCallback;
    private Map<TaskType,ThreadPoolExecutor> mThreadPool = new HashMap<>();
    private Map<TaskType,ITaskHandlerFactory> mTaskHandlerCreators = new HashMap<>();
    private List<ITaskHolder> mTasks = new ArrayList<>(); //task list
    private final String TAG = TaskManager.class.getSimpleName();


    @Override
    public void process(ITaskCmd cmd) {

        switch (cmd.getProceeType()) {
            case TYPE_ADD_TASKS:
                mProcessorProxy.addTasks(cmd.getTasks());
                break;
            case TYPE_ADD_TASK:
                mProcessorProxy.addTask(cmd.getTask());
                break;
            case TYPE_DELETE_TASK:
                mProcessorProxy.deleteTask(cmd.getTaskId());
                break;
            case TYPE_DELETE_TASKS_SOME:
                mProcessorProxy.deleteTasks(cmd.getTaskIds());
                break;
            case TYPE_DELETE_TASKS_GROUP:
                mProcessorProxy.deleteGroup(cmd.getGroupId());
                break;
            case TYPE_DELETE_TASKS_ALL:
                mProcessorProxy.deleteAll(cmd.getTaskType());
                break;
            case TYPE_DELETE_TASKS_COMPLETED:
                mProcessorProxy.deleteCompleted(cmd.getTaskType());
                break;
            case TYPE_DELETE_TASKS_STATE:
                mProcessorProxy.delete(cmd.getTask().getState(),cmd.getTaskType());
                break;
            case TYPE_QUERY_TASK:
                mProcessorProxy.getTask(cmd.getTaskId());
                break;
            case TYPE_QUERY_TASKS_SOME:
                mProcessorProxy.getTasks(cmd.getTaskIds());
                break;
            case TYPE_QUERY_TASKS_ALL:
                mProcessorProxy.getAllTasks(cmd.getTaskType());
                break;
            case TYPE_QUERY_TASKS_COMPLETED:
                mProcessorProxy.getTasks(TaskState.STATE_FINISH,cmd.getTaskType());
                break;
            case TYPE_QUERY_TASKS_GROUP:
                mProcessorProxy.getGroup(cmd.getGroupId());
                break;
            case TYPE_QUERY_TASKS_STATE:
                mProcessorProxy.getTasks(cmd.getState(),cmd.getTaskType());
                break;
            case TYPE_UPDATE_TASK:
                mProcessorProxy.updateTask(cmd.getTask());
                break;
            case TYPE_UPDATE_TASK_WTIHOUT_SAVE:
                Debugger.error(TAG,"speed = " + cmd.getTask().getSpeed());
                mProcessorProxy.updateTaskWithoutSave(cmd.getTask());
                break;
            case TYPE_START_TASK:
                mProcessorProxy.start(cmd.getTaskId());
                break;
            case TYPE_START_GROUP:
                mProcessorProxy.startGroup(cmd.getGroupId());
                break;
            case TYPE_START_ALL:
                mProcessorProxy.startAll();
                break;
            case TYPE_STOP_TASK:
                mProcessorProxy.stop(cmd.getTaskId());
                break;
            case TYPE_STOP_GROUP:
                mProcessorProxy.stop(cmd.getGroupId());
                break;
            case TYPE_STOP_ALL:
                mProcessorProxy.stopAll();
                break;
        }
        mCallback.onFinished(cmd.getTaskType(),cmd.getProceeType(),null);
    }

    @Override
    public void setTaskProcessor(ITaskProcessor operation) {
        mProcessorProxy = operation;
    }

    @Override
    public void setProcessCallback(ITaskProcessCallback callback) {
        mCallback = callback;
    }

    @Override
    public void setThreadPool(TaskType taskType, ThreadPoolExecutor threadPool) {
        mThreadPool.put(taskType,threadPool);
    }

    @Override
    public ThreadPoolExecutor getTaskThreadPool(TaskType type) {
        return mThreadPool.get(type);
    }

    @Override
    public ITaskHandlerFactory getTaskHandlerCreator(ITask task) {
        return mTaskHandlerCreators.get(task.getType());
    }

    @Override
    public void addHandlerCreator(TaskType type, ITaskHandlerFactory handlerCreator) {
        mTaskHandlerCreators.put(type,handlerCreator);
    }

    @Override
    public List<ITaskHolder> getTasks() {
        return mTasks;
    }

    @Override
    public void setManager(ITaskManager manager) {

    }

    @Override
    public ITaskManager getManager() {
        return this;
    }


}
