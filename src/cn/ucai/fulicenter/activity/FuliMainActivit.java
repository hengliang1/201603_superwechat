package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

public class FuliMainActivit extends Activity {
    RadioButton mlayout_new_good,mlayout_boutique,mlayout_category,mlayout_cart,mlayout_personal;
    TextView mtvCartHint;

    RadioButton[] mRadios = new RadioButton[5];
    private int index;
    // 当前fragment的index
    private int currentTabIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuli_main);
        initView();
    }

    private void initView() {
        mlayout_new_good = (RadioButton) findViewById(R.id.layout_new_good);
        mlayout_boutique = (RadioButton) findViewById(R.id.layout_boutique);
        mlayout_category = (RadioButton) findViewById(R.id.layout_category);
        mlayout_cart = (RadioButton) findViewById(R.id.layout_cart);
        mlayout_personal = (RadioButton) findViewById(R.id.layout_personal);
        mtvCartHint = (TextView) findViewById(R.id.tvCartHint);

        mRadios[0]=mlayout_new_good;
        mRadios[1]=mlayout_boutique;
        mRadios[2]=mlayout_category;
        mRadios[3]=mlayout_cart;
        mRadios[4]=mlayout_personal;
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
            case R.id.Layout_cart:
                index = 3;
                break;
            case cn.ucai.fulicenter.R.id.layout_personal:
                index = 4;
                break;
        }
        if (currentTabIndex != index) {
            setRadiosCheck(index);
            currentTabIndex = index;
        }
    }

    private void setRadiosCheck(int index) {
        for (int i=0;i<mRadios.length;i++) {
            if (i == index) {
                mRadios[i].setSelected(true);
            } else {
                mRadios[i].setSelected(false);
            }
        }
    }
}
