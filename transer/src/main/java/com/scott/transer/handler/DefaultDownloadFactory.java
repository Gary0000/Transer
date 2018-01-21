package com.scott.transer.handler;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.manager.ITaskManager;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-01-19 15:23</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      可以修改该类中的参数，配置下载处理器
 * </p>
 */

public class DefaultDownloadFactory implements ITaskHandlerFactory {
    private ITaskHandlerCallback callback;

    @Override
    public ITaskHandler create(ITask task, ITaskManager manager) {
        ITaskHandler handler = new DefaultHttpDownloadHandler();
        handler.setThreadPool(manager.getTaskThreadPool(task.getType()));
        handler.setHandlerListenner(callback);
        handler.setTask(task);

        Map<String,String> params = new HashMap<>();
        params.put("path",task.getName());
        handler.setParams(params);
        return handler;
    }

    @Override
    public void setTaskHandlerCallback(ITaskHandlerCallback callback) {
        this.callback = callback;
    }
}
