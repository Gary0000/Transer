package com.scott.transer.handler;


import com.scott.transer.ITaskHolder;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 13:17</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public interface ITaskHandlerHolder extends ITaskHolder {

    void setTaskHandler(ITaskHandler handler);

    ITaskHandler getTaskHandler();
}
