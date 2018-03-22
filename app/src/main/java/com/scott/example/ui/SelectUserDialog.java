package com.scott.example.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-12.</p>
 * <p>Email:     shijl5@lenovo.com</p>
 * <p>Describe:</p>
 */

public class SelectUserDialog implements DialogInterface.OnClickListener{
    private String[] mUsers;
    private AlertDialog mDialog;
    private int mSelectItem;
    private DialogInterface.OnClickListener mBtnListenner;

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case AlertDialog.BUTTON_NEGATIVE:
                dissmiss();
                break;
            case AlertDialog.BUTTON_POSITIVE:
                if(mBtnListenner != null) {
                    mBtnListenner.onClick(dialog,which);
                }
                break;
            default:
                mSelectItem = which;
        }
    }


    public void show() {
        if(mDialog != null) {
            mDialog.show();
        }
    }

    public int getCurrentIndex() {
        return mSelectItem;
    }

    public String getCurrentName() {
        return mUsers[mSelectItem];
    }

    public void dissmiss() {
        if(mDialog != null) {
            mDialog.dismiss();
        }
    }

   public SelectUserDialog(Context context, String[] users,
                           DialogInterface.OnClickListener l,int index) {
       mUsers = users;
       mBtnListenner = l;
       mDialog = new AlertDialog.Builder(context)
               .setSingleChoiceItems(mUsers, index,this)
               .setTitle("请选择用户")
               .setPositiveButton("确认",this)
               .setNegativeButton("取消",this)
               .create();
   }
}
