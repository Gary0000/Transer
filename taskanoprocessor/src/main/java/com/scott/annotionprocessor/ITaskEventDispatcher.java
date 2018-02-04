package com.scott.annotionprocessor;


import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-14 16:41</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      用于分发事件
 * </p>
 */

public interface ITaskEventDispatcher {

    /***
     * 该方法有事件的被订阅着调用，订阅着需要增加一个方法去接收事件 接收事件的方法需要加 @TaskScriber 注解
     * {@link TaskSubscriber}
     * @param taskType
     * @param type
     * @param taskList
     */
    void dispatchTasks(TaskType taskType, ProcessType type,List<ITask> taskList);
}
