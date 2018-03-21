package com.scott.transer;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.TaskType;

import java.util.List;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2017/12/16</P>
 * <P>Email: shilec@126.com</p>
 */

public class TaskCmd {

    private TaskType taskType;
    private ProcessType processType;
    private ITask task;
    private List<ITask> tasks;
    private String[] taskids;
    private String taskId;
    private int state;
    private String groupId;

    private TaskCmd() {

    }

    public String getTaskId() {
        if(task != null) {
            return task.getTaskId();
        }
        return taskId;
    }

    public String getGroupId() {
        if(task != null) {
            return task.getGroupId();
        }
        return groupId;
    }

    public int getState() {
        if(task != null) {
            return task.getState();
        }
        return state;
    }

    public ITask getTask() {
        return task;
    }

    public List<ITask> getTasks() {
        return tasks;
    }

    public String[] getTaskIds() {
        return taskids;
    }

    public ProcessType getProceeType() {
        return processType;
    }

    public TaskType getTaskType() {
        if(task != null) {
            return task.getType();
        }
        return taskType;
    }
    
    public static class Builder {
        private TaskCmd cmd = new TaskCmd();
        
        public Builder setTaskId(String taskId) {
            cmd.taskId = taskId;
            return this;
        }

        
        public Builder setGroupId(String groupId) {
            cmd.groupId = groupId;
            return this;
        }

        
        public Builder setState(int state) {
            cmd.state = state;
            return this;
        }

        
        public Builder setTask(ITask task) {
            cmd.task = task;
            return this;
        }

        
        public Builder setTasks(List<ITask> tasks) {
            cmd.tasks = tasks;
            return this;
        }

        
        public Builder setTaskIds(String[] taskIds) {
            cmd.taskids = taskIds;
            return this;
        }

        
        public Builder setProcessType(ProcessType type) {
            cmd.processType = type;
            return this;
        }

        
        public Builder setTaskType(TaskType type) {
            cmd.taskType = type;
            return this;
        }

        
        public Builder setTaskCmd(TaskCmd cmd) {
            this.cmd = cmd;
            return this;
        }

        
        public TaskCmd build() {
            return cmd;
        }
    }
}
