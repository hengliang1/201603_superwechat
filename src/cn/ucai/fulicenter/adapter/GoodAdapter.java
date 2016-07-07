package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by sks on 2016/6/21.
 */
public class GoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<NewGoodBean> mGoodList;

    GoodItemViewHolder goodHolder;
    FooterViewHolder footerHolder;

    private String footerText;
    private boolean isMore;
    int sortBy;

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
        sort(sortBy);
        notifyDataSetChanged();
    }

    private void sort(final int sortBy) {
        Collections.sort(mGoodList, new Comparator<NewGoodBean>() {
            @Override
            public int compare(NewGoodBean g1, NewGoodBean g2) {
                int result = 0;
                switch (sortBy) {
                    case I.SORT_BY_ADDTIME_ASC:
                        result = (int) (g1.getAddTime() - g2.getAddTime());
                        break;
                    case I.SORT_BY_ADDTIME_DESC:
                        result = (int) (g2.getAddTime() - g1.getAddTime());
                        break;
                    case I.SORT_BY_PRICE_ASC: {
                        int p1 = converPrice(g1.getCurrencyPrice());
                        int p2 = converPrice(g2.getCurrencyPrice());
                        result = p1 - p2;
                        break;
                    }
                    case I.SORT_BY_PRICE_DESC: {
                        int p1 = converPrice(g1.getCurrencyPrice());
                        int p2 = converPrice(g2.getCurrencyPrice());
                        result = p2 - p1;
                    }
                    break;
                }
                return result;
            }

            private int converPrice(String price) {
                price = price.substring(price.indexOf("￥")+1);
                int p1 = Integer.parseInt(price);
                return p1;
            }
        });
    }

    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
        notifyDataSetChanged();
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public GoodAdapter(Context mContext, ArrayList<NewGoodBean> mGoodList, int sortBy) {
        this.mContext = mContext;
        this.mGoodList = mGoodList;
        this.sortBy = sortBy;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case I.TYPE_ITEM:
                holder = new GoodItemViewHolder(inflater.inflate(R.layout.item_new_good, parent, false));
                break;
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflater.inflate(R.layout.item_footer, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            footerHolder = (FooterViewHolder) holder;
            footerHolder.tvFooter.setText(footerText);
            footerHolder.tvFooter.setVisibility(View.VISIBLE);
        }
        if (holder instanceof GoodItemViewHolder) {
            goodHolder = (GoodItemViewHolder) holder;
            final NewGoodBean good = mGoodList.get(position);
            goodHolder.tvGoodName.setText(good.getGoodsName());
            goodHolder.tvGoodPrice.setText(good.getCurrencyPrice());
            ImageUtils.setNewGoodThumb(good.getGoodsThumb(), goodHolder.nivThumb);


            goodHolder.layoutGood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class)
                            .putExtra(D.NewGood.KEY_GOODS_ID, good.getGoodsId()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mGoodList == null ? 1 : mGoodList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }

    public void initList(ArrayList<NewGoodBean> list) {
        if (mGoodList != null)
            mGoodList.clear();
        mGoodList.addAll(list);

    }

    public void initItems(ArrayList<NewGoodBean> list) {
        if (mGoodList != null && !mGoodList.isEmpty()) {
            mGoodList.clear();
        }
        mGoodList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(ArrayList<NewGoodBean> list) {
        for (NewGoodBean ng : list) {
            if (!mGoodList.contains(ng)) {
                mGoodList.add(ng);
                notifyDataSetChanged();
            }
        }
    }

    class GoodItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutGood;
        NetworkImageView nivThumb;
        TextView tvGoodName;
        TextView tvGoodPrice;

        public GoodItemViewHolder(View itemView) {
            super(itemView);
            layoutGood = (LinearLayout) itemView.findViewById(R.id.layout_good);
            nivThumb = (NetworkImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            tvGoodPrice = (TextView) itemView.findViewById(R.id.tv_good_price);
        }
    }

}