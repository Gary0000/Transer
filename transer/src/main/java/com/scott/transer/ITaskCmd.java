package com.scott.transer;

import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;

import java.util.List;
import java.util.Map;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 16:10</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public interface ITaskCmd {

    String getTaskId();

    String getGroupId();

    int getState();

    ITask getTask();

    List<ITask> getTasks();

    String[] getTaskIds();

    ProcessType getProceeType();

    TaskType getTaskType();

}
