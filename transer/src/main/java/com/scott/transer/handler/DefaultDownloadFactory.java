package com.scott.transer.handler;

import android.text.TextUtils;

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

public class DefaultDownloadFactory extends AbsHandlerFactory {

    @Override
    protected ITaskHandler create(ITask task) {
        return new DefaultHttpDownloadHandler.Builder()
                .addParam("path",task.getName())
                .addParam("root",task.getSourceUrl())
                .build();
    }
}
