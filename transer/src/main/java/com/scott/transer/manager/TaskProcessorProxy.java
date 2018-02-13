package com.scott.transer.manager;

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
        mProcessor.deleteTask(taskId);
        mDbProcessor.deleteTask(taskId);
    }

    @Override
    public void deleteGroup(String groupId) {
        mProcessor.deleteTask(groupId);
        mDbProcessor.deleteTask(groupId);
    }

    @Override
    public void deleteTasks(String[] taskIds) {
        mProcessor.deleteTasks(taskIds);
        mDbProcessor.deleteTasks(taskIds);
    }

    @Override
    public void deleteCompleted(TaskType type) {
        mProcessor.deleteCompleted(type);
        mDbProcessor.deleteCompleted(type);
    }

    @Override
    public void delete(int state,TaskType taskType) {
        mProcessor.delete(state,taskType);
        mDbProcessor.delete(state,taskType);
    }


    @Override
    public void deleteAll(TaskType type) {
        mProcessor.deleteAll(type);
        mDbProcessor.deleteAll(type);
    }

    @Override
    public ITask getTask(String taskId) {
        ITask task = mProcessor.getTask(taskId);
        if(task == null) {
            task = mDbProcessor.getTask(taskId);
            mProcessor.addTask(mDbProcessor.getTask(taskId));
        }
        return task;
    }

    @Override
    public List<ITask> getTasks(String[] taskIds) {
        List<ITask> tasks = mProcessor.getTasks(taskIds);
        if(tasks == null) {
            tasks = mDbProcessor.getTasks(taskIds);
            mProcessor.addTasks(tasks);
        }
        return tasks;
    }

    @Override
    public List<ITask> getGroup(String groupId) {
        List<ITask> tasks = mProcessor.getGroup(groupId);
        if(tasks == null) {
            tasks = mDbProcessor.getGroup(groupId);
            mProcessor.addTasks(tasks);
        }
        return tasks;
    }

    @Override
    public List<ITask> getAllTasks(TaskType taskType) {
        List<ITask> tasks = mProcessor.getAllTasks(taskType);
        if(tasks == null || tasks.isEmpty()) {
            tasks = mDbProcessor.getAllTasks(taskType);
            mProcessor.addTasks(tasks);
        }
        return tasks;
    }

    @Override
    public List<ITask> getTasks(int state,TaskType taskType) {
        List<ITask> tasks = mProcessor.getTasks(state,taskType);
        if(tasks == null) {
            tasks = mDbProcessor.getTasks(state,taskType);
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
        mProcessor.start(taskId);
        mDbProcessor.start(taskId);
    }

    @Override
    public void startGroup(String groupId) {
        mProcessor.start(groupId);
        mDbProcessor.startGroup(groupId);
    }

    @Override
    public void startAll(TaskType taskType) {
        mProcessor.startAll(taskType);
        mDbProcessor.stopAll(taskType);
    }

    @Override
    public void stop(String taskId) {
        mProcessor.stop(taskId);
        mDbProcessor.stop(taskId);
    }

    @Override
    public void stopGroup(String groupId) {
        mProcessor.stopGroup(groupId);
        mDbProcessor.stopGroup(groupId);
    }

    @Override
    public void stopAll(TaskType taskType) {
        mProcessor.stopAll(taskType);
        mDbProcessor.stopAll(taskType);
    }
}
