package com.scott.transer.handler;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.manager.ITaskManager;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-01-19 15:21</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public interface ITaskHandlerCreator {

    ITaskHandler create(ITask task, ITaskManager manager);

    void setTaskHandlerCallback(ITaskHandlerCallback callback);
}
