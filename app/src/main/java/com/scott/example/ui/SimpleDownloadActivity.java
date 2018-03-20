package com.scott.example.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scott.annotionprocessor.ITask;
import com.scott.example.BaseActivity;
import com.scott.example.R;
import com.scott.example.utils.Contacts;
import com.scott.example.utils.TaskUtils;
import com.scott.transer.SimpleTaskHandlerListenner;
import com.scott.transer.Task;
import com.scott.transer.handler.DefaultHttpDownloadHandler;
import com.scott.transer.handler.ITaskHandler;
import com.scott.transer.utils.Debugger;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SimpleDownloadActivity extends BaseActivity {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_complete_length)
    TextView tvCompleteLength;

    @BindView(R.id.tv_all_length)
    TextView tvAllLength;

    @BindView(R.id.progress_length)
    ProgressBar progressLength;

    @BindView(R.id.btn_start)
    Button btnStart;

    @BindView(R.id.btn_stop)
    Button btnStop;

    @BindView(R.id.tv_md5)
    TextView tvMd5;

    @BindView(R.id.tv_md5_new)
    TextView tvNewMd5;

    @BindView(R.id.tv_equals)
    TextView tvEquals;

    @BindView(R.id.tv_speed)
    TextView tvSpeed;

    private ITaskHandler mHandler;

    final String FILE_MD5 = "de37fe1c8f049bdd83090d40f806cd67";
    final String TAG = SimpleDownloadActivity.class.getSimpleName();
    final String FILE_NAME = "test.zip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_download);

        ButterKnife.bind(this);
        tvMd5.setText(FILE_MD5);

        //创建一个任务
        ITask task = new Task.Builder()
                .setName(FILE_NAME) //设置任务名称
                .setSourceUrl(Contacts.API.getUrl(Contacts.API.DOWNLOAD_URL))  //设置数据源
                .setDestUrl(Contacts.LOCAL_STORAGE.getBaseSavePath()) //设置目标路径
                .build();

        mHandler = new DefaultHttpDownloadHandler.Builder()
                .setTask(task)
                .addParam("path",FILE_NAME)
                //.setSpeedLimited(BaseTaskHandler.SPEED_LIMIT_SIZE.SPEED_1MB)
                .setCallback(new DownloadListener())
                .runOnNewThread()
                .setEnableCoverFile(true)
                .build();
        setTitle(getString(R.string.simple_download));
    }

    @OnClick(R.id.btn_stop)
    public void stop() {
        mHandler.stop();
    }

    @OnClick(R.id.btn_start)
    public void start() {
        mHandler.start();
    }

    private final class DownloadListener extends SimpleTaskHandlerListenner {

        @Override
        public void onPiceSuccessful(final ITask params) {

            //Debugger.error(TAG,"finished === " + params);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvCompleteLength.setText(TaskUtils.getFileSize(params.getCompleteLength()));
                    tvAllLength.setText(TaskUtils.getFileSize(params.getLength()));

                    double progress = (double)params.getCompleteLength() / (double)params.getLength();
                    progress = progress * 100f;
                    progressLength.setProgress((int) progress);
                    tvName.setText(params.getName());
                }
            });
        }

        @Override
        public void onError(int code, ITask params) {
            super.onError(code, params);
            Debugger.error(TAG,"error === " + params);
        }

        @Override
        public void onFinished(final ITask task) {
            Debugger.error(TAG,"finished === " + task);
            super.onFinished(task);
            final String newMd5 = TaskUtils.getFileMD5(new File(Contacts.LOCAL_STORAGE.getBaseSavePath() + File.separator + FILE_NAME));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvCompleteLength.setText(TaskUtils.getFileSize(task.getCompleteLength()));
                    tvAllLength.setText(TaskUtils.getFileSize(task.getLength()));

                    double progress = (double)task.getCompleteLength() / (double)task.getLength();
                    progress = progress * 100f;
                    progressLength.setProgress((int) progress);
                    tvNewMd5.setText(newMd5);
                    tvEquals.setText(TextUtils.equals(newMd5,FILE_MD5) + "");
                }
            });
        }

        @Override
        public void onStop(ITask params) {
            super.onStop(params);
            Debugger.error("OnlyDownloadActivity","stop ==========");
        }

        @Override
        public void onSpeedChanged(long speed, final ITask params) {
            super.onSpeedChanged(speed, params);
            Debugger.error("OnlyDownloadActivity","speed = " + TaskUtils.getFileSize(speed) + "/s");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvSpeed.setText(TaskUtils.getFileSize(params.getSpeed()));
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandler != null) {
            mHandler.stop();
        }
    }
}
