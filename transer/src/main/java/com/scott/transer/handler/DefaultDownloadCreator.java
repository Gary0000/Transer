package com.scott.transer.handler;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.manager.ITaskManager;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-01-19 15:23</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class DefaultDownloadCreator implements ITaskHandlerCreator {

    private ITaskHandlerCallback callback;

    @Override
    public ITaskHandler create(ITask task, ITaskManager manager) {
        ITaskHandler handler = new DefaultHttpDownloadHandler();
        handler.setThreadPool(manager.getTaskThreadPool(task.getType()));
        handler.setHandlerListenner(callback);

        Map<String,String> params = new HashMap<>();
        params.put("path",task.getName());
        handler.setParams(params);
        handler.setTask(task);
        return handler;
    }

    @Override
    public void setTaskHandlerCallback(ITaskHandlerCallback callback) {
        this.callback = callback;
    }
}
