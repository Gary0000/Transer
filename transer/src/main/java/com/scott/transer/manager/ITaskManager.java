package com.scott.transer.manager;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.ITaskCmd;
import com.scott.transer.handler.ITaskHandlerFactory;
import com.scott.transer.handler.ITaskHolder;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 14:31</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *    任务调度管理器，可以通过setManager 来设置后继的责任对象
 * </p>
 */

public interface ITaskManager {

    // 执行用户发来的命令
    void process(ITaskCmd cmd);

    //设置处理任务的处理器 例如:对任务的增删改查
    void setTaskProcessor(ITaskInternalProcessor operation);

    //处理任务后的回调
    void setProcessCallback(ITaskProcessCallback callback);

    //设置任务传输的线程池，tasktype 为 传输类型
    void addThreadPool(TaskType taskType, ThreadPoolExecutor threadPool);

    ThreadPoolExecutor getTaskThreadPool(TaskType type);

    //通过传输类型获取一个传输器
    ITaskHandlerFactory getTaskHandlerCreator(ITask task);

    //设置传输器
    void addHandlerCreator(TaskType type, ITaskHandlerFactory handlerCreator);

    //获取任务列表信息
    List<ITaskHolder> getTasks();

    //责任链
    void setManager(ITaskManager manager);

    ITaskManager getManager();

}
