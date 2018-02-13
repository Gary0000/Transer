package com.scott.transer.manager.dynamicproxy;

import com.scott.transer.manager.ITaskInternalProcessor;
import com.scott.transer.manager.ITaskProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2018/2/6</P>
 * <P>Email: shilec@126.com</p>
 *
 * ITaskProcessor 的动态代理类工厂
 *
 * 使用ITaskProcessor 动态的好处在于可以规避掉由于不了解TaskCmd的参数类型导致
 * 创建TaskCmd的不合法问题，动态代理类可以让任务操作变得像一个方法的调用。有具体参数
 * 类型约束。
 *
 * 虽然使用ITaskProcessor 操作任务，有的方法会有返回值，但是实际是不会有返回值的，
 * 任务状态变更还是会在@TaskScribe 注解的方法回掉。
 *
 */

public class ProcessorDynamicProxyFactory {

    private InvocationHandler mInvotionHandler;

    private static ProcessorDynamicProxyFactory sIntance;

    private ITaskProcessor mProcessor;

    private ProcessorDynamicProxyFactory() {
        mInvotionHandler = new ProcessorInvotionHandler();
    }

    public static ProcessorDynamicProxyFactory getInstance() {
        synchronized (ProcessorDynamicProxyFactory.class) {
            if(sIntance == null) {
                sIntance = new ProcessorDynamicProxyFactory();
            }
            return sIntance;
        }
    }

    public ITaskProcessor create() {
        synchronized (ProcessorDynamicProxyFactory.class) {
            mProcessor = (ITaskInternalProcessor) Proxy.newProxyInstance(getClass().getClassLoader(),
                        new Class[]{ITaskInternalProcessor.class}, mInvotionHandler);
        }
        return mProcessor;
    }
}
