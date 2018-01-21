package com.scott.transer.handler;


/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 13:17</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      既持有任务信息，又持有 TaskHandler 包装类
 * </p>
 */

public interface ITaskHandlerHolder extends ITaskHolder {

    //设置一个Handler
    void setTaskHandler(ITaskHandler handler);

    ITaskHandler getTaskHandler();

}
