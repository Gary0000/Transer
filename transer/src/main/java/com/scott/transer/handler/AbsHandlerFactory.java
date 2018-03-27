package com.scott.transer.handler;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.manager.ITaskManager;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-27 14:45</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *
 *     用于创建自定义的handler，例如设置通用请求头，请求参数，baseUrl等
 * </p>
 */

public abstract class AbsHandlerFactory implements ITaskHandlerFactory{

    private ITaskHandlerCallback callback;

    @Override
    public ITaskHandler create(ITask task, ITaskManager manager) {
        ITaskHandler handler = create(task);
        handler.setHandlerListenner(callback);
        handler.setTask(task);
        handler.setThreadPool(manager.getTaskThreadPool(task.getType()));
        return handler;
    }

    @Override
    public void setTaskHandlerCallback(ITaskHandlerCallback callback) {
        this.callback = callback;
    }

    /**
     *  子类实现该类用于创建自定义的Handler
     * @param task
     * @return
     */
    protected abstract ITaskHandler create(ITask task);
}
