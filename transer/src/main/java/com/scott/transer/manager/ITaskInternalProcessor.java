package com.scott.transer.manager;

import com.scott.annotionprocessor.ITask;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 15:00</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public interface ITaskInternalProcessor extends ITaskProcessor {

    void setTaskManager(ITaskManager manager);

    void updateTask(ITask task);

    void updateTaskWithoutSave(ITask task);
}
