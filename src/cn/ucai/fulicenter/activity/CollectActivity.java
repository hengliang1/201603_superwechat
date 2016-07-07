package cn.ucai.fulicenter.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectActivity extends BaseActivity {
    public static final String TAG = CollectActivity.class.getName();
    CollectActivity mContext;
    ArrayList<CollectBean> mCollectList;
    CollectAdapter mAdapter;
    private int pageId = 0;
    private int action = I.ACTION_DOWNLOAD;
    String path;

    /**
     * 下拉刷新空间
     */
    SwipeRefreshLayout msrl;
    RecyclerView mRecyclerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;
    public CollectActivity() {
        // Required empty public constructor
    }
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_collect_details);
        mContext = this;
        mCollectList = new ArrayList<CollectBean>();
        initView();
        initData();
        registerUpdaterCollectListener();
    }



    private void initData() {
        getPath(pageId);
        mContext.executeRequest(new GsonRequest<CollectBean[]>(path, CollectBean[].class,
                responseDownloadCollectListener(), mContext.errorListener()));
    }

    private Response.Listener<CollectBean[]> responseDownloadCollectListener() {
        return new Response.Listener<CollectBean[]>() {
            @Override
            public void onResponse(CollectBean[] collect) {
                if (collect != null) {
                    mAdapter.setMore(true);
                    msrl.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mAdapter.setFooterText(getResources().getString(R.string.load_more));
                    //将数组转换为集合
                    ArrayList<CollectBean> list = Utils.array2List(collect);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initItems(list);
                    } else if (action==I.ACTION_PULL_UP) {
                        mAdapter.addItem(list);
                    }
                    if (collect.length < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                        mAdapter.setFooterText(getResources().getString(R.string.no_more));
                    }
                }
            }
        };
    }


    private String getPath(int pageId) {
        try {
            String userName = FuLiCenterApplication.getInstance().getUserName();
            path = new ApiParams()
                    .with(I.Collect.USER_NAME,userName)
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .getRequestUrl(I.REQUEST_FIND_COLLECTS);
            Log.e("main", "BoutiqueDetailsActivity.path  :" + path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
    }

    /**
     * 上拉刷新
     */
    private void setPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastItemPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        lastItemPosition == mAdapter.getItemCount() - 1) {
                    if (mAdapter.isMore()) {
                        msrl.setRefreshing(true);
                        action = I.ACTION_PULL_UP;
                        pageId+=I.PAGE_SIZE_DEFAULT;
                        getPath(pageId);
                        mContext.executeRequest(new GsonRequest<CollectBean[]>(path,
                                CollectBean[].class, responseDownloadCollectListener(),
                                mContext.errorListener()));
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastItemPosition = mGridLayoutManager.findLastVisibleItemPosition();
                msrl.setEnabled(mGridLayoutManager.findFirstCompletelyVisibleItemPosition()==0);
            }
        });
    }
    /**
     * 下拉刷新
     */
    private void setPullDownRefreshListener() {
        msrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mtvHint.setVisibility(View.VISIBLE);
                pageId = 0;
                action = I.ACTION_PULL_DOWN;
                getPath(pageId);
                mContext.executeRequest(new GsonRequest<CollectBean[]>(path, CollectBean[].class,
                        responseDownloadCollectListener(), mContext.errorListener()));
            }
        });
    }

    private void initView( ) {
        msrl = (SwipeRefreshLayout)findViewById(R.id.srl_collect);
        msrl.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mtvHint = (TextView) findViewById(R.id.tvRefreshHint);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_collect);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mAdapter = new CollectAdapter(mContext, mCollectList);
        mRecyclerView.setAdapter(mAdapter);
        DisplayUtils.initBackWithTitle(mContext, "收藏的宝贝");
    }

    class UpdateCollectListenerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }
    UpdateCollectListenerReceiver mReceiver;
    private void registerUpdaterCollectListener() {
        mReceiver = new UpdateCollectListenerReceiver();
        IntentFilter filter = new IntentFilter("update_collet_count");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
