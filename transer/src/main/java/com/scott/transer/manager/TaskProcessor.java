package com.scott.transer.manager;

import android.text.TextUtils;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.TaskState;
import com.scott.transer.handler.ITaskHandler;
import com.scott.transer.handler.ITaskHandlerFactory;
import com.scott.transer.handler.ITaskHandlerHolder;
import com.scott.transer.handler.ITaskHolder;
import com.scott.transer.handler.TaskHandlerHolder;
import com.shilec.xlogger.XLogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 15:38</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class TaskProcessor implements ITaskInternalProcessor {

    private List<ITaskHolder> mTasks;
    private ITaskManager mTaskManager;
    private final String TAG = TaskProcessor.class.getSimpleName();

    @Override
    public void setTaskManager(ITaskManager manager) {
        mTaskManager = manager;
        mTasks = manager.getTasks();
    }

    @Override
    public void addTask(ITask task) {
        ITaskHolder holder = new TaskHandlerHolder();
        holder.setTask(task);
        mTasks.add(holder);
    }

    @Override
    public void addTasks(List<ITask> tasks) {
        for(ITask task : tasks) {
            ITaskHolder holder = new TaskHandlerHolder();
            holder.setTask(task);
            mTasks.add(holder);
        }
    }

    @Override
    public void deleteTask(String taskId) {
        Iterator<ITaskHolder> iterator = mTasks.iterator();
        while (iterator.hasNext()) {
            ITaskHolder next = iterator.next();
            if(TextUtils.equals(taskId,next.getTask().getTaskId())) {
                iterator.remove();
                ITaskHandlerHolder h = (ITaskHandlerHolder) next;
                if(h.getTaskHandler() != null) {
                    h.getTaskHandler().stop();
                }
                break;
            }
        }
    }

    @Override
    public void deleteGroup(String groupId,String userId) {
        if(TextUtils.isEmpty(groupId)) {
            throw new IllegalArgumentException("groupId can " +
                    "not be a null value!");
        }

        Iterator<ITaskHolder> iterator = mTasks.iterator();
        while (iterator.hasNext()) {
            ITaskHolder next = iterator.next();
            if(TextUtils.equals(groupId,next.getTask().getGroupId()) &&
                    TextUtils.equals(userId,next.getTask().getUserId())) {
                iterator.remove();
                ITaskHandlerHolder h = (ITaskHandlerHolder) next;
                if(h.getTaskHandler() != null) {
                    h.getTaskHandler().stop();
                }
            }
        }
    }

    @Override
    public void deleteTasks(String[] taskIds) {
        Iterator<ITaskHolder> iterator = mTasks.iterator();
        while (iterator.hasNext()) {
            ITaskHolder next = iterator.next();
            for(String taskId : taskIds) {
                if (TextUtils.equals(taskId, next.getTask().getTaskId())) {
                    iterator.remove();
                    ITaskHandlerHolder h = (ITaskHandlerHolder) next;
                    if (h.getTaskHandler() != null) {
                        h.getTaskHandler().stop();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void deleteCompleted(TaskType type,String userId) {
        Iterator<ITaskHolder> iterator = mTasks.iterator();
        while (iterator.hasNext()) {
            ITaskHolder next = iterator.next();
            if(type == next.getType() && next.getTask().getState() == TaskState.STATE_FINISH &&
                    TextUtils.equals(userId,next.getTask().getUserId())) {
                iterator.remove();
                ITaskHandlerHolder h = (ITaskHandlerHolder) next;
                if(h.getTaskHandler() != null) {
                    h.getTaskHandler().stop();
                }
            }
        }
    }

    @Override
    public void delete(int state,TaskType type,String userId) {
        Iterator<ITaskHolder> iterator = mTasks.iterator();
        while (iterator.hasNext()) {
            ITaskHolder next = iterator.next();
            if(type == next.getType() && next.getTask().getState() == state &&
                    TextUtils.equals(userId,next.getTask().getUserId())) {
                iterator.remove();
                ITaskHandlerHolder h = (ITaskHandlerHolder) next;
                if(h.getTaskHandler() != null) {
                    h.getTaskHandler().stop();
                }
            }
        }
    }

    @Override
    public void deleteAll(TaskType type,String userId) {

        Iterator<ITaskHolder> iterator = mTasks.iterator();
        while (iterator.hasNext()) {
            ITaskHolder next = iterator.next();
            if(next.getType() == type && TextUtils.equals(next.getTask().getUserId(),userId)) {
                ITaskHandlerHolder h = (ITaskHandlerHolder) next;
                if(h.getTaskHandler() != null) {
                    h.getTaskHandler().stop();
                }
                iterator.remove();
            }
        }
    }

    @Override
    public ITask getTask(String taskId) {
        for(ITaskHolder holder : mTasks) {
            if(holder.getTask().getTaskId() == taskId) {
                return holder.getTask();
            }
        }
        return null;
    }

    @Override
    public List<ITask> getTasks(String[] taskIds) {
        List<ITask> tasks = new ArrayList<>();
        for(ITaskHolder task : mTasks) {
            for(String taskId : taskIds) {
                if(task.getTask().getTaskId() == taskId) {
                    tasks.add(task.getTask());
                }
            }
        }
        return tasks;
    }

    @Override
    public List<ITask> getGroup(String groupId,String userId) {
        List<ITask> tasks = new ArrayList<>();
        for(ITaskHolder holder : mTasks) {
            if(holder.getTask().getGroupId() == groupId &&
                    TextUtils.equals(holder.getTask().getUserId(),userId)) {
                tasks.add(holder.getTask());
            }
        }
        return tasks;
    }

    @Override
    public List<ITask> getAllTasks(TaskType type,String userId) {
        List<ITask> tasks = new ArrayList<>();
        for(ITaskHolder holder : mTasks) {
            if(type == holder.getType() &&
                    TextUtils.equals(userId,holder.getTask().getUserId())) {
                tasks.add(holder.getTask());
            }
        }
        return tasks;
    }

    @Override
    public List<ITask> getTasks(int state,TaskType type,String userId) {
        List<ITask> tasks = new ArrayList<>();
        for(ITaskHolder holder : mTasks) {
            if(holder.getTask().getState() == state
                    && type == holder.getType() &&
                    TextUtils.equals(userId,holder.getTask().getUserId())) {
                tasks.add(holder.getTask());
            }
        }
        return tasks;
    }

    @Override
    public void updateTask(ITask task) {
        XLogger.getDefault().e(TAG,"STATE === " + task.getState());
        for(ITaskHolder holder : mTasks) {
            ITask task1 = holder.getTask();
            if(TextUtils.equals(task.getTaskId(),task1.getTaskId())) {
                holder.setTask(task);
                if(holder.getTask().getState() == TaskState.STATE_FINISH) {
                    ((ITaskHandlerHolder)holder).setTaskHandler(null);
                }
                break;
            }
        }
    }

    @Override
    public void updateTaskWithoutSave(ITask task) {
        updateTask(task);
    }

    @Override
    public void start(String taskId) {
        for(ITaskHolder h : mTasks) {
            if(TextUtils.equals(h.getTask().getTaskId(),taskId)) {
                startOrStop(h,true);
                break;
            }
        }
    }

    private void startOrStop(ITaskHolder holder,boolean isStart) {
        ITaskHandlerHolder handlerHolder = (ITaskHandlerHolder) holder;
        if(handlerHolder.getTaskHandler() == null) {
            ITaskHandlerFactory creator = mTaskManager.getTaskHandlerCreator(holder.getTask());
            ITaskHandler handler = creator.create(holder.getTask(),mTaskManager);
            handlerHolder.setTaskHandler(handler);
        }

        if(isStart) {
            handlerHolder.getTaskHandler().start();
        } else {
            handlerHolder.getTaskHandler().stop();
        }
    }

    @Override
    public void startGroup(String groupId,String userId) {
        for(ITaskHolder holder : mTasks) {
            if(TextUtils.equals(holder.getTask().getGroupId(),groupId)
                    && TextUtils.equals(holder.getTask().getUserId(),userId)) {
                startOrStop(holder,true);
            }
        }
    }

    @Override
    public void startAll(TaskType taskType,String userId) {
        for(ITaskHolder holder : mTasks) {
            if(holder.getType() == taskType &&
                    TextUtils.equals(holder.getTask().getUserId(),userId)) {
                startOrStop(holder, true);
            }
        }
    }

    @Override
    public void stop(String taskId) {
        for(ITaskHolder holder : mTasks) {
            if(TextUtils.equals(holder.getTask().getTaskId(),taskId)) {
                startOrStop(holder,false);
                break;
            }
        }
    }

    @Override
    public void stopGroup(String groupId,String userId) {
        for(ITaskHolder holder : mTasks) {
            if(TextUtils.equals(holder.getTask().getGroupId(),groupId)
                    && TextUtils.equals(holder.getTask().getUserId(),userId)) {
                startOrStop(holder,false);
            }
        }
    }

    @Override
    public void stopAll(TaskType taskType,String userId) {
        for(ITaskHolder holder : mTasks) {
            if(holder.getType() == taskType &&
                    TextUtils.equals(holder.getTask().getUserId(),userId)) {
                startOrStop(holder, false);
            }
        }
    }

}
