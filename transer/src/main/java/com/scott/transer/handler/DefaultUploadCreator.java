package com.scott.transer.handler;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.manager.ITaskManager;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-01-19 15:45</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class DefaultUploadCreator implements ITaskHandlerCreator {

    private ITaskHandlerCallback callback;

    @Override
    public ITaskHandler create(ITask task, ITaskManager manager) {
        ITaskHandler handler = new DefaultHttpUploadHandler();
        handler.setHandlerListenner(callback);
        handler.setThreadPool(manager.getTaskThreadPool(task.getType()));
        handler.setTask(task);
        return handler;
    }

    @Override
    public void setTaskHandlerCallback(ITaskHandlerCallback callback) {
        this.callback = callback;
    }
}
