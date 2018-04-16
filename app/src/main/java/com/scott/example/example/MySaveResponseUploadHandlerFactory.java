package com.scott.example.example;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.handler.AbsHandlerFactory;
import com.scott.transer.handler.ITaskHandler;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-04-12 13:46</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */
public class MySaveResponseUploadHandlerFactory extends AbsHandlerFactory {
    @Override
    protected ITaskHandler create(ITask iTask) {
        return new MySaveResponseUploadHandler.Builder()
                .addHeader("path",iTask.getDestPath())
                .build();
    }
}
