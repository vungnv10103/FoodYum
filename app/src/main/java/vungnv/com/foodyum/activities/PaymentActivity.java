package vungnv.com.foodyum.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.DAO.UsersDAO;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.adapter.OrderAdapter;
import vungnv.com.foodyum.model.ItemCart;
import vungnv.com.foodyum.model.Order;
import vungnv.com.foodyum.utils.LocationProvider;

public class PaymentActivity extends AppCompatActivity implements Constant {
    private Toolbar toolbar;
    private LinearLayout selectAddress, lnlCoupon;
    private ConstraintLayout ctlMethodPayment;
    private RecyclerView rcvOrder, rcvCoupon;
    private TextView tvFullName, tvPhoneNumber, tvAddress;
    private TextView tvQuantityItem, tvTotalPrice, tvFeeDetail, tvFee, tvCoupon;
    private TextView tvAddCoupon, tvOldPrice, tvNewPrice;
    private Button btnPayment;

    private UsersDAO usersDAO;
    private List<ItemCart> listOrder;
    private ItemCartDAO itemCartDAO;

    private final long delay = 1000;
    private double fee = 0;

    private String valueContext = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        init();


        FirebaseAuth auth = FirebaseAuth.getInstance();
        String idUser = auth.getUid();
        String email = Objects.requireNonNull(auth.getCurrentUser()).getEmail();
        tvFullName.setText(usersDAO.getNameUser(email) + " | ");
        tvPhoneNumber.setText(usersDAO.getPhone(email));

        refreshAddress(auth);
        refreshList(idUser);


        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
        selectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PaymentActivity.this, "updating...", Toast.LENGTH_SHORT).show();
            }
        });
        tvFeeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double service = 2000;
                Dialog dialog = new Dialog(PaymentActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottom_sheet_fee);
                dialog.setCancelable(false);


                ImageButton imgClose = dialog.findViewById(R.id.imgClose);
                TextView tvContextFee = dialog.findViewById(R.id.tvContextFee);
                TextView tvFee = dialog.findViewById(R.id.tvFee);
                TextView tvContextService = dialog.findViewById(R.id.tvContextService);
                TextView tvService = dialog.findViewById(R.id.tvService);

                setContext(tvContextFee, "fee");
                setContext(tvContextService, "service_charge");

                tvService.setText(service + "đ");
                tvFee.setText(fee - service + "đ");


                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);

            }
        });
        ctlMethodPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PaymentActivity.this, "updating.", Toast.LENGTH_SHORT).show();
            }
        });
        tvAddCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentActivity.this, CouponActivity.class));
            }
        });
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resultPrice = tvNewPrice.getText().toString().trim();
                if (resultPrice.equals("0đ")) {
                    Toast.makeText(PaymentActivity.this, "Không có sản phẩm để mua !", Toast.LENGTH_SHORT).show();
                    return;
                }
                listOrder = itemCartDAO.getALL(idUser, 2);

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                @SuppressLint("SimpleDateFormat") SimpleDateFormat fm = new SimpleDateFormat();
                fm.applyPattern("HH:mm:ss-z");
                String time = fm.format(currentTime);
                String date = df.format(currentTime);
                // depends on the date and time (split)
                String idOrder = date.replaceAll("-", "")
                        + time.substring(0, time.indexOf("-")).replaceAll(":", "");
                for (int i = 0; i < listOrder.size(); i++) {
                    pushOrder(idOrder, idUser, listOrder.get(i).idMerchant, currentTime.toString(),
                            listOrder.get(i).name, listOrder.get(i).quantity, 1, listOrder.get(i).price, listOrder.get(i).notes);
                }


            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolBarPayment);
        usersDAO = new UsersDAO(getApplicationContext());
        selectAddress = findViewById(R.id.selectLocation);
        lnlCoupon = findViewById(R.id.lnlCoupon);
        ctlMethodPayment = findViewById(R.id.ctlMethodPayment);
        rcvOrder = findViewById(R.id.rcv_order);
        rcvCoupon = findViewById(R.id.rcvCoupon);
        tvFullName = findViewById(R.id.tvFullName);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvQuantityItem = findViewById(R.id.tvQuantityItem);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvFeeDetail = findViewById(R.id.tvFeeDetail);
        tvFee = findViewById(R.id.tvFee);
        tvCoupon = findViewById(R.id.tvCoupon);
        tvAddCoupon = findViewById(R.id.tvAddCoupon);
        btnPayment = findViewById(R.id.btnPayment);
        tvOldPrice = findViewById(R.id.tvOldPrice);
        tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvNewPrice = findViewById(R.id.tvNewPrice);
        itemCartDAO = new ItemCartDAO(getApplicationContext());
    }

    private void pushOrder(String id, String idUser, String idMerchant, String dateTime,
                           String item, int quantity, int status, double price, String notes) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(idMerchant);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long childCount = dataSnapshot.getChildrenCount();
                int index = (int) (childCount + 1);
                Order user = new Order(id, idUser, dateTime, item, quantity, status, price, notes);
                Map<String, Object> mListOrder = user.toMap();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                Map<String, Object> updates = new HashMap<>();
                updates.put("list_order/" + idMerchant + "/" + childCount, mListOrder);
                reference.updateChildren(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(PaymentActivity.this, REGISTER_SUCCESS, Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

    }

    private void askPermission() {
        ActivityCompat.requestPermissions(PaymentActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }


    private void refreshAddress(FirebaseAuth auth) {
        Thread t1 = new Thread() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {

                while (true) {
                    if (ContextCompat.checkSelfPermission(PaymentActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        LocationProvider locationManager = new LocationProvider(PaymentActivity.this);
                        locationManager.getLastLocation(new LocationProvider.OnLocationChangedListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                if (location != null) {
                                    Geocoder geocoder = new Geocoder(PaymentActivity.this, Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        String mLocation = addresses.get(0).getAddressLine(0);
                                        double currentLongitude = addresses.get(0).getLongitude();
                                        double currentLatitude = addresses.get(0).getLatitude();
                                        final String coordinate = currentLongitude + "-" + currentLatitude;
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tvAddress.setText(mLocation);
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } else {
                        askPermission();
                    }

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.start();
    }

    private void refreshList(String idUser) {
        Thread t1 = new Thread() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {

                while (true) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listOrder = itemCartDAO.getALL(idUser, 2);
                            if (listOrder.size() == 0) {
                                OrderAdapter orderAdapter = new OrderAdapter(getApplicationContext(), listOrder);
                                rcvOrder.setAdapter(orderAdapter);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                                rcvOrder.setLayoutManager(linearLayoutManager);
                                tvQuantityItem.setText("Tạm tính(0 món)");
                                tvTotalPrice.setText(0 + "đ");
                                tvFee.setText(0 + "đ");
                                tvOldPrice.setVisibility(View.INVISIBLE);
                                tvNewPrice.setText(0 + "đ");
                                return;
                            }
                            OrderAdapter orderAdapter = new OrderAdapter(getApplicationContext(), listOrder);
                            rcvOrder.setAdapter(orderAdapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                            rcvOrder.setLayoutManager(linearLayoutManager);

                            double totalPrice = 0;
                            int quantity = 0;
                            for (int i = 0; i < listOrder.size(); i++) {
                                quantity += listOrder.get(i).quantity;
                                totalPrice += listOrder.get(i).price;
                            }
                            tvQuantityItem.setText("Tạm tính(" + quantity + " món)");
                            tvTotalPrice.setText(totalPrice + "đ");
                            fee = totalPrice / 100 * 20;
                            tvFee.setText(fee + "đ");
                            double oldPrice = totalPrice + fee;
                            Bundle data = getIntent().getBundleExtra("data-coupon");
                            if (data == null) {
                                lnlCoupon.setVisibility(View.INVISIBLE);
                                tvOldPrice.setVisibility(View.INVISIBLE);
                                tvNewPrice.setText(oldPrice + "đ");
                            } else {
                                lnlCoupon.setVisibility(View.VISIBLE);
                                String idCoupon = data.getString("id");

                                double discount = data.getDouble("discount") * totalPrice / 100;

                                tvCoupon.setText(discount + "đ");
                                tvAddCoupon.setText(idCoupon);

                                tvOldPrice.setVisibility(View.VISIBLE);

                                tvOldPrice.setText(oldPrice + "đ");
                                tvNewPrice.setText(oldPrice - discount + "đ");

                            }

                        }
                    });


                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.start();
    }

    private void setContext(TextView textView, String paramString) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("asset_string").child(paramString);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                valueContext = (String) snapshot.getValue();
                textView.setText(valueContext);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}