package cn.ucai.fulicenter.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by sks on 2016/6/24.
 */
public class GoodDetailsActivity extends BaseActivity {
    public static final String TAG = GoodDetailsActivity.class.getName();
    GoodDetailsActivity mcontext;
    GoodDetailsBean mGood;
    int mGoodsId;

    RelativeLayout mrlGoodDetail;
    ImageView miv_share;
    ImageView miv_collect;
    ImageView mivCart;
    TextView mtv_cart_count;

    RelativeLayout mrl_good_details;
    TextView tvGoodName,tvGoodEngisName,tvShopPrice,tvCurrencyPrice;
    WebView wvGoodBrief;

    RelativeLayout mlayout_banner;
    SlideAutoLoopView msalv;
    FlowIndicator mindicator;

    LinearLayout mlayoutColor,mlayoutColorSelector;

    //当前的颜色值
    int mCurrentColor;

    boolean isCollect;
    int actionCollect;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_good_details);
        mcontext = this;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setCollectClickListener();
        setCartClickListener();
        ReceiverUpdateCartListener();
        miv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
    }

    private void setCartClickListener() {
        Log.e(TAG, "添加购物车点击事件");
        mivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addCart(mcontext, mGood);

            }
        });
    }

    private void setCollectClickListener() {
        miv_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = FuLiCenterApplication.getInstance().getUser();
                if (user == null) {
                    startActivity(new Intent(GoodDetailsActivity.this, LoginActivity.class));
                } else {
                    try {
                        String path;
                        if (isCollect) {
                            path = new ApiParams()
                                    .with(I.Collect.USER_NAME, user.getMUserName())
                                    .with(I.Collect.GOODS_ID, mGoodsId + "")
                                    .getRequestUrl(I.REQUEST_DELETE_COLLECT);
                            actionCollect = I.ACTION_DEL_COLLECT;
                        } else {
                            path = new ApiParams()
                                    .with(I.Collect.USER_NAME, user.getMUserName())
                                    .with(I.Collect.GOODS_ID, mGoodsId + "")
                                    .with(I.Collect.GOODS_NAME, mGood.getGoodsName())
                                    .with(I.Collect.GOODS_ENGLISH_NAME, mGood.getGoodsEnglishName())
                                    .with(I.Collect.GOODS_THUMB, mGood.getGoodsThumb())
                                    .with(I.Collect.GOODS_IMG, mGood.getGoodsImg())
                                    .with(I.Collect.ADD_TIME, mGood.getAddTime() + "")
                                    .getRequestUrl(I.REQUEST_ADD_COLLECT);
                            actionCollect = I.ACTION_ADD_COLLECT;
                        }
                        executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class,
                                responseSetCollectListener(), errorListener()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private Response.Listener<MessageBean> responseSetCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean message) {
                if (message.isSuccess()) {
                    if (actionCollect == I.ACTION_ADD_COLLECT) {
                        isCollect = true;
                        miv_collect.setImageResource(R.drawable.bg_collect_out);
                    } else {
                        isCollect = false;
                        miv_collect.setImageResource(R.drawable.bg_collect_in);
                    }
                    new DownloadCollectCountTask(mcontext).execute();
                }
                Utils.showToast(mcontext, message.getMsg(), Toast.LENGTH_SHORT);
            }
        };
    }

    private void initView() {
        wvGoodBrief = (WebView) findViewById(R.id.wv_good_brief);
        mrlGoodDetail = (RelativeLayout) findViewById(R.id.rl_top);
        miv_share = (ImageView) findViewById(R.id.iv_share);
        miv_collect = (ImageView) findViewById(R.id.iv_collect);
        mivCart = (ImageView) findViewById(R.id.iv_cart);
        mtv_cart_count = (TextView) findViewById(R.id.tv_cart_count);
        mrl_good_details = (RelativeLayout) findViewById(R.id.rl_good_details);
        tvGoodName = (TextView) findViewById(R.id.tv_english_name);
        tvGoodEngisName = (TextView) findViewById(R.id.tv_chinese_name);
        tvShopPrice = (TextView) findViewById(R.id.tv_price);
        tvCurrencyPrice = (TextView) findViewById(R.id.tv_now_price);
        mlayout_banner = (RelativeLayout) findViewById(R.id.layout_banner);
        msalv = (SlideAutoLoopView) findViewById(R.id.salv);
        mindicator = (FlowIndicator) findViewById(R.id.indicator);
        mlayoutColor = (LinearLayout) findViewById(R.id.layoutColor);
        mlayoutColorSelector = (LinearLayout) findViewById(R.id.layoutColorSelector);

        wvGoodBrief = (WebView) findViewById(R.id.wv_good_brief);
        WebSettings settings = wvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCollectStatus();
        initCartStatus();
    }

    private void initCartStatus() {
        int count = Utils.sumCartCount();
        if (count > 0) {
            mtv_cart_count.setVisibility(View.VISIBLE);
            mtv_cart_count.setText("" + count);
        } else {
            mtv_cart_count.setVisibility(View.GONE);
            mtv_cart_count.setText("0");
        }
    }

    private void initCollectStatus() {
        User user = FuLiCenterApplication.getInstance().getUser();
        if (user != null) {
            try {
                String path = new ApiParams()
                        .with(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                        .with(I.Collect.GOODS_ID, mGoodsId + "")
                        .getRequestUrl(I.REQUEST_IS_COLLECT);
                executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class,
                        responseIsCollectListener(), errorListener()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            isCollect = false;
            miv_collect.setImageResource(R.drawable.bg_collect_in);
        }
    }

    private Response.Listener<MessageBean> responseIsCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean message) {
                if (message.isSuccess()) {
                    isCollect = true;
                    miv_collect.setImageResource(R.drawable.bg_collect_out);
                } else {
                    isCollect = false;
                    miv_collect.setImageResource(R.drawable.bg_collect_in);
                }
            }
        };
    }

    private void initData() {
        mGoodsId= getIntent().getIntExtra(D.NewGood.KEY_GOODS_ID, 0);
        try {
            String path = new ApiParams()
                    .with(D.NewGood.KEY_GOODS_ID, mGoodsId + "")
                    .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
            Log.e(TAG, "path" + path);
            executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class,
                    responseDownloadGoodDetailsListener(), errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Response.Listener<GoodDetailsBean> responseDownloadGoodDetailsListener() {
        return new Response.Listener<GoodDetailsBean>() {
            @Override
            public void onResponse(GoodDetailsBean goodDetailsBean) {
                if (goodDetailsBean != null) {
                    mGood = goodDetailsBean;
                    //设置商品名称，价格，webview简介
                    DisplayUtils.initBackWithTitle(GoodDetailsActivity.this, getResources().getString(R.string.title_good_details));
                    tvCurrencyPrice.setText(mGood.getCurrencyPrice());
                    tvGoodEngisName.setText(mGood.getGoodsEnglishName());
                    tvGoodName.setText(mGood.getGoodsName());
                    wvGoodBrief.loadDataWithBaseURL(null, mGood.getGoodsBrief().trim(), D.TEXT_HTML, D.UTF_8, null);

                    //初始化颜色面板
                    initColorBanner();
                } else {
                    Utils.showToast(mcontext, "商品详情下载失败", Toast.LENGTH_LONG);
                    finish();
                }
            }
        };
    }

    private void initColorBanner() {
        //设置第一个颜色的图片轮播
        updateColor(0);
        for (int i=0;i<mGood.getProperties().length;i++) {
            mCurrentColor = i;
            View layout = View.inflate(mcontext, R.layout.layout_property_color, null);
            final NetworkImageView ivColor = (NetworkImageView) layout.findViewById(R.id.ivColorItem);
            Log.i(TAG, "initColorBanner.goodDetails=" + mGood.getProperties()[i].toString());
            String colorImg = mGood.getProperties()[i].getColorImg();
            if (colorImg.isEmpty()) {
                continue;
            }
            ImageUtils.setGoodDetailThumb(colorImg,ivColor);
            mlayoutColor.addView(layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateColor(mCurrentColor);
                }
            });
        }
    }

    /**
     * 设置指定属性的图片轮播
     * @param i
     */
    private void updateColor(int i) {
        AlbumsBean[] albums = mGood.getProperties()[i].getAlbums();
        String[] albumImgUrl = new String[albums.length];
        for (int j=0;j<albumImgUrl.length;j++) {
            albumImgUrl[j] = albums[j].getImgUrl();
        }
        msalv.startPlayLoop(mindicator, albumImgUrl, albumImgUrl.length);
    }

    class UpdateCartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initCartStatus();
        }
    }

    UpdateCartReceiver mReceiver;

    private void ReceiverUpdateCartListener() {
        mReceiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter("update_cart");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }
}
