package com.scott.transer.handler;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.manager.ITaskManager;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-01-19 15:45</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      修改该类用来配置自定义的上传处理器
 * </p>
 */

public class DefaultUploadFactory implements ITaskHandlerFactory {
    private ITaskHandlerCallback callback;

    @Override
    public ITaskHandler create(ITask task, ITaskManager manager) {
        ITaskHandler handler = new DefaultHttpUploadHandler();
        handler.setHandlerListenner(callback);
        handler.setTask(task);
        return handler;
    }

    @Override
    public void setTaskHandlerCallback(ITaskHandlerCallback callback) {
        this.callback = callback;
    }
}
