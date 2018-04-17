package com.scott.transer.manager.interceptor;

import com.scott.transer.TaskCmd;

import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-28 11:29</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class ChainImpl implements Interceptor.Chain{
    private int index;
    private List<Interceptor> interceptors;

    public ChainImpl(int index,List<Interceptor> interceptors) {
        this.index = index;
        this.interceptors = interceptors;
    }

    @Override
    public TaskCmd process(TaskCmd cmd) {
        Interceptor.Chain chain = new ChainImpl(index + 1,interceptors);
        if(index >= interceptors.size()) {
            return cmd;
        }
        Interceptor interceptor = interceptors.get(index);
        return interceptor.intercept(chain,cmd);
    }
}
