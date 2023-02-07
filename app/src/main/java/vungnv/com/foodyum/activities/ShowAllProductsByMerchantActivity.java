package vungnv.com.foodyum.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.adapter.ItemProductsRecommendAdapter;
import vungnv.com.foodyum.adapter.ListProductsAllDetailAdapter;
import vungnv.com.foodyum.model.ItemCart;
import vungnv.com.foodyum.model.ListProduct;
import vungnv.com.foodyum.model.Product;
import vungnv.com.foodyum.model.ProductRecommend;
import vungnv.com.foodyum.utils.LocationProvider;
import vungnv.com.foodyum.utils.NetworkChangeListener;


public class ShowAllProductsByMerchantActivity extends AppCompatActivity implements Constant {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageButton  imgFavourite, imgSearch;
    private ImageView imgProduct, imgInfoDetail;
    private TextView tvNameRestaurant, tvDistance, tvAddress;
    private LinearLayout lnlInfoDetail;
    private Toolbar toolbar;
    private RecyclerView rcvListProductRecommend, rcvListProduct;
    private SpotsDialog processDialog;

    private ItemProductsRecommendAdapter itemProductsRecommendAdapter;
    private ListProductsAllDetailAdapter listProductsAdapter;

    private final ArrayList<ListProduct> listProductAll = new ArrayList<>();
    private final ArrayList<Product> itemListProductAll = new ArrayList<>();
    ArrayList<ProductRecommend> aListProducts = new ArrayList<>();

    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    int temp = 0;
    private boolean isShowBottomSheet;
    private boolean isReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_products_by_merchant);

        init();

        customCollapsingToolbarLayout();
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Bundle data = getIntent().getBundleExtra("data");

        if (data != null) {
            isShowBottomSheet = data.getBoolean("isShowBottomSheet");
            String idUser = data.getString("idUser");
            //String idUser = "fGQRuHz9mrWKXdk4oJroyhOHxaR2";
            getChildValue(tvNameRestaurant, idUser, "restaurantName");
            getChildValue(tvAddress, idUser, "address");
            getChildValue(tvDistance, idUser, "coordinates");
            setImage(idUser);

            getProductRecommend(idUser);
            //getAllProduct(idUser);
        }


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
        toolbar = findViewById(R.id.toolBarAllProduct);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolBar);
        imgFavourite = findViewById(R.id.imgFavourite);
        imgSearch = findViewById(R.id.imgSearch);
        tvNameRestaurant = findViewById(R.id.tvNameRestaurant);
        tvDistance = findViewById(R.id.tvDistance);
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

    private void setImageProduct(String idMerchant, String idImage, ImageView imgProduct) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("images_product/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

    }

    private void askPermission() {
        ActivityCompat.requestPermissions(ShowAllProductsByMerchantActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private void getLastLocation(double merchantLongitude, double merchantLatitude, TextView textView) {
        processDialog.show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationProvider locationManager = new LocationProvider(ShowAllProductsByMerchantActivity.this);
            locationManager.getLastLocation(new LocationProvider.OnLocationChangedListener() {
                @SuppressLint({"DefaultLocale", "SetTextI18n"})
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(ShowAllProductsByMerchantActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            float[] results = new float[1];
                            double currentLongitude = addresses.get(0).getLongitude();
                            double currentLatitude = addresses.get(0).getLatitude();
                            Location.distanceBetween(currentLatitude, currentLongitude, merchantLatitude, merchantLongitude, results);
                            float distanceInMeters = results[0];
                            float distanceInKilometers = distanceInMeters / 1000;
                            textView.setText(String.format("%.1f", distanceInKilometers) + "km");
                            Log.d(TAG, "distance: " + String.format("%.1f", distanceInMeters / 1000) + "km");
                            processDialog.dismiss();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            processDialog.dismiss();
            askPermission();
        }
    }

    private void customCollapsingToolbarLayout() {
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
    }

    private void getChildValue(TextView textView, String idMerchant, String paramStr) {
        processDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("list_user_merchant").child(idMerchant).child(paramStr);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.getValue();

                if (paramStr.equals("restaurantName")) {
                    collapsingToolbarLayout.setTitle(value);
                } else if (paramStr.equals("coordinates")) {
                    assert value != null;
                    if (value.length() == 0) {
                        return;
                    }
                    double merchantLongitude = Double.parseDouble(value.substring(0, value.indexOf("-")));
                    double merchantLatitude = Double.parseDouble(value.substring(value.indexOf("-") + 1, value.length()));
                    Log.d(TAG, "long: " + merchantLongitude);
                    Log.d(TAG, "la: " + merchantLatitude);
                    getLastLocation(merchantLongitude, merchantLatitude, textView);

                } else {
                    textView.setText(value);
                }
                isReady = true;
                processDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                isReady = true;
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
                            isReady = true;
                            processDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Failed to read value
                            isReady = true;
                            Log.d(TAG, "Failed to read value.", error.toException());
                            processDialog.dismiss();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                isReady = true;
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
                isReady = true;
                processDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                isReady = true;
                Log.d(TAG, "Failed to read value.", error.toException());
                processDialog.dismiss();
            }
        });

    }


    private void animationDecrease(int currentQuantity, ImageButton imgDecrease) {
        if (currentQuantity > 1) {
            imgDecrease.setImageResource(R.drawable.ic_decrease_teal);
            imgDecrease.setBackgroundResource(R.drawable.bg_corner_img_button_increase);
        } else {
            imgDecrease.setImageResource(R.drawable.ic_decrease_gray);
            imgDecrease.setBackgroundResource(R.drawable.bg_corner_img_button_decrease);
        }
    }

    private void animationIncrease(int currentQuantity, ImageButton imgIncrease) {
        if (currentQuantity >= 20) {
            imgIncrease.setImageResource(R.drawable.ic_increase_gray);
            imgIncrease.setBackgroundResource(R.drawable.bg_corner_img_button_decrease);
        } else {
            imgIncrease.setImageResource(R.drawable.ic_increase_teal);
            imgIncrease.setBackgroundResource(R.drawable.bg_corner_img_button_increase);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);


        Bundle data = getIntent().getBundleExtra("data");
        if (data == null) {
            Log.d(TAG, "error get data from adapter");
            return;
        }
        ItemCartDAO itemCartDAO = new ItemCartDAO(getApplicationContext());


        Dialog dialog = new Dialog(ShowAllProductsByMerchantActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet);
        dialog.setCancelable(false);

        // code


        ImageButton imgClose = dialog.findViewById(R.id.imgClose);
        ImageView imgProduct = dialog.findViewById(R.id.imgProduct);
        TextView tvNameProduct = dialog.findViewById(R.id.tvNameProduct);
        TextView tvOldPrice = dialog.findViewById(R.id.tvOldPrice);
        TextView tvNewPrice = dialog.findViewById(R.id.tvPrice);
        TextView tvDescription = dialog.findViewById(R.id.tvDescription);
        EditText edNote = dialog.findViewById(R.id.edNote);
        ImageButton imgDecrease = dialog.findViewById(R.id.imgDecrease);
        TextView tvQuantity = dialog.findViewById(R.id.tvQuantity);
        ImageButton imgIncrease = dialog.findViewById(R.id.imgIncrease);
        Button btnAddToCart = dialog.findViewById(R.id.btnAddToCart);


        String idUser = data.getString("idUser");
        String id = data.getString("id");
        double sPriceOfOne = data.getDouble("price");
        double discount = data.getDouble("discount");
        tvNameProduct.setText(data.getString("name"));
        tvNewPrice.setText(sPriceOfOne + "đ");
        tvDescription.setText(data.getString("desc"));
        setImageProduct(idUser, data.getString("img"), imgProduct);
        btnAddToCart.setText("Thêm • " + sPriceOfOne + "đ");

        tvOldPrice.setText(sPriceOfOne / (1 - discount / 100) + "đ");
        if (discount == 0){
            tvOldPrice.setVisibility(View.INVISIBLE);
        }
        tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String sPrice = tvNewPrice.getText().toString().trim();
        double priceOfOne = Double.parseDouble(sPrice.substring(0, sPrice.length() - 1));
        isReady = true;

        imgIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(tvQuantity.getText().toString().trim());
                currentQuantity++;
                if (currentQuantity > 20) {
                    animationIncrease(currentQuantity, imgIncrease);
                    return;
                }
                tvQuantity.setText(String.valueOf(currentQuantity));
                animationIncrease(currentQuantity, imgIncrease);
                animationDecrease(currentQuantity, imgDecrease);
                btnAddToCart.setText("Thêm • " + priceOfOne * currentQuantity + "đ");

            }
        });
        imgDecrease.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(tvQuantity.getText().toString().trim());
                currentQuantity--;
                if (currentQuantity < 1) {
                    return;
                }
                tvQuantity.setText(String.valueOf(currentQuantity));
                animationIncrease(currentQuantity, imgIncrease);
                animationDecrease(currentQuantity, imgDecrease);
                btnAddToCart.setText("Thêm • " + priceOfOne * currentQuantity + "đ");
            }
        });
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ISO 8601 (format date)

                Date currentTime = Calendar.getInstance().getTime();
                double price = Double.parseDouble(btnAddToCart.getText().toString().trim().substring(6, btnAddToCart.getText().toString().trim().length() - 1));
                String notes = edNote.getText().toString().trim();
                ItemCart item = new ItemCart();
                item.id = id;
                item.name = tvNameProduct.getText().toString().trim();
                item.idUser = idUser;
                item.dateTime = String.valueOf(currentTime);
                item.quantity = Integer.parseInt(tvQuantity.getText().toString().trim());
                item.status = 1;
                item.price = price;
                item.notes = notes;


                if (itemCartDAO.insert(item) > 0) {
                    Log.d(TAG, "insert local db cart success");
                    dialog.dismiss();
                    Toast.makeText(ShowAllProductsByMerchantActivity.this, ADD_SUCCESS, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShowAllProductsByMerchantActivity.this, ADD_FAIL, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "insert local db cart fail");
                }

            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowBottomSheet = false;
                dialog.dismiss();
            }
        });
        if (isShowBottomSheet) {
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }


        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (isReady){
            super.onBackPressed();
        }
    }
}