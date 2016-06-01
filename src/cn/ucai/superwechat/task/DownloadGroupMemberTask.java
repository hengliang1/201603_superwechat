package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.activity.BaseActivity;
import cn.ucai.superwechat.bean.Member;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by sks on 2016/5/31.
 */
public class DownloadGroupMemberTask extends BaseActivity {
    private static final String TAG = DownloadAllGroupTask.class.getName();
    Context mContext;
    String hxId;
    String path;

    public DownloadGroupMemberTask(Context mContext, String hxId) {
        this.mContext = mContext;
        this.hxId = hxId;
        initPath();
    }

    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.Member.GROUP_HX_ID, hxId)
                    .getRequestUrl(I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        executeRequest(new GsonRequest<Member[]>(path, Member[].class,
                responseDownloadGroupMemberTaskListener(), errorListener()));
    }

    private Response.Listener<Member[]> responseDownloadGroupMemberTaskListener() {
        return new Response.Listener<Member[]>() {
            @Override
            public void onResponse(Member[] Member) {
                Log.e(TAG, "DownloadGroupMember");
                if (Member != null) {
                    Log.e(TAG, "DownloadGroupMember,members.size=" + Member.length);
                    ArrayList<Member> list = Utils.array2List(Member);
                    HashMap<String, ArrayList<cn.ucai.superwechat.bean.Member>> groupMembers =
                            SuperWeChatApplication.getInstance().getGroupMembers();
                    ArrayList<Member> memberArrayList = groupMembers.get(hxId);
                    memberArrayList.clear();
                    memberArrayList.addAll(list);
                    mContext.sendStickyBroadcast(new Intent("update_member_list"));

                }
            }
        };
    }
}