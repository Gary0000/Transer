package com.scott.transer.manager.interceptor;

import android.text.TextUtils;
import android.widget.TextView;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.Task;
import com.scott.transer.TaskCmd;
import com.scott.transer.handler.ITaskHolder;
import com.scott.transer.manager.ITaskManager;

import java.io.File;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-28 11:35</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      下载重命名的拦截器
 * </p>
 */

public class ReNameDownloadFileInterceptor implements ICmdInterceptor {

    private ITaskManager mTaskManager;

    public ReNameDownloadFileInterceptor(ITaskManager taskManager) {
        mTaskManager = taskManager;
    }

    @Override
    public TaskCmd intercept(Chain chain, TaskCmd cmd) {
        if((cmd.getProceeType() != ProcessType.TYPE_ADD_TASK &&
            cmd.getProceeType() != ProcessType.TYPE_ADD_TASKS) ||
                cmd.getTaskType() != TaskType.TYPE_HTTP_DOWNLOAD) {
            return chain.process(cmd);
        }

        autoReName(cmd);
        return chain.process(cmd);
    }

    private void autoReName(TaskCmd cmd) {
        switch (cmd.getProceeType()) {
            case TYPE_ADD_TASK:
                autoReNameFile((Task) cmd.getTask());
                break;
            case TYPE_ADD_TASKS:
                for(ITask task : cmd.getTasks()) {
                    autoReNameFile((Task) task);
                }
                break;
        }
    }

    private void autoReNameFile(Task task) {
        for(ITaskHolder task1 : mTaskManager.getTasks()) {
            if(TextUtils.equals(task.getDestUrl(),task1.getTask().getDestUrl())) {

                String name = getNewDestUrl(task.getDestUrl(),task.getName());
                task.setName(name);
                break;
            }
        }
    }

    private String getNewDestUrl(String path,String name) {
        if(!new File(path).exists()) {
            return null;
        }

        String ext = "";
        if(name.contains(".")) {
            ext = name.substring(name.lastIndexOf("."),name.length());
            name = name.substring(0,name.lastIndexOf("."));
        }

        int index = 1;
        String parentPath = new File(path).getParentFile().getAbsolutePath();
        String temp = parentPath + File.separator + name;
        while (true) {
            File file = new File(temp + "(" + index++ + ")" + ext);
            if(!file.exists()) {
                break;
            }
        }
        return new File(temp + "(" + --index + ")" + ext).getName();
    }
}
