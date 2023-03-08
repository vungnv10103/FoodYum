package vungnv.com.foodyum.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.activities.AddToCartActivity;
import vungnv.com.foodyum.model.Product;

public class ItemProductsRecommendAdapter extends RecyclerView.Adapter<ItemProductsRecommendAdapter.viewHolder> implements Constant {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static List<Product> list;

    public ItemProductsRecommendAdapter(Context context, List<Product> list) {
        ItemProductsRecommendAdapter.context = context;
        ItemProductsRecommendAdapter.list = list;
    }

    public ItemProductsRecommendAdapter(Context context) {
        ItemProductsRecommendAdapter.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<Product> list) {
        ItemProductsRecommendAdapter.list = list;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_by_merchant, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            Product item = list.get(position);

            String idImage = item.img;
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            storageRef.child("images_product/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL
                    // Load the image using Glide
                    Glide.with(context)
                            .load(uri)
                            .into(holder.img);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d(TAG, "get image from firebase: " + exception.getMessage());
                }
            });
            holder.tvTitle.setText(item.name);
            if (item.status == 1) {
                holder.tvOutOfStock.setVisibility(View.VISIBLE);
            } else {
                holder.tvOutOfStock.setVisibility(View.INVISIBLE);
            }
            double discount = item.discount;
            double oldPrice = item.price;

            double newPrice = oldPrice - oldPrice / 100 * discount;

            if (discount == 0) {
                holder.tvDiscount.setVisibility(View.INVISIBLE);
                holder.tvPriceOld.setVisibility(View.INVISIBLE);
                holder.tvPriceNew.setText(item.price + "đ");
            } else {
                holder.tvDiscount.setText(discount + "%");
                holder.tvDiscount.setVisibility(View.VISIBLE);
                holder.tvPriceOld.setVisibility(View.VISIBLE);
                holder.tvPriceOld.setText(oldPrice + "đ");
                holder.tvPriceOld.setPaintFlags(holder.tvPriceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvPriceNew.setText(newPrice + "đ");
            }
        }

    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ExpandableTextView tvTitle;
        TextView tvOutOfStock, tvDiscount, tvPriceOld, tvPriceNew;
        ImageView img;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProduct2);
            //tvTitle = itemView.findViewById(R.id.tvNameRestaurant);
            tvTitle = itemView.findViewById(R.id.expand_text_view).findViewById(R.id.expand_text_view);
            tvOutOfStock = itemView.findViewById(R.id.tvOutOfStock);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvPriceOld = itemView.findViewById(R.id.tvPriceOld);
            tvPriceNew = itemView.findViewById(R.id.tvPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product item = list.get(getAdapterPosition());
                    if (item.status == 1) {
                        return;
                    }
                    Intent intent = new Intent(context, AddToCartActivity.class);
                    Bundle bundle = new Bundle();


                    bundle.putString("id", item.id);
                    bundle.putString("idMerchant", item.idUser);
                    bundle.putString("img", item.img);
                    bundle.putString("name", item.name);
                    bundle.putDouble("discount", item.discount);
                    if (item.discount > 0) {
                        bundle.putDouble("price", item.price - item.price / 100 * item.discount);
                    } else {
                        bundle.putDouble("price", item.price);
                    }

                    bundle.putString("desc", item.description);
                    intent.putExtra("data", bundle);
                    v.getContext().startActivity(intent);
                }
            });

        }
    }
}
