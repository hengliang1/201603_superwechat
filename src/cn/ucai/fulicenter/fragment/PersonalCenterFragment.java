package cn.ucai.fulicenter.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CollectActivity;
import cn.ucai.fulicenter.activity.SettingsActivity;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.UserUtils;

/**
 * Created by sks on 2016/6/28.
 */
public class PersonalCenterFragment extends Fragment {
    public static final String TAG = PersonalCenterFragment.class.getName();
    Context mContext;
    //资源文件
    private int[] pic_path = {R.drawable.order_list1,
            R.drawable.order_list2,
            R.drawable.order_list3,
            R.drawable.order_list4,
            R.drawable.order_list5};
    NetworkImageView mivUserAvater;
    TextView mtvUserName;
    TextView mtvCollectCount;
    TextView mtvSettings;
    ImageView mivMessage;
    LinearLayout mLayoutCenterCollet;
    RelativeLayout mLayoutCenterUserInfo;

    int mColletCount=0;
    User user;
    MyClickListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View layout = View.inflate(mContext, R.layout.personal_center,null);
        initView(layout);
        initData();
        setListener();
        return layout;
    }

    private void setListener() {
        registerColletCountChangeListener();
        registerUpdateReceiver();
        listener = new MyClickListener();
        Log.e(TAG,"设置界面");
        mtvSettings.setOnClickListener(listener);
        mLayoutCenterUserInfo.setOnClickListener(listener);
        mLayoutCenterCollet.setOnClickListener(listener);
    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.e(TAG, "~~~~~~~~~~~~~~~~~~~");
            switch (v.getId()) {
                case R.id.tv_personal_center_settings:
                case R.id.center_user_info:
                    startActivity(new Intent(mContext, SettingsActivity.class));
                    break;
                case R.id.layout_center_collet:
                    startActivity(new Intent(mContext, CollectActivity.class));
                    break;
            }
        }
    }

    private void initData() {
        mColletCount = FuLiCenterApplication.getInstance().getCollectCount();
        mtvCollectCount.setText("" + mColletCount);
        if (FuLiCenterApplication.getInstance().getUser() != null) {
            UserUtils.setCurrentUserAvatar(mivUserAvater);
            UserUtils.setCurrentUserBeanNick(mtvUserName);

        }
    }

    private void initView(View layout) {
        mivUserAvater = (NetworkImageView) layout.findViewById(R.id.iv_user_avatar);
        mtvUserName = (TextView) layout.findViewById(R.id.tv_user_name);
        mLayoutCenterCollet = (LinearLayout) layout.findViewById(R.id.layout_center_collet);
        mtvCollectCount = (TextView) layout.findViewById(R.id.tvCollectCount);
        mtvSettings = (TextView) layout.findViewById(R.id.tv_personal_center_settings);
        mivMessage = (ImageView) layout.findViewById(R.id.iv_personal_center_msg);
        mLayoutCenterUserInfo = (RelativeLayout) layout.findViewById(R.id.center_user_info);

        initOrderList(layout);
    }

    private void initOrderList(View layout) {
        //显示GridView的界面
        GridView mOrderList = (GridView) layout.findViewById(R.id.center_user_order_list);
        ArrayList<HashMap<String, Object>> imageList = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> mp1 = new HashMap<String, Object>();
        mp1.put("image", R.drawable.order_list1);
        imageList.add(mp1);
        HashMap<String, Object> mp2 = new HashMap<String, Object>();
        mp2.put("image", R.drawable.order_list2);
        imageList.add(mp2);
        HashMap<String, Object> mp3 = new HashMap<String, Object>();
        mp3.put("image", R.drawable.order_list3);
        imageList.add(mp3);
        HashMap<String, Object> mp4 = new HashMap<String, Object>();
        mp4.put("image", R.drawable.order_list4);
        imageList.add(mp4);
        HashMap<String, Object> mp5 = new HashMap<String, Object>();
        mp5.put("image", R.drawable.order_list5);
        imageList.add(mp5);

        SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, imageList,
                R.layout.simple_grid_item, new String[]{"image"}, new int[]{R.id.image});
        mOrderList.setAdapter(simpleAdapter);
    }

    class ColletCountChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }

    ColletCountChangeReceiver mReceiver;
    private void registerColletCountChangeListener() {
        mReceiver = new ColletCountChangeReceiver();
        IntentFilter filter = new IntentFilter("update_collet_count");
        mContext.registerReceiver(mReceiver,filter);
    }

    class UpdateUserChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new DownloadCollectCountTask(mContext);
            initData();
        }
    }
    UpdateUserChangedReceiver mUserReceiver;
    private void registerUpdateReceiver() {
        mUserReceiver = new UpdateUserChangedReceiver();
        IntentFilter filter = new IntentFilter("update_user");
        mContext.registerReceiver(mUserReceiver, filter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
        if (mUserReceiver != null) {
            mContext.unregisterReceiver(mUserReceiver);
        }
    }
}
