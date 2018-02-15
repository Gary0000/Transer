package com.scott.example.adapter;


import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scott.annotionprocessor.TaskType;
import com.scott.example.R;
import com.scott.example.moudle.FileInfo;
import com.scott.example.utils.TaskUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-21 16:31</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class FileInfoAdapter extends BaseQuickAdapter<FileInfo,BaseViewHolder>  {

    private List<FileInfo> mSelectItems = new ArrayList<>();
    private boolean isSelectMode = false;
    private OnSelectChangeListenner mSelectListenner;

    public void setOnSelectChangeListenner(OnSelectChangeListenner l) {
        mSelectListenner = l;
    }

    public void selectAll() {
        mSelectItems.clear();
        for(FileInfo info : getData()) {
            if(info.type == FileInfo.FILE_TYPE.FILE) {
                mSelectItems.add(info);
            }
        }
        notifyDataSetChanged();
    }

    public void cancelSelect() {
        mSelectItems.clear();
        notifyDataSetChanged();
    }

    public void setSelectMode(boolean isSelectMode) {
        this.isSelectMode = isSelectMode;
        notifyDataSetChanged();
    }
    public FileInfoAdapter(int layoutResId, @Nullable List<FileInfo> data) {
        super(layoutResId, data);
    }

    public List<FileInfo> getCheckedItems() {
        return mSelectItems;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final FileInfo item) {
        helper.setText(R.id.tv_name,item.name);
        Date date = new Date(item.date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        helper.setText(R.id.tv_date,sdf.format(date));
        helper.setText(R.id.tv_length, TaskUtils.getFileSize(item.length));
        helper.getView(R.id.check_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()) {
                    mSelectItems.add(item);
                } else {
                    mSelectItems.remove(item);
                }
                mSelectListenner.onChanged(mSelectItems.size());
            }
        });


        if(item.type == FileInfo.FILE_TYPE.DIRECTORY) {
            helper.setImageResource(R.id.iv_icon,R.drawable.ic_folder_yellow_200_24dp);
            helper.setVisible(R.id.check_box,false);
        } else {
            helper.setVisible(R.id.check_box,isSelectMode ? true : false);
            helper.setImageResource(R.id.iv_icon,R.drawable.ic_insert_drive_file_grey_400_24dp);
            helper.setChecked(R.id.check_box,isSelectMode && mSelectItems.contains(item) ? true : false);
        }
    }

    public interface OnSelectChangeListenner {
        void onChanged(int selectOut);
    }
}
