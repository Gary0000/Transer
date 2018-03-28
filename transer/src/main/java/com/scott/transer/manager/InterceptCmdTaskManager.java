package com.scott.transer.manager;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.TaskCmd;
import com.scott.transer.handler.ITaskHandlerFactory;
import com.scott.transer.handler.ITaskHolder;
import com.scott.transer.manager.interceptor.AutoRenameTaskInterceptor;
import com.scott.transer.manager.interceptor.ChainImpl;
import com.scott.transer.manager.interceptor.CheckParamInterceptor;
import com.scott.transer.manager.interceptor.ICmdInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-28 11:43</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      拦截任务命令的任务管理器
 * </p>
 */

public class InterceptCmdTaskManager implements ITaskManager{
    private ITaskManager mTaskManager;
    private List<ICmdInterceptor> mInterceptors = new ArrayList<>();

    public InterceptCmdTaskManager() {
        mInterceptors.add(new CheckParamInterceptor());
        mInterceptors.add(new AutoRenameTaskInterceptor(this));
        mTaskManager = new DispatchCmdTaskManager();
    }

    @Override
    public void process(TaskCmd cmd) {
        ICmdInterceptor.Chain chain = new ChainImpl(0,mInterceptors);
        TaskCmd process = chain.process(cmd);
        mTaskManager.process(process);
    }

    @Override
    public void setTaskProcessor(ITaskInternalProcessor operation) {
        mTaskManager.setTaskProcessor(operation);
    }

    @Override
    public void setProcessCallback(ITaskProcessCallback callback) {
        mTaskManager.setProcessCallback(callback);
    }

    @Override
    public void addThreadPool(TaskType taskType, ThreadPoolExecutor threadPool) {
        mTaskManager.addThreadPool(taskType,threadPool);
    }

    @Override
    public ThreadPoolExecutor getTaskThreadPool(TaskType type) {
        return mTaskManager.getTaskThreadPool(type);
    }

    @Override
    public ITaskHandlerFactory getTaskHandlerCreator(ITask task) {
        return mTaskManager.getTaskHandlerCreator(task);
    }

    @Override
    public void addHandlerCreator(TaskType type, ITaskHandlerFactory handlerCreator) {
        mTaskManager.addHandlerCreator(type,handlerCreator);
    }

    @Override
    public List<ITaskHolder> getTasks() {
        return mTaskManager.getTasks();
    }

    @Override
    public void setManager(ITaskManager manager) {
        mTaskManager = manager;
    }

    @Override
    public ITaskManager getManager() {
        return mTaskManager;
    }
}
