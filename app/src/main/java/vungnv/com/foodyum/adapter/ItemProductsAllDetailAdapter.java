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

public class ItemProductsAllDetailAdapter extends RecyclerView.Adapter<ItemProductsAllDetailAdapter.viewHolder> implements Constant {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static List<Product> list;

    public ItemProductsAllDetailAdapter(Context context, List<Product> list) {
        ItemProductsAllDetailAdapter.context = context;
        ItemProductsAllDetailAdapter.list = list;
    }

    public ItemProductsAllDetailAdapter(Context context) {
        ItemProductsAllDetailAdapter.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<Product> list) {
        ItemProductsAllDetailAdapter.list = list;
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

            String idImage = item.img + ".png";
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
            double discount = item.discount;
            double oldPrice = item.price;

            double newPrice = oldPrice - oldPrice / 100 * discount;
            if (discount == 0) {
                holder.tvDiscount.setVisibility(View.INVISIBLE);
                holder.tvPriceOld.setVisibility(View.INVISIBLE);
                holder.tvPriceNew.setText(item.price + "đ");
            } else {
                holder.tvDiscount.setText(discount + "%");
                holder.tvPriceOld.setText(oldPrice + "đ");
                holder.tvDiscount.setVisibility(View.VISIBLE);
                holder.tvPriceOld.setVisibility(View.VISIBLE);

                holder.tvPriceOld.setPaintFlags(holder.tvPriceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvPriceNew.setText(item.price - item.price * discount / 100 + "đ");
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
        TextView tvDiscount, tvPriceOld, tvPriceNew;
        ImageView img;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProduct);
            //tvTitle = itemView.findViewById(R.id.tvNameRestaurant);
            tvTitle = itemView.findViewById(R.id.expand_text_view).findViewById(R.id.expand_text_view);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvPriceOld = itemView.findViewById(R.id.tvPriceOld);
            tvPriceNew = itemView.findViewById(R.id.tvPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddToCartActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("img", list.get(getAdapterPosition()).img);
                    bundle.putString("name", list.get(getAdapterPosition()).name);
                    bundle.putDouble("price", list.get(getAdapterPosition()).price);
                    bundle.putString("desc", list.get(getAdapterPosition()).description);
                    intent.putExtra("data", bundle);
                    v.getContext().startActivity(intent);
                }
            });

        }
    }
}
