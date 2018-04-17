package com.scott.transer;

import com.scott.annotionprocessor.TaskType;
import com.scott.transer.handler.ITaskHandlerFactory;
import com.scott.transer.manager.ITaskManager;
import com.scott.transer.manager.ITaskInternalProcessor;
import com.scott.transer.manager.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2018/2/3</P>
 * <P>Email: shilec@126.com</p>
 *
 * transer 配置
 */

public class TranserConfig {

    Builder mBuilder;

    private TranserConfig() {

    }

    public static class Builder {

        ITaskInternalProcessor mMemoryProcessor;

        ITaskInternalProcessor mDatabaseProcessor;

        int mUploadConcurrentThreadSize;

        int mDownloadConcurrentSize;

        ThreadPoolExecutor mUploadThreadPool;

        ThreadPoolExecutor mDownloadThreadPool;

        ITaskManager mTaskManager;

        Map<TaskType,ITaskHandlerFactory> mHanlderCreators = new HashMap<>();

        boolean isSupportProcessorDynamicProxy;

        List<Interceptor> interceptors;

        public Builder addCmdInterceptor(Interceptor interceptor) {
            if(interceptors == null) {
                interceptors = new ArrayList<>();
            }
            interceptors.add(interceptor);
            return this;
        }

        public Builder setSupportProcessorDynamicProxy(boolean isSupport) {
            isSupportProcessorDynamicProxy = isSupport;
            return this;
        }

        /***
         * 设置一个任务处理器，任务的 增删改查
         * @param processor
         * @return
         */
        public Builder setMemoryProcessor(ITaskInternalProcessor processor) {
            mMemoryProcessor = processor;
            return this;
        }

        /***
         * 设置一个任务持久化处理器 任务的持久化 增删改查
         * @param processor
         * @return
         */
        public Builder setDatabaseProcessor(ITaskInternalProcessor processor) {
            mDatabaseProcessor = processor;
            return this;
        }

        /***
         * 设置任务上传同时进行的任务个数
         * @param size
         * @return
         */
        public Builder setUploadConcurrentThreadSize(int size) {
            mUploadConcurrentThreadSize = size;
            return this;
        }

        /***
         * 设置下载任务同时进行的任务个数
         * @param size
         * @return
         */
        public Builder setDownloadConcurrentThreadSize(int size) {
            mDownloadConcurrentSize = size;
            return this;
        }

        /**
         * 设置任务下载的线程池，设置了线程池，则 setDownloadConcurrentThreadSize 将不起作用
         * @param threadPool
         * @return
         */
        public Builder setDownloadThreadPool(ThreadPoolExecutor threadPool) {
            mDownloadThreadPool = threadPool;
            return this;
        }

        /***
         * 设置任务上传的线程池， 设置了线程池， 则 setUploadConcurrentThreadSize 将不起作用
         * @param threadPool
         * @return
         */
        public Builder setUploadThreadPool(ThreadPoolExecutor threadPool) {
            mUploadThreadPool = threadPool;
            return this;
        }

        /**
         * 设置任务管理器，{@link ITaskManager}
         * @param manager
         * @return
         */
        public Builder setTaskManager(ITaskManager manager) {
            mTaskManager = manager;
            return this;
        }

        /**
         * 添加一种任务类型对应的传输器 {@link ITaskHandlerFactory}
         * @param taskType
         * @param factory
         * @return
         */
        public Builder addHandlerFactory(TaskType taskType,ITaskHandlerFactory factory) {
            mHanlderCreators.put(taskType,factory);
            return this;
        }

        public TranserConfig build() {
            TranserConfig config = new TranserConfig();
            config.mBuilder = this;
            return config;
        }
    }
}
