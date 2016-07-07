package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.Contact;
import cn.ucai.fulicenter.bean.Message;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/5/23.
 */
public class DownloadCollectCountTask extends BaseActivity {
    private static final String TAG = DownloadCollectCountTask.class.getName();
    Context mContext;
    String path;

    public DownloadCollectCountTask(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        initPath();
    }

    private void initPath() {
        try {
            User user = FuLiCenterApplication.getInstance().getUser();
            path = new ApiParams()
                    .with(I.Collect.USER_NAME,user.getMUserName())
                    .getRequestUrl(I.REQUEST_FIND_COLLECT_COUNT);
            Log.e(TAG, "ssssssssssssssssssssssssssssssssssssssssssssss=="+path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        if (path == null || path.isEmpty()) {
            return;
        }
        executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class,
                responseDownloadCollectCountTaskListener(), errorListener()));

    }

    private Response.Listener<MessageBean> responseDownloadCollectCountTaskListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean.isSuccess()) {
                    String count = messageBean.getMsg();
                    Log.e(TAG, "responseDownloadCollectCountTaskListener,count=" + count);
                    FuLiCenterApplication.getInstance().setCollectCount(Integer.parseInt(count));
                } else {
                    Log.e(TAG, "responseDownloadCollectCountTaskListener,count=0");
                    FuLiCenterApplication.getInstance().setCollectCount(0);
                }
                Intent intent = new Intent("update_collet_count");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}
