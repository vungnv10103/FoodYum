package vungnv.com.foodyum.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.MainActivity;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.model.ItemCart;
import vungnv.com.foodyum.model.User;
import vungnv.com.foodyum.utils.NetworkChangeListener;


public class AddToCartActivity extends AppCompatActivity implements Constant {
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private ImageView imgProduct;
    private TextView tvNameProduct, tvOldPrice, tvNewPrice, tvDesc;
    private EditText edNote;
    private RecyclerView rcvCombo;
    private ImageButton imgDecrease, imgIncrease;
    private TextView tvQuantity;
    private Button btnAddToCart;

    private String idMerchant = "";
    private String id = "";

    private Toolbar toolbar;
    private SpotsDialog processDialog;

    private ItemCartDAO itemCartDAO;

    private boolean isReady = false;

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_to_cart);

        init();
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        fillData();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String idUser = auth.getUid();

        String sPrice = tvNewPrice.getText().toString().trim();
        double priceOfOne = Double.parseDouble(sPrice.substring(0, sPrice.length() - 1));


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
                List<ItemCart> listCart = itemCartDAO.getALL(idUser, 1,2);
                Date currentTime = Calendar.getInstance().getTime();
                double price = Double.parseDouble(btnAddToCart.getText().toString().trim().substring(6, btnAddToCart.getText().toString().trim().length() - 1));
                String notes = edNote.getText().toString().trim();
                String nameProduct = tvNameProduct.getText().toString().trim();
                int quantity = Integer.parseInt(tvQuantity.getText().toString().trim());

                int temp = 0;

                for (int i = 0; i < listCart.size(); i++) {
                    if (listCart.get(i).id.equals(id)) {
                        temp++;
                    }
                }

                // check Exist
                if (temp > 0) {
                    int currentQuantity = itemCartDAO.getCurrentQuantity(id);
                    double currentPrice = itemCartDAO.getCurrentPrice(id);
                    int newQuantity = currentQuantity + quantity;
                    double newPrice = currentPrice / currentQuantity * newQuantity;
                    ItemCart item1 = new ItemCart();
                    item1.id = id;
                    item1.quantity = String.valueOf(newQuantity);
                    item1.price = newPrice;

                    if (itemCartDAO.updateQuantityAPrice(item1) > 0) {
                        Log.d(TAG, "update quantity success");
                        Toast.makeText(AddToCartActivity.this, UPDATE_QUANTITY, Toast.LENGTH_SHORT).show();
                    }
                    isReady = true;
                    onBackPressed();
                } else {

                    ItemCart item = new ItemCart();
                    item.id = id;
                    item.name = nameProduct;
                    item.idMerchant = idMerchant;
                    item.idUser = idUser;
                    item.dateTime = String.valueOf(currentTime);
                    item.quantity = String.valueOf(quantity);
                    item.status = 1;
                    item.price = price;
                    item.notes = notes;

                    if (itemCartDAO.insert(item) > 0) {
                        Log.d(TAG, "insert local db cart success");
                        Toast.makeText(AddToCartActivity.this, ADD_SUCCESS, Toast.LENGTH_SHORT).show();
                        View view = AddToCartActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        onBackPressed();
                    } else {
                        Toast.makeText(AddToCartActivity.this, ADD_FAIL, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "insert local db cart fail");
                    }

                    isReady = true;
                }


            }
        });


    }


    private void init() {
        imgProduct = findViewById(R.id.imgProduct1);
        tvNameProduct = findViewById(R.id.tvNameProduct);
        tvOldPrice = findViewById(R.id.tvOldPrice);
        tvNewPrice = findViewById(R.id.tvNewPrice);
        tvDesc = findViewById(R.id.tvDescription);
        edNote = findViewById(R.id.edNote);
        rcvCombo = findViewById(R.id.rcvListCombo);
        imgDecrease = findViewById(R.id.imgDecrease);
        imgIncrease = findViewById(R.id.imgIncrease);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        processDialog = new SpotsDialog(AddToCartActivity.this, R.style.Custom2);
        itemCartDAO = new ItemCartDAO(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tool_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_home:
                startActivity(new Intent(AddToCartActivity.this, MainActivity.class));
                break;
            case R.id.action_chat:
                Toast.makeText(this, "updating...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_cart:
                Intent intent = new Intent(AddToCartActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("screen", "cart");
                intent.putExtra("idScreen", bundle);
                startActivity(intent);
                finishAffinity();
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void fillData() {
        processDialog.show();
        Bundle data = getIntent().getBundleExtra("data");
        if (data != null) {
            idMerchant = data.getString("idMerchant");
            id = data.getString("id");
            double priceOfOne = data.getDouble("price");
            tvNameProduct.setText(data.getString("name"));
            double discount = data.getDouble("discount");
            tvOldPrice.setText(priceOfOne / (1 - discount / 100) + "đ");
            if (discount == 0) {
                tvOldPrice.setVisibility(View.INVISIBLE);
            }
            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvNewPrice.setText(priceOfOne + "đ");
            tvDesc.setText(data.getString("desc"));
            setImage(data.getString("img"));
            btnAddToCart.setText("Thêm • " + priceOfOne + "đ");


        }
        isReady = true;
        processDialog.dismiss();
    }

    private void setImage(String idImage) {
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
        isReady = true;
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

    @Override
    public void onBackPressed() {
        if (isReady) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);

        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}