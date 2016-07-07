package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.CartFragment;
import cn.ucai.fulicenter.fragment.CategoryFragment;
import cn.ucai.fulicenter.fragment.NewGoodFragment;
import cn.ucai.fulicenter.fragment.PersonalCenterFragment;
import cn.ucai.fulicenter.utils.Utils;

public class FuliMainActivit extends BaseActivity {
    public static final String TAG = "fuliCenterMainActivity";

    RadioButton mlayout_new_good, mlayout_boutique, mlayout_category, mlayout_cart, mlayout_personal;
    TextView mtvCartHint;

    RadioButton[] mRadios = new RadioButton[5];
    NewGoodFragment mNewGoodFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;
    PersonalCenterFragment mPersonalCenterFragment;
    CartFragment mCartFragment;

    Fragment[] mFragments = new Fragment[5];
    private int index;
    // 当前fragment的index
    private int currentTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuli_main);
        initView();
        initFragment();

        // 添加显示第一个fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(cn.ucai.fulicenter.R.id.fragment_container, mNewGoodFragment)
                .add(cn.ucai.fulicenter.R.id.fragment_container, mBoutiqueFragment).hide(mBoutiqueFragment)
                .add(R.id.fragment_container, mCategoryFragment).hide(mCategoryFragment)
                .show(mNewGoodFragment)
                .commit();

        registerCartListener();
    }

    private void initFragment() {
        mNewGoodFragment = new NewGoodFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment = new CategoryFragment();
        mCartFragment = new CartFragment();
        mPersonalCenterFragment = new PersonalCenterFragment();
        mFragments[0] = mNewGoodFragment;
        mFragments[1] = mBoutiqueFragment;
        mFragments[2] = mCategoryFragment;
        mFragments[3] = mCartFragment;
        mFragments[4] = mPersonalCenterFragment;

    }

    private void initView() {
        mlayout_new_good = (RadioButton) findViewById(R.id.layout_new_good);
        mlayout_boutique = (RadioButton) findViewById(R.id.layout_boutique);
        mlayout_category = (RadioButton) findViewById(R.id.layout_category);
        mlayout_cart = (RadioButton) findViewById(R.id.layout_cart);
        mlayout_personal = (RadioButton) findViewById(R.id.layout_personal);
        mtvCartHint = (TextView) findViewById(R.id.tvCartHint);

        mRadios[0] = mlayout_new_good;
        mRadios[1] = mlayout_boutique;
        mRadios[2] = mlayout_category;
        mRadios[3] = mlayout_cart;
        mRadios[4] = mlayout_personal;
        mtvCartHint.setVisibility(View.GONE);
    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case cn.ucai.fulicenter.R.id.layout_new_good:
                index = 0;
                break;
            case cn.ucai.fulicenter.R.id.layout_boutique:
                index = 1;
                break;
            case cn.ucai.fulicenter.R.id.layout_category:
                index = 2;
                break;
            case cn.ucai.fulicenter.R.id.layout_cart:
                if (FuLiCenterApplication.getInstance().getUser() != null) {
                    index = 3;
                } else {
                    gotoLogin(I.ACTION_TYPE_CART);
                }
                break;
            case cn.ucai.fulicenter.R.id.layout_personal:
                if (FuLiCenterApplication.getInstance().getUser() != null) {
                    index = 4;
                } else {
                    gotoLogin(I.ACTION_TYPE_PERSONAL);
                }
                break;
        }
        if (currentTabIndex != index) {
            Log.e(TAG, "index="+index );
            Log.e(TAG, "currentTabIndex="+currentTabIndex );
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentTabIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(cn.ucai.fulicenter.R.id.fragment_container, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
            setRadiosCheck(index);
            currentTabIndex = index;
        }
    }

    private void gotoLogin(String action) {
        startActivity(new Intent(this, LoginActivity.class).putExtra("action", action));

    }

    private void setRadiosCheck(int index) {
        for (int i = 0; i < mRadios.length; i++) {
            if (i == index) {
                mRadios[i].setChecked(true);
            } else {
                mRadios[i].setChecked(false);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String action = getIntent().getStringExtra("action");
        Log.e(TAG, "onNewIntent action=" + action);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "currentTabIndex =" + currentTabIndex + ",index=" + index);
        Log.e(TAG, "user=" + FuLiCenterApplication.getInstance().getUser());
        String action = getIntent().getStringExtra("action");
        if (action != null && FuLiCenterApplication.getInstance().getUser() != null) {
            if (action.equals("personal")) {
                index = 4;
            }
            if (action.equals(I.ACTION_TYPE_CART)) {
                index = 3;
            }
        } else {
            setRadiosCheck(index);
        }
        if (currentTabIndex == 4 && FuLiCenterApplication.getInstance().getUser() == null) {
            index = 0;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentTabIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(R.id.fragment_container, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
            setRadiosCheck(index);
            currentTabIndex = index;
        }
    }

    class UpdateCartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int count = Utils.sumCartCount();
            Log.e(TAG, "count=" + count);
            if (count > 0) {
                mtvCartHint.setVisibility(View.VISIBLE);
                mtvCartHint.setText("" + count);
            } else {
                mtvCartHint.setVisibility(View.GONE);
            }
            if (FuLiCenterApplication.getInstance().getUser() == null) {
                mtvCartHint.setText("0");
                mtvCartHint.setVisibility(View.GONE);
            }
        }
    }
    UpdateCartReceiver mReceiver;
    private void registerCartListener() {
        mReceiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
        filter.addAction("update_user");
        filter.addAction("update_cart");
        registerReceiver(mReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
