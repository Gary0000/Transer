package com.scott.annotionprocessor;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-14 15:58</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface TaskSubscriber {

    /**
     * 操作类型
     * @return
     */
    ProcessType[] processType() default ProcessType.TYPE_DEFAULT;

    /**
     * 任务类型
     * @return
     */
    TaskType taskType() default TaskType.TYPE_HTTP_UPLOAD;

    /**
     * 事件接收的线程
     * @return
     */
    ThreadMode threadMode() default ThreadMode.MODE_POSTING;
}
