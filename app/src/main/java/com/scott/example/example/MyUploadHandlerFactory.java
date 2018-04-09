package com.scott.example.example;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.handler.AbsHandlerFactory;
import com.scott.transer.handler.ITaskHandler;
import com.scott.transer.handler.ITaskHandlerCallback;
import com.scott.transer.handler.ITaskHandlerFactory;
import com.scott.transer.manager.ITaskManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-27 14:35</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class MyUploadHandlerFactory extends AbsHandlerFactory {

    @Override
    protected ITaskHandler create(ITask task) {
        String path = "Private/" + task.getName();
        try {
            path = URLEncoder.encode(path,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(path == null) {
            return null;
        }
        return new MyUploadHandler.Builder()
                .addHeader("path",path)
                .addHeader("auto-rename","1")
                .addHeader("access-id","0312d44cc00cecfc34bd425a7d31e1e6")
                .build();
    }
}
