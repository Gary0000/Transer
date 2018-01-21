package com.scott.transer.handler;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.manager.ITaskManager;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-01-19 15:21</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      ITaskHandler 的抽象工厂，实现类可以自定义创建一个 ITaskHandler
 * </p>
 */

public interface ITaskHandlerFactory {

    /**
     * 创建一个ITaskHandler 用于传输任务
     * @param task 任务信息
     * @param manager manager
     * @return
     */
    ITaskHandler create(ITask task, ITaskManager manager);

    void setTaskHandlerCallback(ITaskHandlerCallback callback);
}
