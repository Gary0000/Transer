package com.scott.example.app;

import com.shilec.xlogger.DefaultLoggerFormater;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-04-17 10:24</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */
public class XLoggerFormaterImpl extends DefaultLoggerFormater {

    @Override
    public String format(String tag, String msg, Throwable throwable) {
        return msg;
    }
}
