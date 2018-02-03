package com.scott.example.moudle;

import com.scott.example.adapter.IListData;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2018/2/3</P>
 * <P>Email: shilec@126.com</p>
 */

public class FileInfo implements IListData{
    public String name;

    public String path;

    public int type;

    public long length;

    public long date;

    @Override
    public int getItemType() {
        return 0;
    }
}
