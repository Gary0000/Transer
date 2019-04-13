package com.scott.example.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.IExpandable;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.example.R;
import com.scott.example.moudle.TaskChildItem;
import com.scott.example.moudle.TaskGroupItem;
import com.scott.example.moudle.TaskItemType;
import com.scott.example.utils.Contacts;
import com.scott.example.utils.TaskUtils;
import com.scott.transer.Task;
import com.scott.transer.TaskCmd;
import com.scott.transer.TaskState;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.handler.ITaskHolder;

import java.util.Date;
import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-26 14:51</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *
 *      ExpandableListView 对应的适配器
 * </p>
 */

public class TaskGroupExpandAdapter extends QuickExpandableListAdapter<MultiItemEntity,BaseExpandViewHolder>{

    public TaskGroupExpandAdapter(List<MultiItemEntity> datas) {
        super(datas);
        addItemView(TaskItemType.TYPE_CHILD, R.layout.item_task_item);
        addItemView(TaskItemType.TYPE_GROUP,R.layout.item_task_group);
    }

    @Override
    protected void convert(BaseExpandViewHolder holder, MultiItemEntity item) {
        switch (holder.getItemType()) {
            case TaskItemType.TYPE_CHILD:
                onConvertChildItem(holder,item);
                break;
            case TaskItemType.TYPE_GROUP:
                onConvertGroupItem(holder,item);
                break;
        }
    }


    private void onConvertChildItem(final BaseExpandViewHolder helper, MultiItemEntity entity) {

        final ITask item = ((ITaskHolder)entity).getTask();

        helper.itemView.findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskCmd builder = new TaskCmd.Builder()
                        .setUserId(Contacts.USER_ID)
                        .setTask(item)
                        .setProcessType(ProcessType.TYPE_START_TASK)
                        .build();
                TaskEventBus.getDefault().execute(builder);
            }
        });
        helper.itemView.findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskCmd builder = new TaskCmd.Builder()
                        .setUserId(Contacts.USER_ID)
                        .setTask(item)
                        .setProcessType(ProcessType.TYPE_STOP_TASK)
                        .build();
                TaskEventBus.getDefault().execute(builder);
            }
        });
        helper.itemView.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskCmd builder = new TaskCmd.Builder()
                        .setUserId(Contacts.USER_ID)
                        .setTask(item)
                        .setProcessType(ProcessType.TYPE_DELETE_TASK)
                        .build();
                TaskEventBus.getDefault().execute(builder);
            }
        });

        helper.setText(R.id.tv_name,item.getName());
        helper.setText(R.id.tv_complete_length, TaskUtils.getFileSize(item.getCompleteLength()));
        helper.setText(R.id.tv_all_length,TaskUtils.getFileSize(item.getLength()));
        helper.setText(R.id.tv_speed,TaskUtils.getFileSize(item.getSpeed()) + "/s");

        double progress = (double)item.getCompleteLength() / (double)item.getLength();
        progress = progress * 100f;
        helper.setProgress(R.id.progress_length, (int) progress);

        helper.setVisible(R.id.btn_start,true);
        helper.setVisible(R.id.btn_stop,true);

        updateUIbyState(item.getState(),helper,item);
    }

    private void onConvertGroupItem(final BaseViewHolder helper, final MultiItemEntity item) {

        final ITask task = ((ITaskHolder)item).getTask();
        final TaskGroupItem groupItem = (TaskGroupItem) item;

        helper.setText(R.id.tv_title,task.getGroupName());
        helper.setText(R.id.tv_count,"等" + groupItem.getSubItems().size() + "项");
        helper.setText(R.id.tv_size,TaskUtils.getFileSize(groupItem.getCompleteSize()) + "/" + TaskUtils.getFileSize(groupItem.getAllSize()));
        helper.setText(R.id.tv_leave_count,groupItem.getLeaveCount() + "/" + groupItem.getAllCount());
        if(task.getState() == TaskState.STATE_FINISH) {
            helper.getView(R.id.btn_start).setVisibility(View.INVISIBLE);
        } else {
            helper.getView(R.id.btn_start).setVisibility(View.VISIBLE);
        }

        if(task.getState() == TaskState.STATE_RUNNING || task.getState() == TaskState.STATE_READY) {
            helper.setText(R.id.btn_start,"暂停");
            helper.getView(R.id.btn_start).setSelected(false);
        } else {
            helper.setText(R.id.btn_start,"开始");
            helper.getView(R.id.btn_start).setSelected(true);
        }

        helper.itemView.findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TaskCmd builder = new TaskCmd.Builder()
                        .setTaskType(task.getType())
                        .setUserId(Contacts.USER_ID)
                        .setGroupId(task.getGroupId())
                        .setProcessType(helper.getView(R.id.btn_start).isSelected() ?
                                ProcessType.TYPE_START_GROUP : ProcessType.TYPE_STOP_GROUP)
                        .build();
                TaskEventBus.getDefault().execute(builder);
            }
        });

        helper.itemView.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskCmd builder = new TaskCmd.Builder()
                        .setTaskType(task.getType())
                        .setUserId(Contacts.USER_ID)
                        .setGroupId(task.getGroupId())
                        .setProcessType(ProcessType.TYPE_DELETE_TASKS_GROUP)
                        .build();
                TaskEventBus.getDefault().execute(builder);
            }
        });

        helper.itemView.findViewById(R.id.btn_clear_complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskCmd builder = new TaskCmd.Builder()
                        .setTaskType(task.getType())
                        .setUserId(Contacts.USER_ID)
                        .setGroupId(task.getGroupId())
                        .setProcessType(ProcessType.TYPE_DELETE_TASKS_COMPLETED)
                        .build();
                TaskEventBus.getDefault().execute(builder);
            }
        });
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
