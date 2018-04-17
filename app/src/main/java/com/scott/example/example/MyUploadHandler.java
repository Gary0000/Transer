package com.scott.example.example;


import com.scott.annotionprocessor.ITask;
import com.scott.example.utils.FileUtils;
import com.scott.transer.handler.DefaultHttpUploadHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
        String ret = getNowResponse();
        try {
            ret = ret.substring(0, ret.indexOf("/") + 1);
            String reg = "[0-9]+-[0-9]+/";
            if (ret.matches(reg)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isSuccessful() {
        try {
            JSONObject jObj = new JSONObject(getNowResponse());
            int code = jObj.optInt("code");

            return code == 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected int getPiceBuffSize() {
        return 1 * 1024 * 1024;
    }

    @Override
    protected void prepare(ITask task) throws IOException {
        super.prepare(task);
        //在prepare 设置文件MD5，是在线程中获取的，不会阻塞UI
        String file_md5 = FileUtils.getFileMD5Value(task.getSourceUrl());
        getHeaders().put("file-md5",file_md5);
    }

    public static class Builder extends DefaultHttpUploadHandler.Builder {

        private String arg;

        public Builder setArg(String arg) {
            this.arg = arg;
            return this;
        }

        @Override
        protected DefaultHttpUploadHandler buildTarget() {
            return new MyUploadHandler();
        }
    }
}
