package com.scott.transer.manager.interceptor;

import com.scott.transer.TaskCmd;
import com.shilec.xlogger.XLogger;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-04-17 16:00</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */
public class LogInterceptor implements Interceptor {
    @Override
    public TaskCmd intercept(Chain chain, TaskCmd cmd) {
        StringBuilder sb = new StringBuilder();
        sb.append("taskType = " + cmd.getTaskType())
                .append(",")
                .append("processType = " + cmd.getProceeType());
        XLogger.getDefault().e(sb.toString());
        return chain.process(cmd);
    }
}
