package com.scott.transer.handler;

import com.scott.annotionprocessor.ITask;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 12:47</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public interface ITaskHandlerCallback {

    //任务准备好(调用了 start)
    void onReady(ITask task);

    //任务开始
    void onStart(ITask task);

    //任务结束
    void onStop(ITask task);

    //任务异常
    void onError(int code,ITask task);

    //会在一个单独的守护线程中执行
    void onSpeedChanged(long speed,ITask task);

    //每一片成功
    void onPiceSuccessful(ITask task);

    //成功
    void onFinished(ITask task);

}
