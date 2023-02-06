package vungnv.com.foodyum.activities;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.adapter.ItemProductsRecommendAdapter;
import vungnv.com.foodyum.adapter.ListProductsAllDetailAdapter;
import vungnv.com.foodyum.model.ListProduct;
import vungnv.com.foodyum.model.Product;
import vungnv.com.foodyum.model.ProductRecommend;


public class ShowAllProductsByMerchantActivity extends AppCompatActivity implements Constant {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageButton imgBack, imgFavourite, imgSearch;
    private ImageView imgProduct, imgInfoDetail;
    private TextView tvNameRestaurant, tvAddress;
    private LinearLayout lnlInfoDetail;
    private Toolbar toolbar;
    private RecyclerView rcvListProductRecommend, rcvListProduct;
    private SpotsDialog processDialog;

    private ItemProductsRecommendAdapter itemProductsRecommendAdapter;
    private ListProductsAllDetailAdapter listProductsAdapter;

    private final ArrayList<ListProduct> listProductAll = new ArrayList<>();
    private final ArrayList<Product> itemListProductAll = new ArrayList<>();
    ArrayList<ProductRecommend> aListProducts = new ArrayList<>();

    int temp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_products_by_merchant);

        init();

        customCollapsingToolbarLayout();

        String idUser = getIntent().getStringExtra("idUser");
        //String idUser = "fGQRuHz9mrWKXdk4oJroyhOHxaR2";
        getChildValue(tvNameRestaurant, idUser, "restaurantName");
        getChildValue(tvAddress, idUser, "address");
        setImage(idUser);

        getProductRecommend(idUser);
        //getAllProduct(idUser);


        imgBack.setOnClickListener(v -> onBackPressed());
        imgFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add to favourite
                Toast.makeText(ShowAllProductsByMerchantActivity.this, "updating...", Toast.LENGTH_SHORT).show();
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // enable search view
                Toast.makeText(ShowAllProductsByMerchantActivity.this, "updating...", Toast.LENGTH_SHORT).show();
            }
        });
        imgInfoDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowAllProductsByMerchantActivity.this, "updating...", Toast.LENGTH_SHORT).show();
            }
        });
        lnlInfoDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowAllProductsByMerchantActivity.this, "updating...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void init() {
        collapsingToolbarLayout = findViewById(R.id.collapsingToolBar);
        toolbar = findViewById(R.id.toolBarAllProduct);
        imgBack = findViewById(R.id.imgBack);
        imgFavourite = findViewById(R.id.imgFavourite);
        imgSearch = findViewById(R.id.imgSearch);
        tvNameRestaurant = findViewById(R.id.tvNameRestaurant);
        tvAddress = findViewById(R.id.tvAddress);
        imgInfoDetail = findViewById(R.id.imgInfoDetail);
        imgProduct = findViewById(R.id.detailer_img);
        rcvListProductRecommend = findViewById(R.id.rcvListProductRecommendByMerchant);
        rcvListProduct = findViewById(R.id.rcvListProductByMerchant);
        lnlInfoDetail = findViewById(R.id.lnlNameRestaurant);
        listProductsAdapter = new ListProductsAllDetailAdapter(getApplicationContext());
        processDialog = new SpotsDialog(ShowAllProductsByMerchantActivity.this, R.style.Custom);
    }

    private void setImage(String idMerchant) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("list_user_merchant").child(idMerchant).child("img");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String idImage = (String) dataSnapshot.getValue();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                storageRef.child("images_users/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL
                        // Load the image using Glide
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(imgProduct);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Log.d(TAG, "get image from firebase: " + exception.getMessage());
                    }
                });
                processDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
                processDialog.dismiss();
            }
        });

    }

    private void customCollapsingToolbarLayout() {
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
//        ImageView imageView = new ImageView(getApplicationContext());
//        imageView.setImageResource(R.drawable.ic_baseline_arrow_back_24);
//        collapsingToolbarLayout.addView(imageView);
//        TextView textView = new TextView(this);
//        textView.setText("Hello World");
//        textView.setGravity(Gravity.CENTER);
//        collapsingToolbarLayout.addView(textView);
//        // Set the gravity for the collapsed and expanded title
//        collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);
//        collapsingToolbarLayout.setExpandedTitleGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

    private void getChildValue(TextView textView, String idMerchant, String paramStr) {
        processDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("list_user_merchant").child(idMerchant).child(paramStr);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.getValue();
                textView.setText(value);
                if (paramStr.equals("restaurantName")) {
                    collapsingToolbarLayout.setTitle(value);
                }
                processDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
                processDialog.dismiss();
            }
        });

    }

    private void getAllProduct(String idMerchant) {
        processDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("list_product").child(idMerchant);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {
                    Product value = childSnapshot1.getValue(Product.class);
                    assert value != null;
                    String type = value.type;
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            itemListProductAll.clear();

                            for (DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {
                                Product value = childSnapshot1.getValue(Product.class);
                                assert value != null;
                                if (value.type.equals(type)) {
                                    itemListProductAll.add(value);
                                }
                            }
                            listProductAll.add(new ListProduct(value.type, itemListProductAll));
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                            rcvListProduct.setLayoutManager(linearLayoutManager);
                            listProductsAdapter.setData(listProductAll);
                            rcvListProduct.setAdapter(listProductsAdapter);
                            processDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Failed to read value
                            Log.d(TAG, "Failed to read value.", error.toException());
                            processDialog.dismiss();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void getProductRecommend(String idMerchant) {
        processDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("list_product").child(idMerchant);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                aListProducts.clear();
                for (DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {
                    ProductRecommend value = childSnapshot1.getValue(ProductRecommend.class);
                    assert value != null;
                    aListProducts.add(value);
                }
                if (aListProducts.size() == 0) {
                    Toast.makeText(ShowAllProductsByMerchantActivity.this, NO_DATA, Toast.LENGTH_SHORT).show();
                    return;
                }

                itemProductsRecommendAdapter = new ItemProductsRecommendAdapter(ShowAllProductsByMerchantActivity.this, aListProducts);
                rcvListProductRecommend.setAdapter(itemProductsRecommendAdapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ShowAllProductsByMerchantActivity.this, 2);
                rcvListProductRecommend.setLayoutManager(gridLayoutManager);
                rcvListProductRecommend.setHasFixedSize(true);
                rcvListProductRecommend.setNestedScrollingEnabled(false);
                processDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
                processDialog.dismiss();
            }
        });

    }
}