package com.scott.example.example;

import com.scott.transer.handler.DefaultHttpUploadHandler;

import org.json.JSONObject;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-01-31 18:09</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:  自定义上传 或下载，继承 DefaultUploadHandler 或者 DefaultDownloadHandler
 *  例如:
 *  服务器上传一片成功返回:
 *
 *  {
 *      code:1,
 *      now_length:1233123
 *      all_length:34345435
 *  }
 *
 *  全部上传成功返回:
 *  {
 *      code:0
 *  }
 * </p>
 */

public class MyUploadHandler extends DefaultHttpUploadHandler {

    @Override
    public boolean isPiceSuccessful() {
        try{
            String response = getNowResponse();
            JSONObject jObj = new JSONObject(response);
            int code = jObj.optInt("code");
            if(code == 1 || isSuccessful()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean isSuccessful() {
        try {
            String response = getNowResponse();
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.optInt("code");
            if(code == 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
