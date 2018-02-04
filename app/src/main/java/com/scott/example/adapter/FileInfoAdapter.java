package com.scott.example.adapter;


import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scott.example.R;
import com.scott.example.moudle.FileInfo;
import com.scott.example.utils.TaskUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-21 16:31</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class FileInfoAdapter extends BaseQuickAdapter<FileInfo,BaseViewHolder>{

    public FileInfoAdapter(int layoutResId, @Nullable List<FileInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FileInfo item) {
        helper.setText(R.id.tv_name,item.name);
        Date date = new Date(item.date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        helper.setText(R.id.tv_date,sdf.format(date));
        helper.setText(R.id.tv_length, TaskUtils.getFileSize(item.length));
        if(item.type == 0) {
            helper.setImageResource(R.id.iv_icon,R.drawable.ic_folder_yellow_200_24dp);
        } else {
            helper.setImageResource(R.id.iv_icon,R.drawable.ic_insert_drive_file_grey_400_24dp);
        }
    }
}
