package com.scott.example.example;

import com.scott.transer.handler.DefaultHttpUploadHandler;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-04-12 13:37</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      保存服务器返回数据的Handler 实现
 * </p>
 */
public class MySaveResponseUploadHandler extends DefaultHttpUploadHandler {

    @Override
    protected boolean isSuccessful() {
        boolean isUploadSuccessful =  true;
        String response = getNowResponse();
        //TODO 查看response 是否是文件上传成功

        /*
            注意: isSuccessful 是在上传线程中调用的，所以在这
            里做耗时操作会阻塞传输线程。

            如果过报错返回值，建议只有返回成功后再处理
         */
        if(isUploadSuccessful) {
            saveResponse(getNowResponse());
        }
        return isUploadSuccessful;
    }

    private void saveResponse(String response) {
        //TODO save response
    }
}
