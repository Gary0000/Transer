package com.scott.transer;

import android.text.TextUtils;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.utils.NumberUtils;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-14 17:51</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *  携带自定义参数需要继承自该类
 * </p>
 */

@Entity
public class Task implements ITask {

    private String dataSource;
    private String destSource;
    private String sesstionId;
    private long length;
    private long startOffset;
    private long endOffset;
    @Id
    private String taskId;
    private String groupId;
    private String groupName;
    private long completeTime;
    private long completeLength;
    private int state;
    private String name;
    private long speed;
    private String destPath;
    private String sourcePath;

    @Convert(converter = TaskTypeConverter.class,columnType = Integer.class)
    private TaskType type;
    private String userId;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Task task = new Task();
        task.dataSource = dataSource;
        task.destSource = destSource;
        task.sesstionId = sesstionId;
        task.length = length;
        task.startOffset = startOffset;
        task.endOffset = endOffset;
        task.taskId = taskId;
        task.groupId = groupId;
        task.groupName = groupName;
        task.completeTime = completeTime;
        task.completeLength = completeLength;
        task.state = state;
        task.name = name;
        task.speed = speed;
        task.destPath = destPath;
        task.sourcePath = sourcePath;
        task.type = type;
        task.userId = userId;
        return task;
    }

    public Task cloneTask() {
        try {
            return (Task) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Generated(hash = 572294146)
    public Task(String dataSource, String destSource, String sesstionId,
            long length, long startOffset, long endOffset, String taskId,
            String groupId, String groupName, long completeTime,
            long completeLength, int state, String name, long speed,
            String destPath, String sourcePath, TaskType type, String userId) {
        this.dataSource = dataSource;
        this.destSource = destSource;
        this.sesstionId = sesstionId;
        this.length = length;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.taskId = taskId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.completeTime = completeTime;
        this.completeLength = completeLength;
        this.state = state;
        this.name = name;
        this.speed = speed;
        this.destPath = destPath;
        this.sourcePath = sourcePath;
        this.type = type;
        this.userId = userId;
    }
    
    public Task(ITask task) {
        this.dataSource = task.getSourceUrl();
        this.destSource = task.getDestUrl();
        this.sesstionId = task.getSesstionId();
        this.length = task.getLength();
        this.startOffset = task.getStartOffset();
        this.endOffset = task.getEndOffset();
        this.taskId = task.getTaskId();
        this.groupId = task.getGroupId();
        this.groupName = task.getGroupName();
        this.completeTime = task.getCompleteTime();
        this.completeLength = task.getCompleteLength();
        this.state = task.getState();
        this.name = task.getName();
        this.speed = task.getSpeed();
        this.destPath = task.getDestPath();
        this.sourcePath = task.getSourcePath();
        this.type = task.getType();
        this.userId = task.getUserId();
    }


    @Generated(hash = 733837707)
    public Task() {
    }



    @Override
    public String getSourceUrl() {
        return dataSource;
    }

    @Override
    public String getSourcePath() {
        return sourcePath;
    }

    @Override
    public String getDestUrl() {
        return destSource;
    }

    @Override
    public String getDestPath() {
        return destPath;
    }

    @Override
    public String getSesstionId() {
        if(TextUtils.isEmpty(sesstionId)) {
            sesstionId = NumberUtils.getRandomStr(8,getTaskId().hashCode());
        }
        return sesstionId;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public long getStartOffset() {
        return startOffset;
    }

    @Override
    public long getEndOffset() {
        return endOffset;
    }

    @Override
    public String getTaskId() {
        if(TextUtils.isEmpty(taskId)) {
            taskId = name.hashCode() + "" + System.currentTimeMillis();
        }
        return taskId;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    @Override
    public long getCompleteTime() {
        return completeTime;
    }

    @Override
    public long getCompleteLength() {
        return completeLength;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSpeed() {
        return speed;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setDestSource(String destSource) {
        this.destSource = destSource;
    }

    public void setSesstionId(String sesstionId) {
        this.sesstionId = sesstionId;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setStartOffset(long startOffset) {
        this.startOffset = startOffset;
    }

    public void setEndOffset(long endOffset) {
        this.endOffset = endOffset;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setCompleteTime(long completeTime) {
        this.completeTime = completeTime;
    }

    public void setCompleteLength(long completeLength) {
        this.completeLength = completeLength;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Task{" +
                "dataSource='" + dataSource + '\'' +
                ", destSource='" + destSource + '\'' +
                ", sesstionId='" + sesstionId + '\'' +
                ", length=" + length +
                ", startOffset=" + startOffset +
                ", endOffset=" + endOffset +
                ", taskId='" + taskId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", completeTime=" + completeTime +
                ", completeLength=" + completeLength +
                ", state=" + state +
                ", type=" + type +
                ", userId='" + userId + '\'' +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDestSource() {
        return this.destSource;
    }


    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }




    public String getDataSource() {
        return this.dataSource;
    }


    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    
    public static class Builder {
        
        public Builder() {
            task = new Task();
        }
        
        private Task task;
        
        public Builder setSourceUrl(String dataSource) {
            task.dataSource = dataSource;
            return this;
        }

        
        public Builder setDestUrl(String destSource) {
            task.destSource = destSource;
            return this;
        }

        
        public Builder setSesstionId(String sesstionId) {
            task.sesstionId = sesstionId;
            return this;
        }

        
        public Builder setLength(long length) {
            task.length = length;
            return this;
        }

        
        public Builder setStartoffset(long offset) {
            task.startOffset = offset;
            return this;
        }

        
        public Builder setEndOffset(long offset) {
            task.endOffset = offset;
            return this;
        }

        
        public Builder setGroupId(String groupId) {
            task.groupId = groupId;
            return this;
        }

        
        public Builder setGroupName(String groupName) {
            task.groupName = groupName;
            return this;
        }

        
        public Builder setCompleteTime(long completeTime) {
            task.completeTime = completeTime;
            return this;
        }

        
        public Builder setCompleteLength(long length) {
            task.completeLength = length;
            return this;
        }

        
        public Builder setState(int state) {
            task.state = state;
            return this;
        }

        
        public Builder setTaskType(TaskType type) {
            task.type = type;
            return this;
        }
        
        public Builder setUserId(String userId) {
            task.userId = userId;
            return this;
        }

        
        public ITask build() {
            return task;
        }

        
        public Builder setTask(Task task) {
            this.task = task;
            return this;
        }

        
        public Builder setName(String name) {
            task.name = name;
            return this;
        }

        
        public Builder setTaskId(String taskId) {
            task.taskId = taskId;
            return this;
        }

        
        public Builder setSessionId(String sesstionId) {
            task.sesstionId = sesstionId;
            return this;
        }

        
        public Builder setSpeed(long speed) {
            task.speed = speed;
            return this;
        }

        
        public Builder setDestPath(String destPath) {
            task.destPath = destPath;
            return this;
        }

        
        public Builder setSourcePath(String sourcePath) {
            task.sourcePath = sourcePath;
            return this;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ITask)) {
            return false;
        }
        return TextUtils.equals(taskId,((ITask)obj).getTaskId());
    }
}
