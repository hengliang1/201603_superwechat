package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CategoryChildActivity;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by sks on 2016/6/27.
 */
public class CategoryAdapter extends BaseExpandableListAdapter {

    Context mContext;
    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;

    public CategoryAdapter(ArrayList<ArrayList<CategoryChildBean>> mChildList,
                           Context mContext, ArrayList<CategoryGroupBean> mGroupList) {
        this.mChildList = mChildList;
        this.mContext = mContext;
        this.mGroupList = mGroupList;
    }

    @Override
    public int getGroupCount() {
        return mGroupList == null ? 0 : mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList == null || mChildList.get(groupPosition) == null ? 0 : mChildList.get(groupPosition).size();
    }

    @Override
    public CategoryGroupBean getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        return mChildList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View layout, ViewGroup parent) {
        ViewGroupHolder holder = null;
        if (layout == null) {
            layout = View.inflate(mContext, R.layout.item_category, null);
            holder = new ViewGroupHolder();
            holder.ivGroupThumb = (NetworkImageView) layout.findViewById(R.id.niv_category);
            holder.tv_category_name = (TextView) layout.findViewById(R.id.tv_category_name);
            holder.iv_expand = (ImageView) layout.findViewById(R.id.iv_expand);
            layout.setTag(holder);
        } else {
            holder = (ViewGroupHolder) layout.getTag();
        }
        CategoryGroupBean group = getGroup(groupPosition);
        holder.tv_category_name.setText(group.getName());
        String imgUrl = group.getImageUrl();
        String url = I.DOWNLOAD_DOWNLOAD_CATEGORY_GROUP_IMAGE_URL + imgUrl;
        ImageUtils.setThumb(url, holder.ivGroupThumb);
        if (isExpanded) {
            holder.iv_expand.setImageResource(R.drawable.expand_off);
        } else {
            holder.iv_expand.setImageResource(R.drawable.expand_on);
        }
        return layout;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View layout, ViewGroup parent) {
        ViewChildHolder holder = null;
        if (layout == null) {
            layout = View.inflate(mContext, R.layout.item_category_child, null);
            holder = new ViewChildHolder();
            holder.layoutChild = (RelativeLayout) layout.findViewById(R.id.layout_category_child);
            holder.ivChildThumb = (NetworkImageView) layout.findViewById(R.id.niv_category_child);
            holder.tvChild = (TextView) layout.findViewById(R.id.tv_category_child);
            layout.setTag(holder);
        } else {
            holder = (ViewChildHolder) layout.getTag();
        }
        final CategoryChildBean child = getChild(groupPosition, childPosition);
        String name = child.getName();

        holder.tvChild.setText(name);
        String imgUrl = child.getImageUrl();
        String url = I.DOWNLOAD_DOWNLOAD_CATEGORY_CHILD_IMAGE_URL + imgUrl;
        ImageUtils.setThumb(url, holder.ivChildThumb);

        holder.layoutChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, CategoryChildActivity.class)
                        .putExtra(I.CategoryChild.CAT_ID, child.getId())
                        .putExtra(I.CategoryGroup.NAME, mGroupList.get(groupPosition).getName())
                        .putExtra("childList",(ArrayList<CategoryChildBean>)mChildList.get(groupPosition)));

            }
        });
        return layout;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ViewGroupHolder {
        NetworkImageView ivGroupThumb;
        TextView tv_category_name;
        ImageView iv_expand;
    }

    class ViewChildHolder {
        RelativeLayout layoutChild;
        NetworkImageView ivChildThumb;
        TextView tvChild;
    }

    public void addItems(ArrayList<CategoryGroupBean> groupList,
                         ArrayList<ArrayList<CategoryChildBean>> childList) {
        this.mGroupList.addAll(groupList);
        this.mChildList.addAll(childList);
        notifyDataSetChanged();
    }
}
