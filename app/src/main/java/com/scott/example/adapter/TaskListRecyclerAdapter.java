package com.scott.example.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.example.R;
import com.scott.example.utils.TaskUtils;
import com.scott.transer.ITaskCmdBuilder;
import com.scott.transer.TaskCmdBuilder;
import com.scott.transer.TaskState;
import com.scott.transer.event.TaskEventBus;

import java.util.Date;
import java.util.List;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2018/2/4</P>
 * <P>Email: shilec@126.com</p>
 */

public class TaskListRecyclerAdapter extends BaseQuickAdapter<ITask,BaseViewHolder> implements BaseQuickAdapter.OnItemChildClickListener{

    public TaskListRecyclerAdapter(int layoutResId, @Nullable List<ITask> data) {
        super(layoutResId, data);
        setOnItemChildClickListener(this);
    }

    @Override
    protected void convert(BaseViewHolder helper, ITask item) {
        helper.addOnClickListener(R.id.btn_start);
        helper.addOnClickListener(R.id.btn_stop);
        helper.addOnClickListener(R.id.btn_delete);

        helper.setText(R.id.tv_name,item.getName());
        helper.setText(R.id.tv_complete_length,TaskUtils.getFileSize(item.getCompleteLength()));
        helper.setText(R.id.tv_all_length,TaskUtils.getFileSize(item.getLength()));
        helper.setText(R.id.tv_speed,TaskUtils.getFileSize(item.getSpeed()) + "/s");

        double progress = (double)item.getCompleteLength() / (double)item.getLength();
        progress = progress * 100f;
        helper.setProgress(R.id.progress_length, (int) progress);

        helper.setVisible(R.id.btn_start,true);
        helper.setVisible(R.id.btn_stop,true);

        updateUIbyState(item.getState(),helper,item);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        ITaskCmdBuilder builder = new TaskCmdBuilder()
                .setTask(getItem(position));
        switch (view.getId()) {
            case R.id.btn_start:
                builder.setProcessType(ProcessType.TYPE_START_TASK);
                break;
            case R.id.btn_stop:
                builder.setProcessType(ProcessType.TYPE_STOP_TASK);
                break;
            case R.id.btn_delete:
                builder.setProcessType(ProcessType.TYPE_DELETE_TASK);
                break;
        }
        TaskEventBus.getDefault().execute(builder.build());
    }

    private void updateUIbyState(int state, BaseViewHolder helper, ITask task) {
        switch (state) {
            case TaskState.STATE_ERROR:
                helper.getView(R.id.btn_start).setEnabled(true);
                helper.setText(R.id.tv_all_length,helper.getView(R.id.tv_all_length).getContext().getString(R.string.task_failed));
                helper.setText(R.id.tv_speed,"");
                helper.setText(R.id.tv_complete_length,"");
                helper.getView(R.id.btn_stop).setEnabled(false);
                break;
            case TaskState.STATE_READY:
                helper.setText(R.id.tv_all_length,helper.getView(R.id.tv_all_length).getContext().getString(R.string.task_wait));
                helper.setText(R.id.tv_speed,"");
                helper.setText(R.id.tv_complete_length,"");
                helper.getView(R.id.btn_stop).setEnabled(false);
                helper.getView(R.id.btn_start).setEnabled(false);
                break;
            case TaskState.STATE_STOP:
                helper.getView(R.id.btn_stop).setEnabled(false);
                helper.getView(R.id.btn_start).setEnabled(true);
                break;
            case TaskState.STATE_FINISH:
                helper.setVisible(R.id.btn_start,false);
                helper.setVisible(R.id.btn_stop,false);
                helper.setText(R.id.tv_all_length,new Date(task.getCompleteTime()).toString());
                helper.setText(R.id.tv_speed,"");
                helper.setProgress(R.id.progress_length, 100);
                break;
            case TaskState.STATE_RUNNING:
                helper.getView(R.id.btn_stop).setEnabled(true);
                helper.getView(R.id.btn_start).setEnabled(false);
                break;

        }
    }
}
