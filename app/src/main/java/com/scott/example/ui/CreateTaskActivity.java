package com.scott.example.ui;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.TaskSubscriber;
import com.scott.annotionprocessor.TaskType;
import com.scott.annotionprocessor.ThreadMode;
import com.scott.example.BaseActivity;
import com.scott.example.R;
import com.scott.example.utils.Contacts;
import com.scott.transer.ITaskCmd;
import com.scott.transer.TaskBuilder;
import com.scott.transer.TaskCmdBuilder;
import com.scott.transer.event.TaskEventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class CreateTaskActivity extends BaseActivity {

    @BindView(R.id.edit_source)
    EditText editPath;

    @BindView(R.id.edit_dest)
    EditText editUrl;

    @BindView(R.id.rg_type)
    RadioGroup radioGroup;

    private TaskType task_type = TaskType.TYPE_HTTP_UPLOAD;

    final String NAME = "test.zip";
    final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().toString() + File.separator + NAME;
    final String DOWNLOAD_URL = "http://" + Contacts.TEST_HOST + "/WebDemo/DownloadManager";
    ;
    final String UPLOAD_PATH = DOWNLOAD_PATH;
    final String UPLOAD_URL = "http://" + Contacts.TEST_HOST + "/WebDemo/UploadManager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        ButterKnife.bind(this);

        switch (task_type) {
            case TYPE_HTTP_DOWNLOAD:
                editPath.setText(DOWNLOAD_PATH);
                editUrl.setText(DOWNLOAD_URL);
                break;
            case TYPE_HTTP_UPLOAD:
                editPath.setText(UPLOAD_PATH);
                editUrl.setText(UPLOAD_URL);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TaskEventBus.getDefault().regesit(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TaskEventBus.getDefault().unregesit(this);
    }

    @TaskSubscriber(taskType = TaskType.TYPE_HTTP_DOWNLOAD,threadMode = ThreadMode.MODE_MAIN)
    public void onAddTask() {
        Toast.makeText(this,"下载任务添加成功!",Toast.LENGTH_SHORT).show();
    }

    @TaskSubscriber(taskType = TaskType.TYPE_HTTP_UPLOAD,threadMode = ThreadMode.MODE_MAIN)
    public void onAddUploadTask() {
        Toast.makeText(this,"上传任务添加成功!",Toast.LENGTH_SHORT).show();
    }

    @OnCheckedChanged(R.id.rb_upload)
    public void uploadCheck(boolean checked) {
        if(checked) {
            editPath.setText(UPLOAD_PATH);
            editUrl.setText(UPLOAD_URL);
        }
    }

    @OnCheckedChanged(R.id.rb_download)
    public void downloadCheck(boolean checked) {
        if(checked) {
            editUrl.setText(DOWNLOAD_URL);
            editPath.setText(DOWNLOAD_PATH);
        }
    }

    @OnClick(R.id.btn_create_task)
    public void createTask() {

        String source = null;
        String dest = null;

        if(radioGroup.getCheckedRadioButtonId() == R.id.rb_upload) {
            task_type = TaskType.TYPE_HTTP_UPLOAD;
        } else {
            task_type = TaskType.TYPE_HTTP_DOWNLOAD;
        }

        switch (task_type) {
            case TYPE_HTTP_UPLOAD:
                source = editPath.getText().toString();
                dest = editUrl.getText().toString();
                break;
            case TYPE_HTTP_DOWNLOAD:
                dest = editPath.getText().toString();
                source = editUrl.getText().toString();
                break;
        }


        ITask task = new TaskBuilder()
                .setTaskType(task_type)
                .setDataSource(source)
                .setDestSource(dest)
                .setName(NAME)
                .build();

        ITaskCmd cmd = new TaskCmdBuilder()
                .setTaskType(task_type)
                .setProcessType(ProcessType.TYPE_ADD_TASK)
                .setTask(task)
                .build();

        TaskEventBus.getDefault().execute(cmd);
    }
}
