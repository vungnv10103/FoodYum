package vungnv.com.foodyum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.model.Category;


public class CategoriesAdapter extends BaseAdapter implements Constant {
    private final Context context;
    private final int layout;
    private List<Category> listCate = new ArrayList<>();

    public CategoriesAdapter(Context context, int layout, List<Category> listCate) {
        this.context = context;
        this.layout = layout;
        this.listCate = listCate;
    }

    @Override
    public int getCount() {
        return listCate.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String idImage = listCate.get(i).img;
        categoriesViewHolder viewHolder = null;
        viewHolder = new categoriesViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);

        viewHolder.img = view.findViewById(R.id.imgItemCate);
        viewHolder.name = view.findViewById(R.id.tvNameItemCate);
        view.setTag(viewHolder);
        viewHolder.name.setText(listCate.get(i).name);

        // set font with ttf file
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro_Bold.otf");
        viewHolder.name.setTypeface(typeface);

        viewHolder.img.setImageURI(Uri.parse(listCate.get(i).img));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        categoriesViewHolder finalViewHolder = viewHolder;
        storageRef.child("images/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL
                // Load the image using Glide
                Glide.with(context)
                        .load(uri)
                        .into(finalViewHolder.img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, "get image from firebase: " + exception.getMessage());
            }
        });

        return view;
    }

    private static class categoriesViewHolder {
        private ImageView img;
        private TextView name;
    }

}
