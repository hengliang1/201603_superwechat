package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/6/15 0015.
 */
public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<CartBean> mCartList;
    CartItemViewHolder cartViewHolder;

    boolean isMore;

    public CartAdapter(Context mContext, ArrayList<CartBean> list) {
        this.mContext = mContext;
        this.mCartList = list;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater filter = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder = new CartItemViewHolder(filter.inflate(R.layout.item_cart, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        cartViewHolder = (CartItemViewHolder) holder;
        final CartBean cart = mCartList.get(position);
        GoodDetailsBean goods = cart.getGoods();
        if (goods == null) {
            return;
        }
        cartViewHolder.mtvGoodName.setText(goods.getGoodsName());
        cartViewHolder.mtvCartCount.setText("" + cart.getCount());
        cartViewHolder.mchkCart.setChecked(cart.isChecked());
        cartViewHolder.mtvGoodPrice.setText(goods.getRankPrice());

        ImageUtils.setNewGoodThumb(goods.getGoodsThumb(), cartViewHolder.mAvatar);

        AddDelCartClickListener listener = new AddDelCartClickListener(goods);
        cartViewHolder.mivAddCart.setOnClickListener(listener);
        cartViewHolder.mivReduceCart.setOnClickListener(listener);

        cartViewHolder.mchkCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cart.setChecked(isChecked);
                new UpdateCartTask(mContext, cart).exectue();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCartList == null ? 0 : mCartList.size();
    }

    public void initItems(ArrayList<CartBean> list) {
        if (mCartList != null && !mCartList.isEmpty()) {
            mCartList.clear();
        }
        mCartList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(ArrayList<CartBean> list) {
        mCartList.addAll(list);
        notifyDataSetChanged();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox mchkCart;
        NetworkImageView mAvatar;
        TextView mtvGoodName;
        ImageView mivAddCart;
        TextView mtvCartCount;
        ImageView mivReduceCart;
        TextView mtvGoodPrice;

        public CartItemViewHolder(View itemView) {
            super(itemView);
            mAvatar = (NetworkImageView) itemView.findViewById(R.id.ivGoodThumb);
            mtvGoodName = (TextView) itemView.findViewById(R.id.tvGoodName);
            mivAddCart = (ImageView) itemView.findViewById(R.id.ivAddCart);
            mtvCartCount = (TextView) itemView.findViewById(R.id.tvCartCount);
            mivReduceCart = (ImageView) itemView.findViewById(R.id.ivReduceCart);
            mtvGoodPrice = (TextView) itemView.findViewById(R.id.tvGoodPrice);
            mchkCart = (CheckBox) itemView.findViewById(R.id.chkSelect);
        }
    }

    class AddDelCartClickListener implements View.OnClickListener {

        public AddDelCartClickListener(GoodDetailsBean good) {
            this.good = good;
        }

        GoodDetailsBean good;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivAddCart:
                    Utils.addCart(mContext,good);
                    break;
                case R.id.ivReduceCart:
                    Utils.delCart(mContext, good);
                    break;
            }
        }
    }
}


