package vungnv.com.foodyum.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.activities.PaymentActivity;
import vungnv.com.foodyum.model.Coupon;


public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> implements Constant, Filterable {
    private final Context context;
    private static List<Coupon> list;
    private final List<Coupon> listOld;


    public CouponAdapter(Context context, List<Coupon> list) {
        CouponAdapter.list = list;
        this.listOld = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            Coupon item = list.get(position);

            String idImage = item.img + ".png";
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            storageRef.child("images_coupon/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL
                    // Load the image using Glide
                    Glide.with(context)
                            .load(uri)
                            .into(holder.imgCoupon);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d(TAG, "get image from firebase: " + exception.getMessage());
                }
            });

            holder.tvDiscount.setText(item.discount + "%");
            holder.tvExpiry.setText(item.expiry);
            holder.btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PaymentActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putDouble("discount", item.discount);
                    bundle.putString("id", item.id);
                    intent.putExtra("data-coupon", bundle);
                    v.getContext().startActivity(intent);
                    ((Activity) context).finish();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String str = constraint.toString();
                if (str.isEmpty()) {
                    list = listOld;
                } else {
                    List<Coupon> mList = new ArrayList<>();
                    for (Coupon item : listOld) {
                        if (item.id.toLowerCase().contains(str.toLowerCase())) {
                            mList.add(item);
                        }
                    }
                    list = mList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
//                list = (List<ProductModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCoupon;
        TextView tvDiscount, tvExpiry;
        Button btnApply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCoupon = itemView.findViewById(R.id.imgCoupon);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            btnApply = itemView.findViewById(R.id.btnApply);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "" + list.get(getAdapterPosition()).id, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
