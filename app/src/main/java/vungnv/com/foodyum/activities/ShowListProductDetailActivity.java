package vungnv.com.foodyum.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.MerchantDAO;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.adapter.ProductsAdapter;
import vungnv.com.foodyum.model.Merchant;
import vungnv.com.foodyum.model.Product;
import vungnv.com.foodyum.model.User;
import vungnv.com.foodyum.utils.NetworkChangeListener;

public class ShowListProductDetailActivity extends AppCompatActivity implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private TextView tvNameCate;
    private Button btnFilter;


    private ProductsAdapter productsAdapter;
    private List<Product> listProducts;
    private final ArrayList<Product> aListProducts = new ArrayList<>();
    private final ArrayList<User> aListUser = new ArrayList<>();
    private List<String> listIgnoreUser;

    private MerchantDAO merchantDAO;
    private RecyclerView rcvListProduct;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private int temp = 0;
    private boolean isReady = false;
    private SpotsDialog progressDialog;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list_product_detail);

        init();
        //getIDMerchantIgnore();
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        temp++;
        Map<String, String> map = new HashMap<String, String>();
        map.put("Cơm", "Rice");
        map.put("Bánh mỳ", "Bread");
        map.put("Bún-Phở", "Noodles");
        map.put("Salad", "Salad");
        map.put("Đồ uống", "Drinks");
        map.put("Bánh ngọt", "Cake");
        map.put("Đồ ăn nhanh", "Fast Food");
        map.put("Xúc xích", "Sausages");
        map.put("Khác", "Other");

        Intent intent = getIntent();
        Intent intent2 = getIntent();
        Bundle bd2 = intent2.getBundleExtra("option-filter");
        Bundle bundle = intent.getBundleExtra("data-category");
        if (bundle != null) {
            int id = bundle.getInt("id", 0);
            String img = bundle.getString("img");
            String type = bundle.getString("name");
            //Log.d(TAG, "id: " + id + "-img: " + img + "-name: " + type);
            String typeEn = map.get(type);
            if (typeEn == null) {
                tvNameCate.setText(type);
            } else {
                tvNameCate.setText(type + " / " + map.get(type));
            }

//            getIDMerchantIgnore(type);
            listProduct(type);


        }

        String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        if (currentLanguage.equals("en")) {
            if (bd2 != null) {
                String value = bd2.getString("value");
                String title = bd2.getString("title");
                tvNameCate.setText(title);
                btnFilter.setText(value);

                // get key word
                int index = tvNameCate.getText().toString().trim().indexOf("/");
                String type = tvNameCate.getText().toString().trim().substring(0, index - 1);
                Toast.makeText(this, "en: " + type, Toast.LENGTH_SHORT).show();
//                listProduct(type);
            }
        } else {
            if (bd2 != null) {
                String value = bd2.getString("value");
                String title = bd2.getString("title");
                tvNameCate.setText(title);
                btnFilter.setText(value);

                // get key word
                int index = tvNameCate.getText().toString().trim().indexOf("/");
                String type = tvNameCate.getText().toString().trim().substring(0, index - 1);
                Toast.makeText(this, "vi: " + type, Toast.LENGTH_SHORT).show();
//                listProduct(type);
            }
        }

        swipeRefreshLayout.setEnabled(false); // turn off swipeRefreshLayout.
        swipeRefreshLayout.setColorSchemeColors(
//                getResources().getColor(android.R.color.holo_red_light),
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green));


        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OptionFilterActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", tvNameCate.getText().toString().trim());
                intent.putExtra("data-temp", bundle);
                startActivity(intent);
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolBarShowListDetail);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_list_product);
        swipeRefreshLayout.setOnRefreshListener(this);
        tvNameCate = findViewById(R.id.tvNameCate);
        rcvListProduct = findViewById(R.id.rcvListProduct);
        btnFilter = findViewById(R.id.btnFilter);
        progressDialog = new SpotsDialog(ShowListProductDetailActivity.this, R.style.Custom);
        merchantDAO = new MerchantDAO(getApplicationContext());
    }

    private void getIDMerchantIgnore(String type) {
        String path = "list_product_all";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_user_merchant");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //merchantDAO.deleteTable();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Merchant merchant = data.getValue(Merchant.class);
                    assert merchant != null;
                    if (merchant.status == 2) {
                        String merchantIgnore = merchant.id;
                        Log.d(TAG, "onDataChange: " + merchantIgnore);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {
                                    Product value = childSnapshot1.getValue(Product.class);
                                    assert value != null;
                                    if (value.type.equals(type) && value.status != 0 && !value.idUser.equals(merchantIgnore)) {
                                        aListProducts.add(value);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                isReady = true;
                                Log.d(TAG, "Failed to read value.", error.toException());
                                progressDialog.dismiss();
                            }
                        });
                    } else if (merchant.status == 1) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {
                                    Product value = childSnapshot1.getValue(Product.class);
                                    assert value != null;
                                    if (value.type.equals(type) && value.status != 0) {
                                        aListProducts.add(value);
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                isReady = true;
                                Log.d(TAG, "Failed to read value.", error.toException());
                                progressDialog.dismiss();
                            }
                        });
                    }

                }
                productsAdapter = new ProductsAdapter(ShowListProductDetailActivity.this, aListProducts);
                rcvListProduct.setAdapter(productsAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShowListProductDetailActivity.this, RecyclerView.VERTICAL, false);
                rcvListProduct.setLayoutManager(linearLayoutManager);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListProduct.getContext(),
                        linearLayoutManager.getOrientation());
                rcvListProduct.addItemDecoration(dividerItemDecoration);

                isReady = true;
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void listProduct(String type) {
        progressDialog.show();
        String path = "list_product_all";

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                aListProducts.clear();
                for (DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {
                    Product value = childSnapshot1.getValue(Product.class);
                    assert value != null;

                    if (value.type.equals(type) && value.status != 0) {
                        aListProducts.add(value);
                    }
                }

                productsAdapter = new ProductsAdapter(ShowListProductDetailActivity.this, aListProducts);
                rcvListProduct.setAdapter(productsAdapter);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShowListProductDetailActivity.this, RecyclerView.VERTICAL, false);
                rcvListProduct.setLayoutManager(linearLayoutManager);
                linearLayoutManager.setSmoothScrollbarEnabled(true);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListProduct.getContext(),
                        linearLayoutManager.getOrientation());
                rcvListProduct.addItemDecoration(dividerItemDecoration);

                isReady = true;
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                isReady = true;
                Log.d(TAG, "Failed to read value.", error.toException());
                progressDialog.dismiss();
            }
        });

    }

    public void offServer() {
        FirebaseDatabase.getInstance().getReference().child("list_product_all")
                .onDisconnect().cancel();

    }


    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                int index = tvNameCate.getText().toString().trim().indexOf("/");
                String type = tvNameCate.getText().toString().trim().substring(0, index - 1);
                listProduct(type);

            }
        }, 1500);
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

    @Override
    public void onBackPressed() {
        if (isReady) {
            super.onBackPressed();
        }
    }
}