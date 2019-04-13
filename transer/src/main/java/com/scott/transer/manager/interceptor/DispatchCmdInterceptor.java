package com.scott.transer.manager.interceptor;

import com.scott.transer.TaskCmd;
import com.scott.transer.TaskState;
import com.scott.transer.manager.ITaskInternalProcessor;
import com.scott.transer.manager.ITaskProcessCallback;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-04-17 13:10</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */
public class DispatchCmdInterceptor implements Interceptor {
    private ITaskInternalProcessor mProcessorProxy;

    public DispatchCmdInterceptor(ITaskInternalProcessor processor) {
        mProcessorProxy = processor;
    }

    @Override
    public TaskCmd intercept(Chain chain, TaskCmd cmd) {
        return dispatch(cmd);
    }

    private TaskCmd dispatch(TaskCmd cmd) {
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
                mProcessorProxy.deleteGroup(cmd.getGroupId(),cmd.getUserId());
                break;
            case TYPE_DELETE_TASKS_ALL:
                mProcessorProxy.deleteAll(cmd.getTaskType(),cmd.getUserId());
                break;
            case TYPE_DELETE_TASKS_COMPLETED:
                mProcessorProxy.deleteCompleted(cmd.getTaskType(),cmd.getUserId(), cmd.getGroupId());
                break;
            case TYPE_DELETE_TASKS_STATE:
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
                mProcessorProxy.getAllTasks(cmd.getTaskType(),cmd.getUserId());
                break;
            case TYPE_QUERY_TASKS_COMPLETED:
                mProcessorProxy.getTasks(TaskState.STATE_FINISH,cmd.getTaskType(),cmd.getUserId());
                break;
            case TYPE_QUERY_TASKS_GROUP:
                mProcessorProxy.getGroup(cmd.getGroupId(),cmd.getUserId());
                break;
            case TYPE_QUERY_TASKS_STATE:
                mProcessorProxy.getTasks(cmd.getState(),cmd.getTaskType()
                        ,cmd.getUserId());
                break;
            case TYPE_UPDATE_TASK:
                mProcessorProxy.updateTask(cmd.getTask());
                break;
            case TYPE_UPDATE_TASK_WTIHOUT_SAVE:
                mProcessorProxy.updateTaskWithoutSave(cmd.getTask());
                break;
            case TYPE_START_TASK:
                mProcessorProxy.start(cmd.getTaskId());
                break;
            case TYPE_START_GROUP:
                mProcessorProxy.startGroup(cmd.getGroupId(),cmd.getUserId());
                break;
            case TYPE_START_ALL:
                mProcessorProxy.startAll(cmd.getTaskType(),cmd.getUserId());
                break;
            case TYPE_STOP_TASK:
                mProcessorProxy.stop(cmd.getTaskId());
                break;
            case TYPE_STOP_GROUP:
                mProcessorProxy.stopGroup(cmd.getGroupId(),cmd.getUserId());
                break;
            case TYPE_STOP_ALL:
                mProcessorProxy.stopAll(cmd.getTaskType(),cmd.getUserId());
                break;
        }
        return cmd;
    }
}
