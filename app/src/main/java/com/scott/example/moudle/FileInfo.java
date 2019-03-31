package com.scott.example.moudle;

import android.text.TextUtils;

import com.scott.example.adapter.IListData;

import java.io.Serializable;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2018/2/3</P>
 * <P>Email: shilec@126.com</p>
 */

public class FileInfo implements IListData,Serializable{
    public String name;

    public String path;

    public int type;

    public long length;

    public long date;

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof FileInfo)) {
            return false;
        }
        FileInfo info = (FileInfo) obj;
        return TextUtils.equals(info.path,info.path);
    }

    public interface FILE_TYPE {
        int DIRECTORY = 0;
        int FILE = 1;
    }
}
