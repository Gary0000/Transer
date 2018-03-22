package com.scott.transer.manager;

import android.text.TextUtils;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.transer.TaskCmd;
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

    private ITaskInternalProcessor mProcessorProxy;
    private ITaskProcessCallback mCallback;
    private Map<TaskType,ThreadPoolExecutor> mThreadPool = new HashMap<>();
    private Map<TaskType,ITaskHandlerFactory> mTaskHandlerCreators = new HashMap<>();
    private List<ITaskHolder> mTasks = new ArrayList<>(); //task list
    private final String TAG = TaskManager.class.getSimpleName();

    private void throwArgException(ProcessType type,String... args) {
        if(args == null || args.length == 0) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        for(String arg : args) {
            builder.append(arg + " ");
        }
        throw new IllegalArgumentException("current process type is " + type + "," + builder + "can not be a null value!");
    }

    @Override
    public void process(TaskCmd cmd) {

        switch (cmd.getProceeType()) {
            case TYPE_ADD_TASKS:
                mProcessorProxy.addTasks(cmd.getTasks());
                break;
            case TYPE_ADD_TASK:
                mProcessorProxy.addTask(cmd.getTask());
                break;
            case TYPE_DELETE_TASK:
                if(TextUtils.isEmpty(cmd.getTaskId())) {
                    throwArgException(ProcessType.TYPE_DELETE_TASK,"taskId");
                }
                mProcessorProxy.deleteTask(cmd.getTaskId());
                break;
            case TYPE_DELETE_TASKS_SOME:
                mProcessorProxy.deleteTasks(cmd.getTaskIds());
                break;
            case TYPE_DELETE_TASKS_GROUP:
                if(TextUtils.isEmpty(cmd.getGroupId())) {
                    throwArgException(ProcessType.TYPE_DELETE_TASKS_GROUP,"groupId");
                }
                if(TextUtils.isEmpty(cmd.getUserId())) {
                    throwArgException(ProcessType.TYPE_DELETE_TASKS_GROUP,"userId");
                }
                mProcessorProxy.deleteGroup(cmd.getGroupId(),cmd.getUserId());
                break;
            case TYPE_DELETE_TASKS_ALL:
                if(TextUtils.isEmpty(cmd.getUserId())) {
                    throwArgException(ProcessType.TYPE_DELETE_TASKS_ALL,"userId");
                }
                mProcessorProxy.deleteAll(cmd.getTaskType(),cmd.getUserId());
                break;
            case TYPE_DELETE_TASKS_COMPLETED:
                if(TextUtils.isEmpty(cmd.getUserId())) {
                    throwArgException(ProcessType.TYPE_DELETE_TASKS_COMPLETED,"userId");
                }
                mProcessorProxy.deleteCompleted(cmd.getTaskType(),cmd.getUserId());
                break;
            case TYPE_DELETE_TASKS_STATE:
                if(TextUtils.isEmpty(cmd.getUserId())) {
                    throwArgException(ProcessType.TYPE_DELETE_TASKS_STATE,"userId");
                }
                mProcessorProxy.delete(cmd.getTask().getState(),
                        cmd.getTaskType(),cmd.getUserId());
                break;
            case TYPE_QUERY_TASK:
                mProcessorProxy.getTask(cmd.getTaskId());
                break;
            case TYPE_QUERY_TASKS_SOME:
                mProcessorProxy.getTasks(cmd.getTaskIds());
                break;
            case TYPE_QUERY_TASKS_ALL:
                if(TextUtils.isEmpty(cmd.getUserId())) {
                    throwArgException(ProcessType.TYPE_QUERY_TASKS_ALL,"userId");
                }
                mProcessorProxy.getAllTasks(cmd.getTaskType(),cmd.getUserId());
                break;
            case TYPE_QUERY_TASKS_COMPLETED:
                if(TextUtils.isEmpty(cmd.getUserId())) {
                    throwArgException(ProcessType.TYPE_QUERY_TASKS_COMPLETED,"userId");
                }
                mProcessorProxy.getTasks(TaskState.STATE_FINISH,cmd.getTaskType(),cmd.getUserId());
                break;
            case TYPE_QUERY_TASKS_GROUP:
                if(TextUtils.isEmpty(cmd.getUserId())) {
                    throwArgException(ProcessType.TYPE_QUERY_TASKS_GROUP,"userId");
                }
                if(TextUtils.isEmpty(cmd.getGroupId())) {
                    throwArgException(ProcessType.TYPE_QUERY_TASKS_GROUP,"groupId");
                }
                mProcessorProxy.getGroup(cmd.getGroupId(),cmd.getUserId());
                break;
            case TYPE_QUERY_TASKS_STATE:
                if(TextUtils.isEmpty(cmd.getUserId())) {
                    throwArgException(ProcessType.TYPE_QUERY_TASKS_STATE,"userId");
                }
                mProcessorProxy.getTasks(cmd.getState(),cmd.getTaskType()
                        ,cmd.getUserId());
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
                if(TextUtils.isEmpty(cmd.getUserId())) {
                    throwArgException(ProcessType.TYPE_START_GROUP,"userId");
                }
                if(TextUtils.isEmpty(cmd.getGroupId())) {
                    throwArgException(ProcessType.TYPE_START_GROUP,"groupId");
                }
                mProcessorProxy.startGroup(cmd.getGroupId(),cmd.getUserId());
                break;
            case TYPE_START_ALL:
                if(TextUtils.isEmpty(cmd.getUserId())) {
                    throwArgException(ProcessType.TYPE_START_ALL,"userId");
                }
                mProcessorProxy.startAll(cmd.getTaskType(),cmd.getUserId());
                break;
            case TYPE_STOP_TASK:
                mProcessorProxy.stop(cmd.getTaskId());
                break;
            case TYPE_STOP_GROUP:
                mProcessorProxy.stop(cmd.getGroupId());
                break;
            case TYPE_STOP_ALL:
                if(TextUtils.isEmpty(cmd.getUserId())) {
                    throwArgException(ProcessType.TYPE_STOP_ALL,"userId");
                }
                mProcessorProxy.stopAll(cmd.getTaskType(),cmd.getUserId());
                break;
        }
//        if(TextUtils.isEmpty(cmd.getUserId())) {
//            throwArgException(cmd.getProceeType(), "userId");
//        }
        mCallback.onFinished(cmd.getUserId(),cmd.getTaskType(),cmd.getProceeType(),null);
    }

    @Override
    public void setTaskProcessor(ITaskInternalProcessor operation) {
        mProcessorProxy = operation;
    }

    @Override
    public void setProcessCallback(ITaskProcessCallback callback) {
        mCallback = callback;
    }

    @Override
    public void addThreadPool(TaskType taskType, ThreadPoolExecutor threadPool) {
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
