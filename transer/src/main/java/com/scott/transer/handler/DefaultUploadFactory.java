package com.scott.transer.handler;

import com.scott.annotionprocessor.ITask;

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

        if(task.getLength() > BaseTaskHandler.SPEED_LIMIT_SIZE.SPEED_2MB) {
            return new DefaultHttpUploadHandler.Builder()
                    .disableAutoRetry()
                    .addHeader("path", task.getDestPath() + "/")
                    .build();
        } else {
            return new DefaultFormPartUploadHandler.Builder()
                    .addParam("path",task.getDestPath() + "/")
                    .build();
        }
    }
}
