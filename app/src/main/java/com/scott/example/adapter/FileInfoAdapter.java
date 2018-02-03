package com.scott.example.adapter;


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

public class FileInfoAdapter extends BaseQuckListAdapter<FileInfo,BaseListHolder> {
    public FileInfoAdapter(List<FileInfo> datas) {
        super(datas);
        addItemView(0,R.layout.item_file_info);
    }

    @Override
    public void convert(BaseListHolder holder, int position, FileInfo item) {
        holder.setText(R.id.tv_name,item.name);
        Date date = new Date(item.date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.setText(R.id.tv_date,sdf.format(date));
        holder.setText(R.id.tv_length, TaskUtils.getFileSize(item.length));
        if(item.type == 0) {
            holder.setImage(R.id.iv_icon,R.drawable.ic_folder_yellow_200_24dp);
        } else {
            holder.setImage(R.id.iv_icon,R.drawable.ic_insert_drive_file_grey_400_24dp);
        }
    }
}
