package com.scott.transer.event;

import android.content.Context;

import com.scott.annotionprocessor.ThreadMode;
import com.scott.transer.ITaskCmd;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-14 16:05</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class TaskEventBus {

    EventDispatcher mDispatcher;
    volatile static TaskEventBus sInstance;
    private Map<ThreadMode,ITaskPoster> mTaskPosters = new HashMap<>();

    public ITaskPoster getTaskPoster(ThreadMode mode) {
        return mTaskPosters.get(mode);
    }

    public static void init(Context context) {
        synchronized (TaskEventBus.class) {
            if (sInstance == null) {
                sInstance = new TaskEventBus(context);
            }
        }
    }

    public  EventDispatcher getDispatcher() {
        return mDispatcher;
    }

    TaskEventBus(Context context) {
        mDispatcher = new EventDispatcher(context);
        sInstance = this;
        mTaskPosters.put(ThreadMode.MODE_ASYNC,new AsyncTaskPoster());
        mTaskPosters.put(ThreadMode.MODE_MAIN,new MainTaskPoster());
        mTaskPosters.put(ThreadMode.MODE_POSTING,new PostingTaskPoster());
    }

    public void regesit(Object obj) {
        mDispatcher.regist(obj);
    }

    public void unregesit(Object obj) {
        mDispatcher.unregist(obj);
    }

    public static TaskEventBus getDefault() {
        if(sInstance == null) {
            throw new IllegalStateException("you must init TaskEventBus in your application!");
        }
        return sInstance;
    }

    /**
     * 使用{@link com.scott.transer.manager.dynamicproxy.ProcessorDynamicProxyFactory}
     * @param cmd
     */
    @Deprecated
    public synchronized void execute(ITaskCmd cmd) {
        if(mDispatcher == null) {
            return;
        }
        mDispatcher.dispatchCmd(cmd);
    }
}
