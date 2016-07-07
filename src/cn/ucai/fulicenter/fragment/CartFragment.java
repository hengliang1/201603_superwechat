package cn.ucai.fulicenter.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuliMainActivit;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.task.DownloadCartListTask;
import cn.ucai.fulicenter.utils.Utils;


/**
 * Created by Administrator on 2016/6/20 0020.
 */
public class CartFragment extends Fragment {
    public static final String TAG = CartFragment.class.getName();

    FuliMainActivit mContext;
    ArrayList<CartBean> mCartList;
    CartAdapter mAdapter;
    private int action = I.ACTION_DOWNLOAD;
    String path;

    int pageId = 0;

    /**
     * 下拉刷新控件
     */

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    TextView mtvHint;
    LinearLayoutManager mLinearLayoutManager;

    TextView mtvNothing;
    TextView mtvRankPrice;
    TextView mtvSavePrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_cart, container, false);
        mContext = (FuliMainActivit) getActivity();
        initView(layout);
        initData();
        setListener();

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_cart);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );

        mtvHint = (TextView) layout.findViewById(R.id.tvRefreshHint);
        mtvNothing = (TextView) layout.findViewById(R.id.tvNothing);
        mtvNothing.setVisibility(View.GONE);
        mtvRankPrice = (TextView) layout.findViewById(R.id.tv_sum_price);
        mtvSavePrice = (TextView) layout.findViewById(R.id.tv_save_price);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_cart);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mCartList = new ArrayList<CartBean>();
        mAdapter = new CartAdapter(mContext, mCartList);
        mRecyclerView.setAdapter(mAdapter);

    }

    private Response.Listener<CartBean[]> responseDownloadCartListener() {
        return new Response.Listener<CartBean[]>() {
            @Override
            public void onResponse(CartBean[] boutiqueBeen) {
                if (boutiqueBeen != null) {
                    mAdapter.setMore(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);

                    ArrayList<CartBean> list = Utils.array2List(boutiqueBeen);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initItems(list);
                    } else if (action == I.ACTION_PULL_UP) {
                        mAdapter.addItem(list);
                    }
                    if (boutiqueBeen.length < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                    }
                }
            }
        };
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
        registerUpdateCartListener();
    }

    /**
     * 下拉事件监听
     */
    private void setPullUpRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mtvHint.setVisibility(View.VISIBLE);
                action = I.ACTION_PULL_DOWN;
                getpath();
                mContext.executeRequest(new GsonRequest<CartBean[]>(path,
                        CartBean[].class, responseDownloadCartListener(), mContext.errorListener()));
            }
        });
    }

    /**
     * 上拉事件监听
     */
    private void setPullDownRefreshListener() {
        mRecyclerView.setOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    int lastItemPostion;

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPostion == mAdapter.getItemCount() - 1) {
                            if (mAdapter.isMore()) {
                                mSwipeRefreshLayout.setRefreshing(true);
                                action = I.ACTION_PULL_UP;
                                pageId = I.PAGE_SIZE_DEFAULT;
                                getpath();
                                mContext.executeRequest(new GsonRequest<CartBean[]>(path,
                                        CartBean[].class, responseDownloadCartListener(), mContext.errorListener()));
                            }
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        //获取最后列表的下标
                        lastItemPostion = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                        mSwipeRefreshLayout.setEnabled(mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
                    }
                });
    }


    private void initData() {
        ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        Log.e(TAG, "initData-cartList" + cartList);
        mCartList.clear();
        mCartList.addAll(cartList);
        mAdapter.notifyDataSetChanged();

        sumPrice();
        if (mCartList == null || mCartList.size() == 0) {
            mtvNothing.setVisibility(View.VISIBLE);
        } else {
            mtvNothing.setVisibility(View.GONE);
        }
    }

    private String getpath() {
        try {
            path = new ApiParams()
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .with(I.User.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                    .getRequestUrl(I.REQUEST_FIND_CARTS);
            Log.e(TAG, "path=" + path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sumPrice() {
        int sumPrice = 0;
        int currentPrice = 0;
        if (mCartList != null && mCartList.size() > 0) {
            for (CartBean cart : mCartList) {
                GoodDetailsBean goods = cart.getGoods();
                if (goods != null && cart.isChecked()) {
                    sumPrice += converPrice(goods.getCurrencyPrice()) * cart.getCount();
                    currentPrice += converPrice(goods.getRankPrice()) * cart.getCount();

                }
            }
        }
        int savePrice = sumPrice - currentPrice;
        mtvRankPrice.setText("合计:￥"+sumPrice);
        mtvSavePrice.setText("节省:￥"+savePrice);

    }

    private int converPrice(String price) {
        price = price.substring(price.indexOf("￥")+1);
        int p1 = Integer.parseInt(price);
        return p1;
    }

    class UpdateCartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }
    UpdateCartReceiver mReceiver;

    private void registerUpdateCartListener() {
        mReceiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter("update_cart");
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }
}
