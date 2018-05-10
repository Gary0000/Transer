package com.scott.example.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.scott.annotionprocessor.TaskType;
import com.scott.example.BaseActivity;
import com.scott.example.R;
import com.scott.example.utils.Contacts;
import com.shilec.xlogger.XLogger;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    SelectUserDialog mDialog;
    int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        AndPermission.with(this)
                .permission(Permission.STORAGE)
                .callback(this)
                .start();
        setTitle("Transer");

        getSwitchUserView().setVisibility(View.VISIBLE);
        getSwitchUserView().setText(Contacts.USER_ID);
        getSwitchUserView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSwitchUserDialog();
            }
        });
    }

    private void showSwitchUserDialog() {
        mDialog = new SelectUserDialog(MainActivity.this, Contacts.TEMP_USERS,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == AlertDialog.BUTTON_POSITIVE) {
                            getSwitchUserView().setText(mDialog.getCurrentName());
                            Contacts.USER_ID = mDialog.getCurrentName();
                            mCurrentIndex = mDialog.getCurrentIndex();
                        }
                    }},mCurrentIndex);
        mDialog.show();
    }


    @OnClick(R.id.btn_simple_download)
    public void onSimpleDownload() {
        startActivity(new Intent(this,SimpleDownloadActivity.class));
    }

    @OnClick(R.id.btn_simple_upload)
    public void onSimpleUpload() {
        startActivity(new Intent(this,SimpleUploadActivity.class));
    }

    @OnClick(R.id.btn_upload_tasks)
    public void showUploadTasks() {
        Intent intent = new Intent(this,SimpleTaskListActivity.class);
        intent.putExtra(TaskFragment.EXTRA_TASK_TYPE,TaskType.TYPE_HTTP_UPLOAD);
        startActivity(intent);
    }

    @OnClick(R.id.btn_download_tasks)
    public void showDownloadTasks() {
        Intent intent = new Intent(this,SimpleTaskListActivity.class);
        intent.putExtra(TaskFragment.EXTRA_TASK_TYPE,TaskType.TYPE_HTTP_DOWNLOAD);
        startActivity(intent);
    }

    @OnClick(R.id.btn_file_list)
    public void showServerFileList() {
        Intent intent = new Intent(this,FileListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_local_file_list)
    public void showLocalFileList() {
        Intent intent = new Intent(this,FileListActivity.class);
        intent.putExtra(FileListActivity.EXTRA_IS_LOCAL,true);
        startActivity(intent);
    }

    @OnClick(R.id.btn_event_bus)
    public void showEventBusDemo() {
        Intent intent = new Intent(this,SimpleTaskEventBusActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_create_task)
    public void createTask() {
        startActivity(new Intent(this,CreateTaskActivity.class));
    }

    @OnClick(R.id.btn_task_group)
    public void taskGroupManage() {
        startActivity(new Intent(this,TaskGroupActivity.class));
    }
}
