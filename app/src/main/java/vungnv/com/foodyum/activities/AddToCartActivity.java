package vungnv.com.foodyum.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.MainActivity;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.model.ItemCart;


public class AddToCartActivity extends AppCompatActivity implements Constant {
    private ImageButton imgBack;
    private ImageView imgProduct;
    private TextView tvNameProduct, tvPrice, tvDesc;
    private EditText edNote;
    private RecyclerView rcvCombo;
    private ImageButton imgDecrease, imgIncrease;
    private TextView tvQuantity;
    private Button btnAddToCart;

    private String idUser = "";
    private String id = "";

    private Toolbar toolbar;
    private SpotsDialog processDialog;

    private ItemCartDAO itemCartDAO;

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_to_cart);

        init();
        fillData();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String sPrice = tvPrice.getText().toString().trim();
        double priceOfOne = Double.parseDouble(sPrice.substring(0, sPrice.length() - 1));


        imgIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(tvQuantity.getText().toString().trim());
                currentQuantity++;
                if (currentQuantity > 20) {
                    return;
                }
                tvQuantity.setText(String.valueOf(currentQuantity));
                animation(currentQuantity);
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
                animation(currentQuantity);
                btnAddToCart.setText("Thêm • " + priceOfOne * currentQuantity + "đ");
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ISO 8601 (format date)

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat fm = new SimpleDateFormat();
                fm.applyPattern("HH:mm:ss-z");
                String time = fm.format(currentTime);
                String date = df.format(currentTime);

                // depends on the date and time (split)
                String idOrder = date.replaceAll("-", "")
                        + time.substring(0, time.indexOf("-")).replaceAll(":", "");

//                OrderModel item = new OrderModel();
//                item.id = idOrder + item.stt;
//                item.idUser = idUser;
//                item.dateTime = time;
//                item.items =

                double price = Double.parseDouble(btnAddToCart.getText().toString().trim().substring(6, btnAddToCart.getText().toString().trim().length() - 1));

                ItemCart item = new ItemCart();
                item.id = id;
                item.name = tvNameProduct.getText().toString().trim();
                item.idUser = idUser;
                item.dateTime = String.valueOf(currentTime);
                item.quantity = Integer.parseInt(tvQuantity.getText().toString().trim());
                item.status = 1;
                item.price = price;

                if (itemCartDAO.insert(item) > 0) {
                    Log.d(TAG, "insert local db cart success");
                    Toast.makeText(AddToCartActivity.this,ADDED , Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                else {
                    Log.d(TAG, "insert local db cart fail");
                }


            }
        });


    }


    private void init() {
        imgBack = findViewById(R.id.imgBack);
        imgProduct = findViewById(R.id.imgProduct1);
        tvNameProduct = findViewById(R.id.tvNameProduct);
        tvPrice = findViewById(R.id.tvPrice);
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

            idUser = data.getString("idUser");
            id = data.getString("id");
            double priceOfOne = data.getDouble("price");
            tvNameProduct.setText(data.getString("name"));
            tvPrice.setText(priceOfOne + "đ");
            tvDesc.setText(data.getString("desc"));
            setImage(data.getString("img"));
            btnAddToCart.setText("Thêm • " + priceOfOne + "đ");


        }
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
    }

    private void animation(int currentQuantity) {
        if (currentQuantity > 1) {
            imgDecrease.setImageResource(R.drawable.ic_decrease_teal);
            imgDecrease.setBackgroundResource(R.drawable.bg_corner_img_button_increase);
        } else {
            imgDecrease.setImageResource(R.drawable.ic_decrease_gray);
            imgDecrease.setBackgroundResource(R.drawable.bg_corner_img_button_decrease);
        }
    }
}