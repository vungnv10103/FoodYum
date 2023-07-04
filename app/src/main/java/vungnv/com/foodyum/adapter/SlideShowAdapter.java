package vungnv.com.foodyum.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.model.ProductSlideShow;


public class SlideShowAdapter extends RecyclerView.Adapter<SlideShowAdapter.ViewHolder> implements Constant {
    private static List<ProductSlideShow> list;
    @SuppressLint("StaticFieldLeak")
    private static Context context;


    public SlideShowAdapter(List<ProductSlideShow> list, Context context) {
        SlideShowAdapter.list = list;
        SlideShowAdapter.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            String idImage = list.get(position).img;
            Log.d(TAG, "onBindViewHolder: " + idImage);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            storageRef.child("images_slideshow/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgSlideShow);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // làm màu tý :)
                    int idImg = list.get(getAdapterPosition()).id;
                    String imgUrl = "";
                    Intent browse;
                    switch (idImg) {

                        case 1:
                            imgUrl = "https://firebasestorage.googleapis.com/v0/b/food-app-2fb04.appspot.com/o/images_slideshow%2F1000002662?alt=media&token=dba4b7d3-d44a-4902-b7e7-25b1c85bd3ca";
                            browse = new Intent(Intent.ACTION_VIEW, Uri.parse(imgUrl));
                            context.startActivity(browse);
                            break;
                        case 2:
                            imgUrl = "https://firebasestorage.googleapis.com/v0/b/food-app-2fb04.appspot.com/o/images_slideshow%2F1000002661?alt=media&token=66b7050e-7743-4cf4-b8a8-4459d6b17cd5";
                            browse = new Intent(Intent.ACTION_VIEW, Uri.parse(imgUrl));
                            context.startActivity(browse);
                            break;

                        case 3:
                            imgUrl = "https://firebasestorage.googleapis.com/v0/b/food-app-2fb04.appspot.com/o/images_slideshow%2F1000002660?alt=media&token=6b6d3914-626d-4b03-88e8-f65c98d84e10";
                            browse = new Intent(Intent.ACTION_VIEW, Uri.parse(imgUrl));
                            context.startActivity(browse);
                            break;
                        case 4:
                            imgUrl = "https://firebasestorage.googleapis.com/v0/b/food-app-2fb04.appspot.com/o/images_slideshow%2F1000002659?alt=media&token=7038a108-e90c-499d-a49b-46595ed7fef1";
                            browse = new Intent(Intent.ACTION_VIEW, Uri.parse(imgUrl));
                            context.startActivity(browse);
                            break;
                        case 5:
                            imgUrl = "https://firebasestorage.googleapis.com/v0/b/food-app-2fb04.appspot.com/o/images_slideshow%2F1000002658?alt=media&token=d2f790ab-d2d9-4fbc-9e9b-1ca9b01ca613";
                            browse = new Intent(Intent.ACTION_VIEW, Uri.parse(imgUrl));
                            context.startActivity(browse);
                            break;

                        case 6:
                            String inURL = "https://firebasestorage.googleapis.com/v0/b/food-app-2fb04.appspot.com/o/images_slideshow%2F1000002657?alt=media&token=b9bf7ca1-bc07-45f6-a6ef-383ff10091d8";
                            browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
                            context.startActivity(browse);
                            break;
                    }

                }
            });
        }
    }

}
