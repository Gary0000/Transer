package com.scott.transer.handler;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.http.OkHttpProxy;
import com.shilec.xlogger.XLogger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 11:59</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *  默认的http upload handler
 * </p>
 */

public class DefaultHttpUploadHandler extends BaseTaskHandler {

    private RandomAccessFile mFile;
    private String mResponse;     //返回数据
    private int mPiceRealSize = 0; //每一片的实际大小
    private PiceRequestBody mRequestBody; // 写入一片
    private final String TAG = DefaultHttpUploadHandler.class.getSimpleName();
    private Call mCurrentCall;

    /***
     * 当前这一片传输完成服务器返回的数据
     * @return
     */
    protected String getNowResponse() {
        return mResponse;
    }


    /***
     * 判断一片是否上传成功，需要通过服务器的返回值去判断
     * 注意:
     * 最后一片上传完也会去判断 一片是否上传成功
     * 所以需要考虑最后一片返回和每一片返回不同。
     * @return true 成功， false 失败
     */
    @Override
    protected boolean isPiceSuccessful() { //判断一片是否成功
//        if(getNowResponse() == null || isSuccessful()) {
//            return false;
//        }
        return true;
    }

    /***
     * 最后一片上传完会被调用，需要判断最后一片上传成功后服务器返回值
     * @return true 成功， false 失败
     */
    @Override
    protected boolean isSuccessful() {
//        if(getNowResponse() == null) {
//            return false;
//        }
//
//        try {
//            JSONObject job = new JSONObject(mResponse);
//            String range = job.optString("range");
//            String end = range.split("-")[0];
//            String all = range.split("-")[1];
//
//            if(Long.parseLong(end) - 1 >= Long.parseLong(all)) {
//                return true;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            XLogger.getDefault().e(TAG,e.getMessage());
//        }
//        return false;
        return true;
    }

    @Override
    public TaskType getType() {
        return TaskType.TYPE_HTTP_UPLOAD;
    }

    @Override
    protected byte[] readPice(ITask task) throws IOException{
        byte[] datas = new byte[getPiceBuffSize()];
        mPiceRealSize = mFile.read(datas,0, getPiceBuffSize());
        return datas;
    }

    private String encodeName(String name) {
        try {
            return URLEncoder.encode(name,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void writePice(byte[] datas, ITask task) throws IOException{

        //服务端需要支持 Content-Range 的 header
        mRequestBody = new PiceRequestBody(datas);
        Request.Builder builder = new Request.Builder()
                .addHeader("Session-ID", task.getSesstionId())
                .addHeader("Content-Range", "bytes " + task.getStartOffset()
                        + "-" + (task.getEndOffset() - 1) + "/" + mFile.length())
                .addHeader("Content-Disposition", "attachment; filename=" + encodeName(task.getName()))
                .addHeader("Connection", "Keep-Alive")
                .url(task.getDestUrl())
                .post(mRequestBody);
        //加入header
        if(getHeaders() != null) {
            for (String k : getHeaders().keySet()) {
                builder.addHeader(k, getHeaders().get(k));
            }
        }

        Request request = builder.build();
        OkHttpClient client = OkHttpProxy.getClient();
        mCurrentCall = client.newCall(request);

        mResponse = null;
        //XLogger.getDefault().e(TAG,"wait response === ");
        Response execute = mCurrentCall.execute();
        //XLogger.getDefault().e(TAG,"wait2 response === ");
        if(!execute.isSuccessful()) {
            XLogger.getDefault().e(TAG,"error msg = " + execute.body().string());
            throw new IllegalStateException(execute.message());
        }
        ResponseBody body = execute.body();
        mResponse = body.string();
        XLogger.getDefault().e(TAG,"response === " + mResponse);
    }

    @Override
    public void stop() {
        if(mCurrentCall != null) {
            mCurrentCall.cancel();
        }
        super.stop();
    }

    @Override
    protected void release() {
        super.release();
        try {
            mFile.close();
            mRequestBody = null;
            mResponse = null;
            mPiceRealSize = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void prepare(ITask task) throws IOException{
        mFile = new RandomAccessFile(task.getSourceUrl(),"r");
        //将文件指针移动到上次传输的位置
        mFile.seek(task.getCompleteLength());
    }

    @Override
    protected long getPiceRealSize() {
        return mPiceRealSize;
    }

    @Override
    protected long fileSize() {
        try {
            return mFile.length();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected long getCurrentCompleteLength() {
        if(mRequestBody == null) {
            return super.getCurrentCompleteLength();
        }
        return super.getCurrentCompleteLength() + mRequestBody.mCurrentCompleteLength;
    }

    protected class PiceRequestBody extends RequestBody {

        private ByteArrayInputStream mSource; //当前需要传输的一片
        private int mCurrentCompleteLength; //当前已经完成的长度，写入多少增加多少

        PiceRequestBody(byte[] datas) {
            mSource = new ByteArrayInputStream(datas,0, (int) getPiceRealSize());
        }

        @Override
        public long contentLength() throws IOException {
            //需要指定此次请求的内容长度，以从数据圆中实际读取的长度为准
            XLogger.getDefault().e(TAG,"offset = " + getTask().getStartOffset() + ", end = " + getTask().getEndOffset());
            return getPiceRealSize();
        }

        @Override
        public MediaType contentType() {
            //服务器支持的contenttype 类型
            return MediaType.parse("application/octet-stream");
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            byte[] buf = new byte[8192];
            int len = 0;

            //这里这样处理是由于可以得到进度的连续变化数值，而不需要等到一片传完才等获取已经传输的长度
            while((len = mSource.read(buf)) != -1) {
                sink.write(buf,0,len);
                sink.flush();
                mCurrentCompleteLength += len;
            }

            mSource.reset();
            mSource.close();
        }
    }

    public static class Builder extends BaseTaskHandler.Builder<Builder,DefaultHttpUploadHandler> {
        @Override
        protected DefaultHttpUploadHandler buildTarget() {
            return new DefaultHttpUploadHandler();
        }
    }
}
