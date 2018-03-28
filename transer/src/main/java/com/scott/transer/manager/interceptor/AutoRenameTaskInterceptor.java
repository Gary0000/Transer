package com.scott.transer.manager.interceptor;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.TaskCmd;
import com.scott.transer.manager.ITaskManager;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-28 11:35</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      下载重命名的拦截器
 * </p>
 */

public class AutoRenameTaskInterceptor implements ICmdInterceptor {

    private ITaskManager mTaskManager;

    public AutoRenameTaskInterceptor(ITaskManager taskManager) {
        mTaskManager = taskManager;
    }

    @Override
    public TaskCmd intercept(Chain chain, TaskCmd cmd) {

        return chain.process(cmd);
    }

}
