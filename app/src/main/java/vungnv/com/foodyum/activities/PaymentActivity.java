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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.DAO.UsersDAO;
import vungnv.com.foodyum.MainActivity;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.adapter.OrderAdapter;
import vungnv.com.foodyum.model.Asset;
import vungnv.com.foodyum.model.ItemCart;
import vungnv.com.foodyum.model.ListItemInOrder;
import vungnv.com.foodyum.model.Order;
import vungnv.com.foodyum.model.ProductSlideShow;
import vungnv.com.foodyum.utils.LocationProvider;
import vungnv.com.foodyum.utils.NetworkChangeListener;

public class PaymentActivity extends AppCompatActivity implements Constant, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;

    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
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
    private double service_charge = 0;
    private int waiting_time = 0;

    private String valueContext = "";

    private int posByUser = 0;

    private static final String idUser = FirebaseAuth.getInstance().getUid();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        init();
        //

        swipeRefreshLayout.setEnabled(false); // turn off swipe
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String name = Objects.requireNonNull(auth.getCurrentUser()).getDisplayName();
        String phoneNumber = auth.getCurrentUser().getPhoneNumber();

        if (name != null) {
            tvFullName.setText(name + " | ");
        } else {
            tvFullName.setText(NO_NAME);
        }
        if (phoneNumber != null) {
            tvPhoneNumber.setText(phoneNumber);
        } else {
            phoneNumber = usersDAO.getPhone(auth.getCurrentUser().getEmail());
            if (phoneNumber == null || phoneNumber.length() == 0) {
                tvPhoneNumber.setText(NO_PHONE);
            } else {
                tvPhoneNumber.setText(phoneNumber);
            }

        }

        getLocation();
        getListItemInOrder();

        // get service_charge_value
        setContext(null, "service_charge_value");
        // get waiting_time
        setContext(null, "waiting_time");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("screen", "cart");
                intent.putExtra("idScreen", bundle);
                startActivity(intent);
                finishAffinity();

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
                String valuePrice = tvTotalPrice.getText().toString().trim();
                double totalPrice = Double.parseDouble(valuePrice.substring(0, valuePrice.length() - 1));
                if (totalPrice == 0) {
                    return;
                }

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

                tvService.setText(service_charge + "đ");
                tvFee.setText(fee - service_charge + "đ");

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
                String resultPrice = tvNewPrice.getText().toString().trim();
                if (resultPrice.equals("0đ")) {
                    return;
                }
                startActivity(new Intent(PaymentActivity.this, CouponActivity.class));
            }
        });
        String finalPhoneNumber = phoneNumber;
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNull(name, finalPhoneNumber)) {
                    showConfirmDialog();
                    return;
                }

                String resultPrice = tvNewPrice.getText().toString().trim();
                if (resultPrice.equals("0đ")) {
                    Toast.makeText(PaymentActivity.this, NO_TO_BUY, Toast.LENGTH_SHORT).show();
                    return;
                }

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat fm = new SimpleDateFormat();
                fm.applyPattern("HH:mm:ss-z");
                String time = fm.format(currentTime);
                String date = df.format(currentTime);
                String dateTime = date + "-" + time;
                // depends on the date and time (split)
                String idOrder = date.replaceAll("/", "")
                        + time.substring(0, time.indexOf("-")).replaceAll(":", "");

                String sTotalPrice = tvNewPrice.getText().toString().trim();
                double totalPrice = Double.parseDouble(sTotalPrice.substring(0, sTotalPrice.length() - 1));

                listOrder = itemCartDAO.getALL(idUser, 2);

                // Check if there are more than 2 products of the same restaurant
                Set<String> idMerchant = new HashSet<>();
                Set<String> duplicatesIdMerchant = new HashSet<>();
                List<String> nonDuplicatesIdMerchant = new ArrayList<String>();

                for (int i = 0; i < listOrder.size(); i++) {
                    if (!idMerchant.add(listOrder.get(i).idMerchant)) {
                        duplicatesIdMerchant.add(listOrder.get(i).idMerchant);
                    }
                }

                for (int i = 0; i < listOrder.size(); i++) {
                    if (!duplicatesIdMerchant.contains(listOrder.get(i).idMerchant)) {
                        nonDuplicatesIdMerchant.add(listOrder.get(i).idMerchant);
                    }
                }
                for (String id : nonDuplicatesIdMerchant) {
                    listOrder = itemCartDAO.getALL(idUser, id, 2);
                    // push default
                    for (int i = 0; i < listOrder.size(); i++) {
                        // true for 1 idMerchant at a time (>2 id in cart => error) fixed on 23/02/2023
                        ItemCart itemCart = listOrder.get(i);
                        pushOrderByClient(idOrder, idUser, itemCart.idMerchant, dateTime,
                                itemCart.name, itemCart.quantity, 1, String.valueOf(itemCart.price), waiting_time,
                                itemCart.notes);
                    }
                }

                List<ListItemInOrder> listItemInOrders = new ArrayList<>();
                if (duplicatesIdMerchant.size() > 0) {
                    // There are duplicates in the list
                    for (String id : duplicatesIdMerchant) {
                        for (String id1 : idMerchant) {
                            if (id.equals(id1)) {
                                Log.d(TAG, "ID duplicate: " + id1);
                                listOrder = itemCartDAO.getALL(idUser, id, 2);
                                for (int i = 0; i < listOrder.size(); i++) {
                                    // combined into 1 order
                                    listItemInOrders.add(new ListItemInOrder(listOrder.get(i).idMerchant,
                                            String.valueOf(listOrder.get(i).quantity), listOrder.get(i).name,
                                            String.valueOf(listOrder.get(i).price), listOrder.get(i).notes));
                                }
                            }

                        }
                    }
                    List<ListItemInOrder> mergedLists1 = new ArrayList<>();
                    Map<String, ListItemInOrder> map = new HashMap<>();
                    for (ListItemInOrder item : listItemInOrders) {
                        String idMerchant2 = item.idMerchant;
                        if (map.containsKey(idMerchant2)) {
                            ListItemInOrder mergedItem = map.get(idMerchant2);
                            assert mergedItem != null;
                            mergedItem.quantity = mergedItem.quantity + "-" + item.quantity;
                            mergedItem.name = mergedItem.name + "-" + item.name;
                            mergedItem.price = mergedItem.price + "-" + item.price;
                            mergedItem.notes = mergedItem.notes + "-" + item.notes;
                        } else {
                            mergedLists1.add(item);
                            map.put(idMerchant2, item);
                        }
                    }
                    Log.d(TAG, "size: " + mergedLists1.size());
                    for (int i = 0; i < mergedLists1.size(); i++) {
                        Log.d(TAG, "mergedList: " + mergedLists1.get(i).toString());
                        ListItemInOrder itemCart = mergedLists1.get(i);
                        pushOrderByClient(idOrder, idUser, itemCart.idMerchant, dateTime,
                                itemCart.name, itemCart.quantity, 1, itemCart.price, waiting_time, itemCart.notes);
                    }

                }
                // update status item cart 2 -> 0 (hide item bought)
                ItemCart item = new ItemCart();
                item.status = 0;
                for (int i = 0; i < listOrder.size(); i++) {
                    item.id = listOrder.get(i).id;
                    if (itemCartDAO.updateStatus(item) > 0) {
                        Log.d(TAG, "update status item cart 2 -> 0");
                    }
                }

                getListItemInOrder();
            }

        });
    }

    private void init() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_payment);
        swipeRefreshLayout.setOnRefreshListener(this);
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
            String item, String quantity, int status, String price, int waitingTime, String notes) {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order")
                .child(idMerchant);

        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                long childCount = currentData.getChildrenCount();
                String idNew = id + childCount;
                Order order = new Order((int) childCount, posByUser, idNew, idUser, idMerchant, dateTime, item,
                        quantity, status, price, waitingTime, notes);
                Map<String, Object> mListOrder = order.toMap();
                currentData.child(String.valueOf(childCount)).setValue(mListOrder);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                    @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.d(TAG, "onComplete push order: " + "Transaction failed.");
                    Toast.makeText(PaymentActivity.this, ORDER_FAIL, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, ORDER_SUCCESS + ", id: " + id);
                    lnlCoupon.setVisibility(View.INVISIBLE);
                    tvAddCoupon.setText(ADD_COUPON);
                    Toast.makeText(PaymentActivity.this, ORDER_SUCCESS, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void pushOrderByClient(String id, String idUser, String idMerchant, String dateTime,
            String item, String quantity, int status, String price, int waitingTime, String notes) {

        final DatabaseReference refNew = FirebaseDatabase.getInstance().getReference()
                .child("list_order_by_idUserClient").child(idUser);

        refNew.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                long childCount = currentData.getChildrenCount();
                posByUser = (int) childCount;
                String idNew = id + childCount;
                Order order = new Order((int) childCount, idNew, idUser, idMerchant, dateTime, item, quantity, status,
                        price, waitingTime, notes);
                Map<String, Object> mListOrder = order.toMap();
                currentData.child(String.valueOf(childCount)).setValue(mListOrder);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                    @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.d(TAG, "onComplete push order: " + "Transaction failed.");
                    Toast.makeText(PaymentActivity.this, ORDER_FAIL, Toast.LENGTH_SHORT).show();
                } else {
                    pushOrder(id, idUser, idMerchant, dateTime, item, quantity, status, price, waitingTime, notes);

                }
            }
        });

    }

    private boolean isNull(String name, String phoneNumber) {
        return (name == null || phoneNumber == null);
    }

    private void showConfirmDialog() {
        Dialog dialog = new Dialog(PaymentActivity.this);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.setCancelable(false);
        Button btnCancel, btnConfirm;

        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnConfirm = dialog.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v1 -> {
            // update information
            dialog.dismiss();
            startActivity(new Intent(PaymentActivity.this, InformationUserActivity.class));
        });
        btnCancel.setOnClickListener(v12 -> dialog.dismiss());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(PaymentActivity.this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE);
    }

    @SuppressLint("SetTextI18n")
    public void getListItemInOrder() {
        listOrder = itemCartDAO.getALL(idUser, 2);
        if (listOrder.size() == 0) {
            OrderAdapter orderAdapter = new OrderAdapter(getApplicationContext(), listOrder, PaymentActivity.this);
            rcvOrder.setAdapter(orderAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                    RecyclerView.VERTICAL, false);
            rcvOrder.setLayoutManager(linearLayoutManager);
            tvQuantityItem.setText(DEFAULT_TV_QUANTITY);
            tvTotalPrice.setText(0 + "đ");
            tvFee.setText(0 + "đ");
            tvOldPrice.setVisibility(View.INVISIBLE);
            tvNewPrice.setText(0 + "đ");
            return;
        }
        OrderAdapter orderAdapter = new OrderAdapter(getApplicationContext(), listOrder, PaymentActivity.this);
        rcvOrder.setAdapter(orderAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                RecyclerView.VERTICAL, false);
        rcvOrder.setLayoutManager(linearLayoutManager);

        double totalPrice = 0;
        int quantity = 0;
        for (int i = 0; i < listOrder.size(); i++) {
            quantity += Integer.parseInt(listOrder.get(i).quantity);
            totalPrice += listOrder.get(i).price;
        }
        tvQuantityItem.setText("Tạm tính(" + quantity + " món)");
        tvTotalPrice.setText(totalPrice + "đ");

        // get fee

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("asset_string");
        double finalTotalPrice = totalPrice;
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Asset> listAsset = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Asset asset = ds.getValue(Asset.class);
                    assert asset != null;
                    listAsset.add(asset);

                }
                double total_price_lv1 = Double.parseDouble(listAsset.get(0).total_price_lv1);
                double total_price_lv2 = Double.parseDouble(listAsset.get(0).total_price_lv2);
                double transport_fee_lv1 = Double.parseDouble(listAsset.get(0).transport_fee_lv1);
                double transport_fee_lv2 = Double.parseDouble(listAsset.get(0).transport_fee_lv2);
                double transport_fee_lv3 = Double.parseDouble(listAsset.get(0).transport_fee_lv3);

                if (finalTotalPrice <= total_price_lv1) {
                    fee = finalTotalPrice / PERCENT * transport_fee_lv1;
                } else if (finalTotalPrice < total_price_lv2) {
                    fee = finalTotalPrice / PERCENT * transport_fee_lv2;
                } else if (finalTotalPrice > total_price_lv2) {
                    fee = finalTotalPrice / PERCENT * transport_fee_lv3;
                }

                tvFee.setText(fee + "đ");
                double oldPrice = finalTotalPrice + fee;
                Bundle data = getIntent().getBundleExtra("data-coupon");
                if (data == null) {
                    lnlCoupon.setVisibility(View.INVISIBLE);
                    tvOldPrice.setVisibility(View.INVISIBLE);
                    tvNewPrice.setText(oldPrice + "đ");
                } else {
                    lnlCoupon.setVisibility(View.VISIBLE);
                    String idCoupon = data.getString("id");
                    double discount = data.getDouble("discount") * finalTotalPrice / PERCENT;
                    tvCoupon.setText(discount + "đ");
                    tvAddCoupon.setText(idCoupon);
                    tvOldPrice.setVisibility(View.VISIBLE);
                    tvOldPrice.setText(oldPrice + "đ");
                    tvNewPrice.setText(oldPrice - discount + "đ");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });

    }

    private void setContext(TextView textView, String paramString) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("asset_value").child(paramString);
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (paramString.equals("service_charge_value")) {
                    service_charge = Double.parseDouble((String) Objects.requireNonNull(snapshot.getValue()));
                }
                if (paramString.equals("waiting_time")) {
                    waiting_time = Integer.parseInt((String) Objects.requireNonNull(snapshot.getValue()));
                }
                valueContext = (String) snapshot.getValue();
                if (textView != null) {
                    textView.setText(valueContext);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(PaymentActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationProvider locationManager = new LocationProvider(PaymentActivity.this);
            locationManager.getLastLocation(new LocationProvider.OnLocationChangedListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(PaymentActivity.this, Locale.getDefault());
                        List<Address> addresses;
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
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);

            // reload ...

        }, 1500);
    }
}