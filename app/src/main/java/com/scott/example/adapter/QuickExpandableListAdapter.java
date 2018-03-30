package com.scott.example.adapter;

import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.IExpandable;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017/7/7.</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *    参考BRCV 实现的 ExpandableListView 的 Adapter
 *
 *    1. D 为 Group Item 类型
 *    2. D 必须实现 IExpandable
 *    3. convert(BaseExpandViewHolder helper,MultiItemEntity item)
 *    中的Item 在对应Group 时 返回 getGroup
 *    对应Child 时 返回 getChild
 * </p>
 */

public abstract class QuickExpandableListAdapter<D extends MultiItemEntity,
        VH extends BaseExpandViewHolder> extends BaseExpandableListAdapter {

    private List<D> mDatas;
    protected SparseArray<Integer> layouts;
    protected LayoutInflater mInflater;

    public QuickExpandableListAdapter(List<D> datas) {
        mDatas = datas;
    }


    public void addItemView(int type,@LayoutRes int layout) {
        if(layouts == null) {
            layouts = new SparseArray<>();
        }
        layouts.put(type,layout);
    }

    @Override
    public int getGroupCount() {
        return mDatas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(!(mDatas.get(groupPosition) instanceof IExpandable)) {
            return 0;
        }
        IExpandable expandable = ((IExpandable) mDatas.get(groupPosition));
        return expandable.getSubItems() == null ?
                0 : expandable.getSubItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mDatas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(!(mDatas.get(groupPosition) instanceof IExpandable)) {
            return null;
        }
        IExpandable expandable = ((IExpandable) mDatas.get(groupPosition));
        return expandable.getSubItems() == null ? null :
                expandable.getSubItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        D group = mDatas.get(groupPosition);
        return createConvertView(groupPosition,0,group,convertView,parent,false);
    }

    private View createConvertView(int groupPostion,int childPostion
            ,MultiItemEntity item, View convertView, ViewGroup parent,boolean isChild) {
        VH holder = null;
        if(convertView != null) {
            holder = (VH) convertView.getTag();
            holder.setGroupPostion(groupPostion);
            holder.setChildPostion(childPostion);
            if(holder.getItemType() != item.getItemType()) {
                holder = null;
            }
        }

        if(holder == null) {
            int layoutId = layouts.get(item.getItemType());
            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }
            convertView = mInflater.inflate(layoutId,parent,false);
            holder = createBaseViewHolder(convertView);
            holder.setGroupPostion(groupPostion);
            holder.setChildPostion(childPostion);
            holder.setItemType(item.getItemType());
            convertView.setTag(holder);
        }
        //holder.setItemType(item.getItemType());
        if(isChild) {
            item = (MultiItemEntity) getChild(groupPostion,childPostion);
        } else {
            item = (MultiItemEntity) getGroup(groupPostion);
        }
        convert(holder,item);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        MultiItemEntity item  = (MultiItemEntity) ((IExpandable)mDatas.get(groupPosition)).getSubItems().get(childPosition);
        return createConvertView(groupPosition,childPosition,item,convertView,parent,true);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    protected abstract void convert(final VH holder,final MultiItemEntity item);

    /**
     * if you want to use subclass of BaseViewHolder in the adapter,
     * you must override the method to create new ViewHolder.
     *
     * @param view view
     * @return new ViewHolder
     */
    protected VH createBaseViewHolder(View view) {
        Class temp = getClass();
        Class z = null;
        while (z == null && null != temp) {
            z = getInstancedGenericKClass(temp);
            temp = temp.getSuperclass();
        }
        VH k = createGenericKInstance(z, view);
        return null != k ? k : (VH) new BaseViewHolder(view);
    }

    /**
     * try to create Generic K instance
     *
     * @param z
     * @param view
     * @return
     */
    private VH createGenericKInstance(Class z, View view) {
        try {
            Constructor constructor;
            String buffer = Modifier.toString(z.getModifiers());
            String className = z.getName();
            // inner and unstatic class
            if (className.contains("$") && !buffer.contains("static")) {
                constructor = z.getDeclaredConstructor(getClass(), View.class);
                return (VH) constructor.newInstance(this, view);
            } else {
                constructor = z.getDeclaredConstructor(View.class);
                return (VH) constructor.newInstance(view);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get generic parameter K
     *
     * @param z
     * @return
     */
    private Class getInstancedGenericKClass(Class z) {
        Type type = z.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            for (Type temp : types) {
                if (temp instanceof Class) {
                    Class tempClass = (Class) temp;
                    if (BaseViewHolder.class.isAssignableFrom(tempClass)) {
                        return tempClass;
                    }
                }
            }
        }
        return null;
    }

}
