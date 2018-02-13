package com.scott.transer.manager;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;

import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-02-11 21:14</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public interface ITaskProcessor {
    void addTasks(List<ITask> tasks);

    void addTask(ITask task);

    void deleteTask(String taskId);

    void deleteGroup(String groupId);

    void deleteTasks(String[] taskIds);

    void deleteCompleted(TaskType type);

    void delete(int state,TaskType type);

    void deleteAll(TaskType type);

    ITask getTask(String taskId);

    List<ITask> getTasks(String[] taskIds);

    List<ITask> getGroup(String groupId);

    List<ITask> getAllTasks(TaskType type);

    List<ITask> getTasks(int state, TaskType type);

    void start(String taskId);

    void startGroup(String groupId);

    void startAll(TaskType taskType);

    void stop(String taskId);

    void stopGroup(String groupId);

    void stopAll(TaskType taskType);
}
