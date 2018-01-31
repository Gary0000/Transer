package com.scott.example.adapter;
import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-01-22 13:11</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class SelectAdapter extends BaseQuckListAdapter<IListData,BaseListHolder>{

    public SelectAdapter(List<IListData> datas) {
        super(datas);
    }

    @Override
    public void convert(BaseListHolder holder, int position, IListData item) {

    }
}
