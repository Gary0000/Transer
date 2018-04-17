package com.scott.transer.manager;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.Task;
import com.scott.transer.TaskState;
import com.scott.transer.TaskTypeConverter;
import com.scott.transer.dao.DaoHelper;
import com.scott.transer.dao.TaskDao;


import java.util.ArrayList;
import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 15:39</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class TaskDbProcessor implements ITaskInternalProcessor {

    private TaskDao mTaskDao;
    public TaskDbProcessor() {
        mTaskDao = DaoHelper.getDbSession().getTaskDao();
    }

    @Override
    public void setTaskManager(ITaskManager manager) {

    }

    @Override
    public void addTask(ITask task) {
        mTaskDao.insert((Task) task);
    }

    @Override
    public void addTasks(List tasks) {
        mTaskDao.insertInTx(tasks);
    }

    @Override
    public void deleteTask(String taskId) {
        Task task = mTaskDao
                .queryBuilder()
                .where(TaskDao.Properties.TaskId.eq(taskId))
                .unique();
        mTaskDao.delete(task);
    }

    @Override
    public void deleteGroup(String groupId,String userId) {
        List<Task> tasks = mTaskDao
                .queryBuilder()
                .where(TaskDao.Properties.GroupId.eq(groupId))
                .where(TaskDao.Properties.UserId.eq(userId))
                .list();
        mTaskDao.deleteInTx(tasks);
    }

    @Override
    public void deleteTasks(String[] taskIds) {
//        List<Task> tasks = mTaskDao
//                .queryBuilder()
//                .where(TaskDao.Properties.TaskId.in(taskIds))
//                .list();
//        mTaskDao.deleteInTx(tasks);
    }

    @Override
    public void deleteCompleted(TaskType type,String userId) {
        List<Task> tasks = mTaskDao
                .queryBuilder()
                .where(TaskDao.Properties.State.eq(TaskState.STATE_FINISH))
                .where(TaskDao.Properties.Type.eq(type))
                .where(TaskDao.Properties.UserId.eq(userId))
                .list();
        mTaskDao.deleteInTx(tasks);
    }

    @Override
    public void delete(int state,TaskType type,String userId) {
        List<Task> tasks = mTaskDao
                .queryBuilder()
                .where(TaskDao.Properties.State.eq(state))
                .where(TaskDao.Properties.Type.eq(type))
                .where(TaskDao.Properties.UserId.eq(userId))
                .list();
        mTaskDao.deleteInTx(tasks);
    }

    @Override
    public void deleteAll(TaskType type,String userId) {
        TaskTypeConverter converter = new TaskTypeConverter();
        List<Task> tasks = mTaskDao
                .queryBuilder()
                .where(TaskDao.Properties.Type.eq(converter.convertToDatabaseValue(type)))
                .where(TaskDao.Properties.UserId.eq(userId))
                .list();
        mTaskDao.deleteInTx(tasks);
    }

    @Override
    public ITask getTask(String taskId) {
        ITask task = mTaskDao.queryBuilder()
                .where(TaskDao.Properties.TaskId.eq(taskId))
                .unique();
        return task;
    }

    @Override
    public List<ITask> getTasks(String[] taskIds) {
//        List<Task> tasks = mTaskDao
//                .queryBuilder()
//                .where(TaskDao.Properties.TaskId.in(taskIds))
//                .list();
//        List<ITask> tasks1 = new ArrayList<>();
//        tasks1.addAll(tasks);
        return null;
    }

    @Override
    public List<ITask> getGroup(String groupId,String userId) {
        List<Task> tasks = mTaskDao
                .queryBuilder()
                .where(TaskDao.Properties.GroupId.eq(groupId))
                .where(TaskDao.Properties.UserId.eq(userId))
                .list();
        List<ITask> tasks1 = new ArrayList<>();
        tasks1.addAll(tasks);
        return tasks1;
    }

    @Override
    public List<ITask> getAllTasks(TaskType type,String userId) {

        TaskTypeConverter converter = new TaskTypeConverter();
        int nType = converter.convertToDatabaseValue(type);

        List<Task> tasks = mTaskDao
                .queryBuilder()
                .where(TaskDao.Properties.Type.eq(nType))
                .where(TaskDao.Properties.UserId.eq(userId))
                .list();
        List<ITask> tasks1 = new ArrayList<>();
        tasks1.addAll(tasks);
        return tasks1;
    }

    @Override
    public List<ITask> getTasks(int state,TaskType type,String userId) {
        List<Task> tasks = mTaskDao
                .queryBuilder()
                .where(TaskDao.Properties.State.eq(state))
                .where(TaskDao.Properties.Type.eq(type))
                .where(TaskDao.Properties.UserId.eq(userId))
                .list();
        List<ITask> tasks1 = new ArrayList<>();
        tasks1.addAll(tasks);
        return tasks1;
    }

    @Override
    public void updateTask(ITask task) {
        mTaskDao.update((Task) task);
    }

    @Override
    public void updateTaskWithoutSave(ITask task) {

    }

    @Override
    public void start(String taskId) {
        Task task = mTaskDao.queryBuilder()
                .where(TaskDao.Properties.TaskId.eq(taskId))
                .unique();
        task.setState(TaskState.STATE_READY);
        mTaskDao.update(task);
    }

    @Override
    public void startGroup(String groupId,String userId) {
        List<Task> list = mTaskDao.queryBuilder()
                .where(TaskDao.Properties.TaskId.eq(groupId))
                .where(TaskDao.Properties.UserId.eq(userId))
                .list();
        for(Task task : list) {
            task.setState(TaskState.STATE_READY);
        }
        mTaskDao.updateInTx(list);
    }

    @Override
    public void startAll(TaskType taskType,String userId) {
        TaskTypeConverter converter = new TaskTypeConverter();
        int nType = converter.convertToDatabaseValue(taskType);
        List<Task> list = mTaskDao.queryBuilder()
                .where(TaskDao.Properties.Type.eq(nType))
                .where(TaskDao.Properties.UserId.eq(userId))
                .list();
        for(Task task : list) {
            task.setState(TaskState.STATE_READY);
        }
        mTaskDao.updateInTx(list);
    }

    @Override
    public void stop(String taskId) {
        Task task = mTaskDao.queryBuilder()
                .where(TaskDao.Properties.TaskId.eq(taskId))
                .unique();
        if(task == null) {
            return;
        }
        task.setState(TaskState.STATE_STOP);
        mTaskDao.update(task);
    }

    @Override
    public void stopGroup(String groupId,String userId) {
        List<Task> list = mTaskDao.queryBuilder()
                .where(TaskDao.Properties.TaskId.eq(groupId))
                .where(TaskDao.Properties.UserId.eq(userId))
                .list();
        for(Task task : list) {
            task.setState(TaskState.STATE_STOP);
        }
        mTaskDao.updateInTx(list);
    }

    @Override
    public void stopAll(TaskType taskType,String userId) {
        TaskTypeConverter converter = new TaskTypeConverter();
        int nType = converter.convertToDatabaseValue(taskType);
        List<Task> list = mTaskDao.queryBuilder()
                .where(TaskDao.Properties.Type.eq(nType))
                .where(TaskDao.Properties.UserId.eq(userId))
                .list();
        for(Task task : list) {
            task.setState(TaskState.STATE_STOP);
        }
        mTaskDao.updateInTx(list);
    }
}
