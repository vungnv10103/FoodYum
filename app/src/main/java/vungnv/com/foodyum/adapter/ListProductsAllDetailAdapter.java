package vungnv.com.foodyum.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vungnv.com.foodyum.R;
import vungnv.com.foodyum.model.ListProduct;

public class ListProductsAllDetailAdapter extends RecyclerView.Adapter<ListProductsAllDetailAdapter.ProductViewHolder> {

    private final Context context;
    private ArrayList<ListProduct> list;


    public ListProductsAllDetailAdapter(Context context) {
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<ListProduct> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            ListProduct item = list.get(position);
            if (item == null) {
                return;
            }
            holder.tvTitle.setText(item.getNameProduct());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            holder.rcvProduct.setLayoutManager(linearLayoutManager);

            ItemProductsAllDetailAdapter itemProductAdapter = new ItemProductsAllDetailAdapter(context);
            itemProductAdapter.setData(item.getList());
            holder.rcvProduct.setAdapter(itemProductAdapter);
        }


    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        RecyclerView rcvProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            rcvProduct = itemView.findViewById(R.id.rcvProduct);
        }
    }


}
