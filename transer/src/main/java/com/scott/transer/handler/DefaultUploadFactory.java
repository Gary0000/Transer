package com.scott.transer.handler;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.manager.ITaskManager;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-01-19 15:45</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      修改该类用来配置自定义的上传处理器
 * </p>
 */

public class DefaultUploadFactory extends AbsHandlerFactory {

    @Override
    protected ITaskHandler create(ITask task) {
        return new DefaultHttpUploadHandler.Builder()
                .addHeader("path",task.getDestPath() + "/")
                .build();
    }
}
