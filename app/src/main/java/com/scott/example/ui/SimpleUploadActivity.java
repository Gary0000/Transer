package com.scott.example.ui;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scott.annotionprocessor.ITask;
import com.scott.example.BaseActivity;
import com.scott.example.R;
import com.scott.example.utils.Contacts;
import com.scott.example.utils.TaskUtils;
import com.scott.transer.SimpleTaskHandlerListenner;
import com.scott.transer.TaskBuilder;
import com.scott.transer.handler.DefaultHttpUploadHandler;
import com.scott.transer.handler.ITaskHandler;
import com.scott.transer.utils.Debugger;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.scott.example.utils.TaskUtils.getFileSize;

public class SimpleUploadActivity extends BaseActivity {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_complete_length)
    TextView tvCompleteLength;

    @BindView(R.id.tv_all_length)
    TextView tvAllLength;

    @BindView(R.id.progress_length)
    ProgressBar progressLength;

    @BindView(R.id.tv_md5)
    TextView tvMd5;

    @BindView(R.id.tv_md5_new)
    TextView tvNewMd5;

    @BindView(R.id.tv_equals)
    TextView tvEquals;

    @BindView(R.id.tv_speed)
    TextView tvSpeed;

    private ITaskHandler mHandler;
    private ITask task;

    final String TAG = SimpleUploadActivity.class.getSimpleName();
    final String FILE_NAME = "test.zip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_download);

        ButterKnife.bind(this);

        task = new TaskBuilder()
                .setName(FILE_NAME)
                .setTaskId("1233444")
                .setSessionId("123123123131")
                .setSourceUrl(Contacts.LOCAL_STORAGE.getBaseSavePath() + File.separator + FILE_NAME)
                .setDestUrl(Contacts.API.getUrl(Contacts.API.UPLOAD_URL))
                .build();

        mHandler = new DefaultHttpUploadHandler.Builder()
                .setTask(task)
                .addHeader("path","upload")
                .setCallback(new UploadListenner())
                .runOnNewThread()
                .build();
        setTitle(getString(R.string.simple_upload));
    }

    private final class UploadListenner extends SimpleTaskHandlerListenner {
        @Override
        public void onPiceSuccessful(final ITask params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvCompleteLength.setText(getFileSize(params.getCompleteLength()));
                    tvAllLength.setText(getFileSize(params.getLength()));

                    double progress = (double)params.getCompleteLength() / (double)params.getLength();
                    progress = progress * 100f;
                    progressLength.setProgress((int) progress);
                }
            });
        }

        @Override
        public void onFinished(final ITask task) {
            super.onFinished(task);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvCompleteLength.setText(getFileSize(task.getCompleteLength()));
                    tvAllLength.setText(getFileSize(task.getLength()));

                    double progress = (double)task.getCompleteLength() / (double)task.getLength();
                    progress = progress * 100f;
                    progressLength.setProgress((int) progress);
                }
            });
            Debugger.error(TAG,"========onFinished============");
        }

        @Override
        public void onStop(ITask params) {
            super.onStop(params);
            Debugger.error("OnlyDownloadActivity","stop ==========");
        }


        @Override
        public void onSpeedChanged(long speed, final ITask params) {
            super.onSpeedChanged(speed, params);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvCompleteLength.setText(getFileSize(params.getCompleteLength()));
                    tvAllLength.setText(getFileSize(params.getLength()));

                    double progress = (double)params.getCompleteLength() / (double)params.getLength();
                    progress = progress * 100f;
                    progressLength.setProgress((int) progress);
                    tvSpeed.setText(TaskUtils.getFileSize(task.getSpeed()));
                }
            });
            Debugger.error("OnlyDownloadActivity","speed = " + getFileSize(speed) + "/s");
        }

        @Override
        public void onError(int code, ITask params) {
            super.onError(code, params);
            Debugger.error("SimpleUploadActivity","error " + code);
        }
    }

    @OnClick(R.id.btn_stop)
    public void stop() {
        mHandler.stop();
    }


    @OnClick(R.id.btn_start)
    public void start() {
        mHandler.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandler != null) {
            mHandler.stop();
        }
    }
}
