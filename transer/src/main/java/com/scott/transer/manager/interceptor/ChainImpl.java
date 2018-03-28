package com.scott.transer.manager.interceptor;

import com.scott.transer.TaskCmd;
import com.scott.transer.manager.ITaskManager;

import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-28 11:29</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class ChainImpl implements ICmdInterceptor.Chain{
    private int index;
    private List<ICmdInterceptor> interceptors;

    public ChainImpl(int index,List<ICmdInterceptor> interceptors) {
        this.index = index;
        this.interceptors = interceptors;
    }

    @Override
    public TaskCmd process(TaskCmd cmd) {
        ICmdInterceptor.Chain chain = new ChainImpl(index + 1,interceptors);
        if(index >= interceptors.size()) {
            return cmd;
        }
        ICmdInterceptor interceptor = interceptors.get(index);
        return interceptor.intercept(chain,cmd);
    }
}
