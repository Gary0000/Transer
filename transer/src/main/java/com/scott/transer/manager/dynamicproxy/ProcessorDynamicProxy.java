package com.scott.transer.manager.dynamicproxy;

import com.scott.transer.manager.ITaskProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2018/2/6</P>
 * <P>Email: shilec@126.com</p>
 */

public class ProcessorDynamicProxy {

    private InvocationHandler mInvotionHandler;

    private static ProcessorDynamicProxy sIntance;

    private ITaskProcessor mProcessor;

    private ProcessorDynamicProxy() {
        mInvotionHandler = new ProcessorInvotionHandler();
    }

    public static ProcessorDynamicProxy getInstance() {
        synchronized (ProcessorDynamicProxy.class) {
            if(sIntance == null) {
                sIntance = new ProcessorDynamicProxy();
            }
            return sIntance;
        }
    }

    public ITaskProcessor create() {
        synchronized (ProcessorDynamicProxy.class) {
                mProcessor = (ITaskProcessor) Proxy.newProxyInstance(getClass().getClassLoader(),
                        new Class[]{ITaskProcessor.class}, mInvotionHandler);
        }
        return mProcessor;
    }
}
