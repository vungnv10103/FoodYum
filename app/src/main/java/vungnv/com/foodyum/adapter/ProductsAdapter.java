package vungnv.com.foodyum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.activities.AddToCartActivity;
import vungnv.com.foodyum.activities.ShowAllProductsByMerchantActivity;
import vungnv.com.foodyum.model.Product;
import vungnv.com.foodyum.model.ProductRecommend;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.viewHolder> implements Constant {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static List<Product> list;

    public ProductsAdapter(Context context, List<Product> list) {
        ProductsAdapter.context = context;
        ProductsAdapter.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
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
        holder.tvDesc.setText(item.description);
        holder.tvRate.setText(String.format("%.2f", item.rate));
        holder.tvQuantitySold.setText("(" + item.quantity_sold + ")");

        float[] results = new float[1];
        double currentLongitude = 105.77553463;
        double currentLatitude = 21.06693654;

        double productLongitude = 106.296250;
        double productLatitude = 20.200560;
        Location.distanceBetween(currentLatitude, currentLongitude, productLatitude, productLongitude, results);
        float distanceInMeters = results[0];
        holder.tvDistance.setText(String.format("%.1f", distanceInMeters / 1000) + "km");
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView tvRate, tvQuantitySold, tvDistance;
        ExpandableTextView tvDesc, tvTitle;
        ImageView img;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProduct);
            //tvTitle = itemView.findViewById(R.id.tvNameRestaurant);
            tvTitle = itemView.findViewById(R.id.expand_text_view1).findViewById(R.id.expand_text_view1);
            tvDesc = itemView.findViewById(R.id.expand_text_view2).findViewById(R.id.expand_text_view2);
            tvRate = itemView.findViewById(R.id.tvRateProduct);
            tvQuantitySold = itemView.findViewById(R.id.tvQuantitySold);
            tvDistance = itemView.findViewById(R.id.tvDistance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowAllProductsByMerchantActivity.class);
                    Bundle bundle = new Bundle();
                    Product item = list.get(getAdapterPosition());

                    bundle.putBoolean("isShowBottomSheet", true);
                    bundle.putString("id", item.id);
                    bundle.putString("idUser", item.idUser);
                    intent.putExtra("data", bundle);
                    bundle.putString("img", item.img);
                    bundle.putString("name", item.name);
                    bundle.putString("desc", item.description);
                    bundle.putDouble("discount", item.discount);
                    if (item.discount > 0) {
                        bundle.putDouble("price", item.price - item.price / 100 * item.discount);
                    } else {
                        bundle.putDouble("price", item.price);
                    }
                    intent.putExtra("data", bundle);
                    context.startActivity(intent);
                }
            });

        }
    }
}
