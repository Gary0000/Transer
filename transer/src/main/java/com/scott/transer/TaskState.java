package com.scott.transer;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 13:05</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      当前handler 的状态值
 * </p>
 */

public interface TaskState {

    //错误
    int STATE_ERROR = -1;

    //正在执行
    int STATE_RUNNING = 1;

    //停止
    int STATE_STOP = 2;

    //完成
    int STATE_FINISH = 3;

    //准备好了，等待执行
    int STATE_READY = 4;
}
