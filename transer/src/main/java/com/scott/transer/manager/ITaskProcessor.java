package com.scott.transer.manager;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;

import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-02-11 21:14</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *     taskId 作为主键，任何时候都唯一
 * </p>
 */

public interface ITaskProcessor {
    void addTasks(List<ITask> tasks);

    void addTask(ITask task);

    void deleteTask(String taskId);

    void deleteGroup(String groupId,String userId);

    void deleteTasks(String[] taskIds);

    void deleteCompleted(TaskType type,String userId);

    void delete(int state,TaskType type,String userId);

    void deleteAll(TaskType type,String userId);

    ITask getTask(String taskId);

    List<ITask> getTasks(String[] taskIds);

    List<ITask> getGroup(String groupId,String userId);

    List<ITask> getAllTasks(TaskType type,String userId);

    List<ITask> getTasks(int state, TaskType type,String userId);

    void start(String taskId);

    void startGroup(String groupId,String userId);

    void startAll(TaskType taskType,String userId);

    void stop(String taskId);

    void stopGroup(String groupId,String userId);

    void stopAll(TaskType taskType,String userId);
}
