package com.scott.example.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.example.R;
import com.scott.example.moudle.TaskGroupItem;
import com.scott.example.utils.TaskUtils;
import com.scott.transer.TaskCmd;
import com.scott.transer.TaskState;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.handler.ITaskHolder;

import java.util.Date;
import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-21 12:49</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class TaskGroupAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity,BaseViewHolder> implements
        BaseQuickAdapter.OnItemChildClickListener{

    public static final int TYPE_GROUP = 0;

    public static final int TYPE_CHILD = 1;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public TaskGroupAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_CHILD, R.layout.item_task_item);
        addItemType(TYPE_GROUP,R.layout.item_task_group);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case TYPE_CHILD:
                onConvertChildItem(helper,item);
                break;
            case TYPE_GROUP:
                onConvertGroupItem(helper,item);
                break;
        }
    }

    private void onConvertChildItem(final BaseViewHolder helper, MultiItemEntity entity) {

        ITask item = ((ITaskHolder)entity).getTask();

        helper.itemView.findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskCmd builder = new TaskCmd.Builder()
                        .setTask(((ITaskHolder)getItem(helper.getAdapterPosition())).getTask())
                        .setProcessType(ProcessType.TYPE_START_TASK)
                        .build();
                TaskEventBus.getDefault().execute(builder);
            }
        });
        helper.itemView.findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskCmd builder = new TaskCmd.Builder()
                        .setTask(((ITaskHolder)getItem(helper.getAdapterPosition())).getTask())
                        .setProcessType(ProcessType.TYPE_STOP_TASK)
                        .build();
                TaskEventBus.getDefault().execute(builder);
            }
        });
        helper.itemView.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskCmd builder = new TaskCmd.Builder()
                        .setTask(((ITaskHolder)getItem(helper.getAdapterPosition())).getTask())
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

        ITask task = ((ITaskHolder)item).getTask();
        final TaskGroupItem groupItem = (TaskGroupItem) item;

        helper.setText(R.id.tv_title,task.getGroupId());
        helper.setText(R.id.tv_count,groupItem.getSubItems().size() + "");
        helper.setText(R.id.tv_group_info,task.getGroupName());

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupItem.isExpanded()) {
                    collapse(helper.getAdapterPosition(),false);
                } else {
                    expand(helper.getAdapterPosition(), false);
                }
            }
        });
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        TaskCmd.Builder builder = new TaskCmd.Builder()
                .setTask(((ITaskHolder)getItem(position)).getTask());
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
