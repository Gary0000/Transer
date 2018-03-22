package com.scott.transer.manager;

import android.text.TextUtils;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;

import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 15:18</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class TaskProcessorProxy implements ITaskInternalProcessor {

    ITaskInternalProcessor mProcessor;
    ITaskInternalProcessor mDbProcessor;

    public TaskProcessorProxy(ITaskInternalProcessor taskProcessor, ITaskInternalProcessor dbProcessor) {
        mProcessor = taskProcessor;
        mDbProcessor = dbProcessor;
    }

    @Override
    public void setTaskManager(ITaskManager manager) {
        mProcessor.setTaskManager(manager);
        mDbProcessor.setTaskManager(null);
    }

    @Override
    public void addTask(ITask task) {
        mProcessor.addTask(task);
        mDbProcessor.addTask(task);
    }

    @Override
    public void addTasks(List<ITask> tasks) {
        mProcessor.addTasks(tasks);
        mDbProcessor.addTasks(tasks);
    }

    @Override
    public void deleteTask(String taskId) {
        if(TextUtils.isEmpty(taskId)) {
            throw new IllegalArgumentException("if you want to use " +
                    "deleteTask by taskId,the task id can not be a null value!");
        }
        mProcessor.deleteTask(taskId);
        mDbProcessor.deleteTask(taskId);
    }

    @Override
    public void deleteGroup(String groupId,String userId) {
        if(TextUtils.isEmpty(groupId)) {
            throw new IllegalArgumentException("if you want to use " +
                    "deleteGroup by groupId,the group id can not be a null value!");
        }
        mProcessor.deleteGroup(groupId,userId);
        mDbProcessor.deleteGroup(groupId,userId);
    }

    @Override
    public void deleteTasks(String[] taskIds) {
        if(taskIds == null || taskIds.length == 0) {
            throw new IllegalArgumentException("if you want to use " +
                    "deleteTasks by taskIds,the task ids can not be a null value!");
        }
        mProcessor.deleteTasks(taskIds);
        mDbProcessor.deleteTasks(taskIds);
    }

    @Override
    public void deleteCompleted(TaskType type,String userId) {
        mProcessor.deleteCompleted(type,userId);
        mDbProcessor.deleteCompleted(type,userId);
    }

    @Override
    public void delete(int state,TaskType taskType,String userId) {
        mProcessor.delete(state,taskType,userId);
        mDbProcessor.delete(state,taskType,userId);
    }


    @Override
    public void deleteAll(TaskType type,String userId) {
        mProcessor.deleteAll(type,userId);
        mDbProcessor.deleteAll(type,userId);
    }

    @Override
    public ITask getTask(String taskId) {
        if(TextUtils.isEmpty(taskId)) {
            throw new IllegalArgumentException("if you want to use " +
                    "getTask by taskId,the task id can not be a null value!");
        }
        ITask task = mProcessor.getTask(taskId);
        if(task == null) {
            task = mDbProcessor.getTask(taskId);
            mProcessor.addTask(mDbProcessor.getTask(taskId));
        }
        return task;
    }

    @Override
    public List<ITask> getTasks(String[] taskIds) {
        if(taskIds == null || taskIds.length == 0) {
            throw new IllegalArgumentException("if you want to use " +
                    "deleteTask by taskId,the task id can not be a null value!");
        }
        List<ITask> tasks = mProcessor.getTasks(taskIds);
        if(tasks == null || tasks.isEmpty()) {
            tasks = mDbProcessor.getTasks(taskIds);
            mProcessor.addTasks(tasks);
        }
        return tasks;
    }

    @Override
    public List<ITask> getGroup(String groupId,String userId) {
        if(TextUtils.isEmpty(groupId)) {
            throw new IllegalArgumentException("if you want to use " +
                    "getGroup by groupId,the group id can not be a null value!");
        }
        List<ITask> tasks = mProcessor.getGroup(groupId,userId);
        if(tasks == null || tasks.isEmpty()) {
            tasks = mDbProcessor.getGroup(groupId,userId);
            mProcessor.addTasks(tasks);
        }
        return tasks;
    }

    @Override
    public List<ITask> getAllTasks(TaskType taskType,String userId) {
        List<ITask> tasks = mProcessor.getAllTasks(taskType,userId);
        if(tasks == null || tasks.isEmpty()) {
            tasks = mDbProcessor.getAllTasks(taskType,userId);
            mProcessor.addTasks(tasks);
        }
        return tasks;
    }

    @Override
    public List<ITask> getTasks(int state,TaskType taskType,String userId) {
        List<ITask> tasks = mProcessor.getTasks(state,taskType,userId);
        if(tasks == null || tasks.isEmpty()) {
            tasks = mDbProcessor.getTasks(state,taskType,userId);
            mProcessor.addTasks(tasks);
        }
        return tasks;
    }


    @Override
    public void updateTask(ITask task) {
        mProcessor.updateTask(task);
        mDbProcessor.updateTask(task);
    }

    @Override
    public void updateTaskWithoutSave(ITask task) {
        mProcessor.updateTaskWithoutSave(task);
    }

    @Override
    public void start(String taskId) {
        if(TextUtils.isEmpty(taskId)) {
            throw new IllegalArgumentException("if you want to use" +
                    "start by taskId,the task id can not be a null value!");
        }
        mProcessor.start(taskId);
        mDbProcessor.start(taskId);
    }

    @Override
    public void startGroup(String groupId,String userId) {
        if(TextUtils.isEmpty(groupId)) {
            throw new IllegalArgumentException("if you want to use" +
                    "start by groupId,the task id can not be a null value!");
        }
        mProcessor.startGroup(groupId,userId);
        mDbProcessor.startGroup(groupId,userId);
    }

    @Override
    public void startAll(TaskType taskType,String userId) {
        mProcessor.startAll(taskType,userId);
        mDbProcessor.stopAll(taskType,userId);
    }

    @Override
    public void stop(String taskId) {
        if(TextUtils.isEmpty(taskId)) {
            throw new IllegalArgumentException("if you want to use" +
                    "stop by taskId,the task id can not be a null value!");
        }
        mProcessor.stop(taskId);
        mDbProcessor.stop(taskId);
    }

    @Override
    public void stopGroup(String groupId,String userId) {
        if(TextUtils.isEmpty(groupId)) {
            throw new IllegalArgumentException("if you want to use" +
                    "stop by groupId,the group id can not be a null value!");
        }
        mProcessor.stopGroup(groupId,userId);
        mDbProcessor.stopGroup(groupId,userId);
    }

    @Override
    public void stopAll(TaskType taskType,String userId) {
        mProcessor.stopAll(taskType,userId);
        mDbProcessor.stopAll(taskType,userId);
    }
}
