package com.scott.transer.handler;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.Speed;
import com.scott.transer.http.OkHttpProxy;
import com.shilec.xlogger.XLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <P>Author: shijiale-PUBG</P>
 * <P>Date: 2018/5/17</P>
 * <P>Email: shilec@126.com</p>
 */
public class DefaultFormPartUploadHandler extends BaseTaskHandler {

    public static final @Speed long DEFAULT_LIMIT_SIZE = SPEED_LIMIT_SIZE.SPEED_2MB;
    private @Speed long mSizeLimit = DEFAULT_LIMIT_SIZE;

    private File mFile;
    private Call mRequestCall;
    private Request.Builder mReqBuilder;
    private MultipartBody.Builder mFPBuilder;
    private MediaType mFileMediaType;
    private String mResponse;
    private long mCurrentSize = 0;

    protected String getResponse() {
        return mResponse;
    }

    @Override
    public TaskType getType() {
        return TaskType.TYPE_HTTP_UPLOAD;
    }

    @Override
    public void stop() {
        super.stop();
        if(mRequestCall != null) {
            mRequestCall.cancel();
        }
    }

    @Override
    protected boolean isSuccessful() {
        if (TextUtils.isEmpty(mResponse)) {
            return false;
        }

        XLogger.getDefault().e("---- response = " + mResponse);
        try {
            JSONObject jsonObject = new JSONObject(mResponse);
            int code = jsonObject.optInt("code");
            return  code != -1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected byte[] readPice(ITask task) throws Exception {
        return null;
    }

    @Override
    protected void writePice(byte[] datas, ITask task) throws Exception {
        MultipartBody body = mFPBuilder.addFormDataPart("image",mFile.getName(),
                FormBody.create(mFileMediaType,mFile))
                .build();
        Request request = mReqBuilder.post(body)
                .build();
        mRequestCall = OkHttpProxy.getClient()
                .newCall(request);
        Response response = mRequestCall.execute();
        if(!response.isSuccessful()) {
            throw new IllegalStateException(response.message());
        }
        mResponse = response.body().string();
        mCurrentSize = -1;
    }

    @Override
    protected void prepare(ITask task) throws Exception {
        mFile = new File(task.getSourceUrl());
        if(fileSize() > mSizeLimit ) {
            throw new IllegalStateException("file size is " + fileSize() +
                    ",but handler only can upload file size less than " + mSizeLimit + " !");
        }

        if(mFileMediaType == null) {
            String ext = getTask().getName();
            ext = ext.substring(ext.lastIndexOf(".") + 1, ext.length());
            String mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(ext);
            try {
                mFileMediaType = MediaType.parse(mimeType);
            } catch (Exception e) {
                //throw new IllegalArgumentException("you should set a media type for file part!");
                mFileMediaType = MediaType.parse("multipart/form-data");
            }
        }

        mReqBuilder = new Request.Builder()
                .url(task.getDestUrl());
        if(getHeaders() != null) {
            for (String k : getHeaders().keySet()) {
                mReqBuilder.addHeader(k, getHeaders().get(k));
            }
        }

        mFPBuilder = new MultipartBody.Builder();
        if(getParams() != null) {
            for (String k : getParams().keySet()) {
                mFPBuilder.addFormDataPart(k, getParams().get(k));
            }
        }

        mCurrentSize = mFile.length();
    }

    @Override
    protected long getPiceRealSize() {
        return mCurrentSize;
    }

    @Override
    protected int getPiceBuffSize() {
        return (int) mFile.length();
    }

    @Override
    protected long fileSize() {
        return mFile.length();
    }

    public static class Builder extends BaseTaskHandler.Builder<Builder,DefaultFormPartUploadHandler> {
        private MediaType mediaType;
        private @Speed long limitSize;

        public Builder setFileMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder setLimitSize(@Speed long limitSize) {
            this.limitSize = limitSize;
            return this;
        }

        @Override
         protected DefaultFormPartUploadHandler buildTarget() {
            DefaultFormPartUploadHandler handler = new DefaultFormPartUploadHandler();
            if (limitSize > SPEED_LIMIT_SIZE.SPEED_5MB) {
                throw new IllegalArgumentException("limit size is too large, max is " + SPEED_LIMIT_SIZE.SPEED_5MB);
            }

            if (limitSize > 0) {
                handler.mSizeLimit = limitSize;
            }
            handler.mFileMediaType = mediaType;
            return handler;
        }
    }
}
