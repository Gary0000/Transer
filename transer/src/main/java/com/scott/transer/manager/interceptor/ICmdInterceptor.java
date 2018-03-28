package com.scott.transer.manager.interceptor;

import com.scott.transer.TaskCmd;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-28 10:53</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      命令拦截器
 * </p>
 */

public interface ICmdInterceptor {
    TaskCmd intercept(Chain chain,TaskCmd cmd);

    /**
     * 责任链
     */
    interface Chain {
        TaskCmd process(TaskCmd cmd);
    }
}
