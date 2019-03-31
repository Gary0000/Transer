package com.scott.transer.utils;

import com.scott.transer.TaskErrorCode;

import java.lang.reflect.Field;

/**
 * <P>Author: shijiale-PUBG</P>
 * <P>Date: 2018/5/18</P>
 * <P>Email: shilec@126.com</p>
 */
public class ErrorCodeFormatUtil {

    public static String format(int code) {
        TaskErrorCode errorCode = new TaskErrorCode() {};
        Class<TaskErrorCode> cls = (Class<TaskErrorCode>) errorCode.getClass();
        Field[] declaredFields = cls.getFields();
        for(Field field : declaredFields) {
            try {
                Object o = field.get(errorCode);
                if (o == null) {
                    continue;
                }

                if (code == Integer.parseInt(o.toString())) {
                    return field.getName();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }
        }
        return "UNKNOW_ERROR";
    }
}
