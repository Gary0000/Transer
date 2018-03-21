package com.scott.example.moudle;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.example.adapter.TaskGroupAdapter;
import com.scott.transer.handler.ITaskHolder;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-21 13:04</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class TaskChildItem implements MultiItemEntity,ITaskHolder{

    private ITask task;

    public TaskChildItem(ITask task) {
        this.task = task;
    }

    public ITask getTask() {
        return task;
    }

    @Override
    public void setTask(ITask task) {
        this.task = task;
    }

    @Override
    public TaskType getType() {
        return task.getType();
    }

    @Override
    public int getItemType() {
        return TaskGroupAdapter.TYPE_CHILD;
    }
}
