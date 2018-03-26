package com.scott.example.moudle;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.handler.ITaskHolder;

import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-21 13:03</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class TaskGroupItem extends AbstractExpandableItem<TaskChildItem>
        implements MultiItemEntity ,ITaskHolder {
    private ITask task;

    public TaskGroupItem(ITask task) {
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
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return mSubItems == null ?
                TaskItemType.TYPE_CHILD : TaskItemType.TYPE_GROUP;
    }



    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ITaskHolder)) {
            return false;
        }

        ITaskHolder item = (ITaskHolder) obj;
        return task.equals(item.getTask());
    }
}
