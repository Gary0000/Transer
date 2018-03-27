package com.scott.example.example;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.handler.AbsHandlerFactory;
import com.scott.transer.handler.ITaskHandler;
import com.scott.transer.handler.ITaskHandlerCallback;
import com.scott.transer.handler.ITaskHandlerFactory;
import com.scott.transer.manager.ITaskManager;


/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-27 14:35</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class MyUploadHandlerFactory extends AbsHandlerFactory {

    @Override
    protected ITaskHandler create(ITask task) {
        return new MyUploadHandler.Builder()
                .addHeader("path","Private/" + task.getName())
                .addHeader("access_id","11314d017f97c77a4fe69fcbf6197d7f")
                .addHeader("file-md5","")
                .build();
    }
}
