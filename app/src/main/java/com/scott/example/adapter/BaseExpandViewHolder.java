package com.scott.example.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-26 15:36</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class BaseExpandViewHolder extends BaseViewHolder{

    private int mViewType = -1;

    private int mGroupPostion = 0;

    private int mChildPostion = 0;

    public void setItemType(int viewType) {
        mViewType = viewType;
    }

    public int getItemType() {
        return mViewType;
    }

    public BaseExpandViewHolder(View view) {
        super(view);
    }

    public int getChildPostion() {
        return mChildPostion;
    }

    public int getGroupPostion() {
        return mGroupPostion;
    }

    public void setChildPostion(int postion) {
        mChildPostion = postion;
    }

    public void setGroupPostion(int postion) {
        mGroupPostion = postion;
    }
}
