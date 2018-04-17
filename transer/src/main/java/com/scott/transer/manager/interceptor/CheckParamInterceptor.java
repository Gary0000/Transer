package com.scott.transer.manager.interceptor;

import android.text.TextUtils;

import com.scott.annotionprocessor.ProcessType;
import com.scott.transer.TaskCmd;
import com.shilec.xlogger.XLogger;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-28 11:32</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      检查命令是否合法的拦截器
 * </p>
 */

public class CheckParamInterceptor implements ICmdInterceptor{
    private final String TAG = CheckParamInterceptor.class.getSimpleName();

    @Override
    public TaskCmd intercept(Chain chain, TaskCmd cmd) {
        checkParams(cmd);
        return chain.process(cmd);
    }

    private void checkParams(TaskCmd cmd) {
        XLogger.getDefault().e(TAG,"intercept cmd = " + cmd);
        if(TextUtils.isEmpty(cmd.getUserId())) {
            throw new IllegalArgumentException("missing userId param!");
        }

        if(cmd.getProceeType() == null) {
            throw new IllegalArgumentException("missing processtype!");
        }

        if(cmd.getTaskType() == null) {
            throw new IllegalArgumentException("missing taskType!");
        }

        switch (cmd.getProceeType()) {
            case TYPE_ADD_TASK:
                if(cmd.getTask() == null) {
                    throwIllegalException(ProcessType.TYPE_ADD_TASK,"missing task!");
                }
                break;
            case TYPE_ADD_TASKS:
                if(cmd.getTasks() == null) {
                    throwIllegalException(ProcessType.TYPE_ADD_TASKS,"missing tasks!");
                }
                break;
            case TYPE_DELETE_TASK:
            case TYPE_QUERY_TASK:
            case TYPE_UPDATE_TASK:
            case TYPE_UPDATE_TASK_WTIHOUT_SAVE:
            case TYPE_START_TASK:
            case TYPE_STOP_TASK:
                if(cmd.getTask() == null && TextUtils.isEmpty(cmd.getTaskId())){
                    throwIllegalException(ProcessType.TYPE_DELETE_TASK,"missing taskId or task!");
                }
                break;
            case TYPE_DELETE_TASKS_GROUP:
            case TYPE_QUERY_TASKS_GROUP:
            case TYPE_START_GROUP:
            case TYPE_STOP_GROUP:
                if(cmd.getTask() == null && TextUtils.isEmpty(cmd.getGroupId())) {
                    throwIllegalException(ProcessType.TYPE_DELETE_TASKS_GROUP,"missing groupId!");
                }
                break;
            case TYPE_DELETE_TASKS_SOME:
            case TYPE_QUERY_TASKS_SOME:
                if(cmd.getTasks() == null && cmd.getTaskIds() == null) {
                    throwIllegalException(ProcessType.TYPE_DELETE_TASKS_SOME,"missing tasks or taskIds!");
                }
                break;
        }
    }

    private void throwIllegalException(ProcessType processType,String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append("current processType is " + processType + ",");
        builder.append(msg);
        throw new IllegalArgumentException(builder.toString());
    }
}
