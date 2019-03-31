package com.scott.transer.handler;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.Task;
import com.scott.transer.http.OkHttpProxy;
import com.shilec.xlogger.XLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2017/12/16</P>
 * <P>Email: shilec@126.com</p>
 */

public class DefaultHttpDownloadHandler extends BaseTaskHandler {

    protected RandomAccessFile mFile;
    protected InputStream mInputStream;
    protected int mPiceSize = 0;
    protected long mFileSize = 0;
    final String TAG = DefaultHttpDownloadHandler.class.getSimpleName();
    protected long mLimitSpeed;
    protected boolean isCoverOldFile = true;
    private Call mCurrentCall;

    public void enableCoverFile() {
        isCoverOldFile = true;
    }

    public void setSpeedLimited(long speed) {
        mLimitSpeed = speed;
    }

    @Override
    protected boolean isPiceSuccessful() {
        return true;
    }

    @Override
    protected boolean isSuccessful() {
        return getTask().getLength() == getTask().getCompleteLength() && getTask().getLength() != 0;
    }

    @Override
    protected void release() {
        try {
            mFile.close();
            mInputStream.close();
            //mFile = null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public TaskType getType() {
        return TaskType.TYPE_HTTP_DOWNLOAD;
    }

    @Override
    protected byte[] readPice(ITask task) throws IOException {
        if (mInputStream == null) {
            return null;
        }
        byte[] buf = new byte[getPiceBuffSize()];
        mPiceSize = mInputStream.read(buf);
        return buf;
    }

    @Override
    protected void writePice(byte[] datas, ITask task) throws IOException {
        mFile.write(datas, 0, (int) getPiceRealSize());
    }

    @Override
    protected void prepare(ITask task) throws Exception {

        //设置url 参数
        String url = getTask().getSourceUrl();
        if (getParams() != null) {
            for (String k : getParams().keySet()) {
                if (!url.contains("?")) {
                    url += "?" + k + "=" + getParams().get(k);
                } else {
                    url += "&" + k + "=" + getParams().get(k);
                }
            }
        }

        File file = new File(getTask().getDestUrl()
                + File.separator + getTask().getName());
        mFileSize = getNetSize(url); //从服务端获取文件大小
        if(mFileSize == 0) {
            return;
        }

        if (file.length() == mFileSize && mFileSize != 0) {
            //if local exists and is completed,params contains cover-file -> true
            //delete file, else return finished.
            if (isCoverOldFile) {
                file.delete();
            } else {
                return;
            }

        }
        mFile = new RandomAccessFile(file, "rw");

        //将文件指针移动到末尾
        if (task.getStartOffset() != 0) {
            mFile.seek(task.getStartOffset());
        }

        XLogger.getDefault().e(DefaultHttpDownloadHandler.class.getSimpleName(),
                "=================== fileLength = " + mFile.length() + "," +
                        task.getStartOffset());
        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("Range", "bytes=" +
                        task.getStartOffset() + "-" + mFileSize);

        if (getHeaders() != null) {
            for (String k : getHeaders().keySet()) {
                builder.addHeader(k, getHeaders().get(k));
            }
        }
        OkHttpClient client = OkHttpProxy.getClient();
        mCurrentCall = client.newCall(builder.build());
        Response response = mCurrentCall.execute();
        if (!response.isSuccessful()) {
            return;
        }
        ResponseBody body = response.body();
        mInputStream = body.byteStream();
    }

    @Override
    public void stop() {
        // crash #12
        try {
            if (mCurrentCall != null) {
                mCurrentCall.cancel();
            }
            super.stop();
        } catch (Exception e) {}
    }

    @Override
    protected long getPiceRealSize() {
        return mPiceSize;
    }

    @Override
    protected long fileSize() {
        return mFileSize;
    }

    protected long getNetSize(String src) throws Exception {
        Request request = new Request.Builder()
                .url(src)
                .head()
                .build();
        OkHttpClient client = OkHttpProxy.getClient();
        mCurrentCall = client.newCall(request);

        Response response = mCurrentCall.execute();
        if(!response.isSuccessful()) {
            return -1;
        }
        String header = response.header("Content-Length");
        if(header == null) {
            throw new IllegalStateException("Please check whether the server supports the HEAD request!");
        }
        long length = Long.parseLong(header);
        return length;
    }

    @Override
    protected long getLimitSpeed() {
        if(mLimitSpeed <= 0) {
            return SPEED_LIMIT_SIZE.SPEED_UNLIMITED;
        }
        return mLimitSpeed;
    }

    public static class Builder extends BaseTaskHandler.Builder<Builder,DefaultHttpDownloadHandler> {

        private boolean isEnableCoverfile;
        private long mSpeedLimited;

        public Builder setSpeedLimited(long limited) {
            mSpeedLimited = limited;
            return this;
        }

        public Builder setEnableCoverFile(boolean enable){
            isEnableCoverfile = enable;
            return this;
        }

        @Override
        protected DefaultHttpDownloadHandler buildTarget() {
            DefaultHttpDownloadHandler handler = new DefaultHttpDownloadHandler();
            handler.isCoverOldFile = isEnableCoverfile;
            handler.mLimitSpeed = mSpeedLimited;
            return handler;
        }
    }
}
