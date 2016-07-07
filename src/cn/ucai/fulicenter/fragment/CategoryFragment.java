package cn.ucai.fulicenter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuliMainActivit;
import cn.ucai.fulicenter.adapter.CategoryAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/6/27.
 */
public class CategoryFragment extends Fragment {
    public static final String TAG = CategoryFragment.class.getName();

    ExpandableListView elCollect;
    FuliMainActivit mContext;
    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;

    CategoryAdapter mAdapter;
    /**
     * 小类匹配大类的辅助
     */
    int groupCount;//记录一共有几个大类

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (FuliMainActivit) getActivity();
        View layout = inflater.inflate(R.layout.fragment_category, container, false);
        initView(layout);
        initData();
        return layout;
    }

    private void initView(View layout) {
        elCollect = (ExpandableListView) layout.findViewById(R.id.el_collect);
        elCollect.setGroupIndicator(null);
        mGroupList = new ArrayList<CategoryGroupBean>();
        mChildList = new ArrayList<ArrayList<CategoryChildBean>>();
        mAdapter = new CategoryAdapter(mChildList, mContext, mGroupList);
        elCollect.setAdapter(mAdapter);
    }

    private void initData() {
        mGroupList = new ArrayList<CategoryGroupBean>();
        mChildList = new ArrayList<ArrayList<CategoryChildBean>>();
        try {
            String path = new ApiParams()
                    .getRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP);
            Log.e(TAG, "path=" + path);
            mContext.executeRequest(new GsonRequest<CategoryGroupBean[]>(path, CategoryGroupBean[].class
                    , responseDownloadListener(), mContext.errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<CategoryGroupBean[]> responseDownloadListener() {
        return new Response.Listener<CategoryGroupBean[]>() {
            @Override
            public void onResponse(CategoryGroupBean[] categoryGroupBeen) {
                if (categoryGroupBeen != null) {
                    try {
                        mGroupList = Utils.array2List(categoryGroupBeen);
                        int i = 0;
                        for (CategoryGroupBean group : mGroupList) {
                            mChildList.add(i, new ArrayList<CategoryChildBean>());
                            String path = new ApiParams()
                                    .with(I.CategoryChild.PARENT_ID, group.getId() + "")
                                    .with(I.PAGE_ID, "0")
                                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                                    .getRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN);
                            Log.e(TAG, "ChildPath=" + path);
                            mContext.executeRequest(new GsonRequest<CategoryChildBean[]>(path, CategoryChildBean[].class
                                    , responseDownChildListener(i), mContext.errorListener()));
                            i++;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private Response.Listener<CategoryChildBean[]> responseDownChildListener(final int i) {
        return new Response.Listener<CategoryChildBean[]>() {
            @Override
            public void onResponse(CategoryChildBean[] categoryChildBeen) {
                groupCount++;
                if (categoryChildBeen != null) {
                    ArrayList<CategoryChildBean> childList = Utils.array2List(categoryChildBeen);
                    if (childList != null) {
                        mChildList.set(i, childList);
                    }
                }
                if (mGroupList.size() == groupCount) {
                    mAdapter.addItems(mGroupList,mChildList);
                }
            }
        };
    }
}