package vungnv.com.foodyum.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.adapter.CouponAdapter;

import vungnv.com.foodyum.model.Coupon;

public class CouponActivity extends AppCompatActivity {
    private ImageButton imgBack;
    private TextInputEditText edIdCoupon;
    private Button btnApply;
    private RecyclerView rcv_listCoupon;

    private List<Coupon> listCoupon;
    private final ArrayList<Coupon> aListCoupon = new ArrayList<>();
    ;
    private CouponAdapter couponAdapter;

    private final long delay = 1000;
    private SpotsDialog processDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        init();

        String idMerchantTemp = "K7rJ9YbiB0crBf7PfJ9gy2f8kwn1"; // hard code
        getListCouponByIdMerchant(idMerchantTemp);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        edIdCoupon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
//                    if (aListCoupon.size() == 0) {
//                        return;
//                    }


                    btnApply.setBackgroundColor(btnApply.getContext().getResources().getColor(R.color.teal2));
                    btnApply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(CouponActivity.this, "apply...", Toast.LENGTH_SHORT).show();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            //Find the currently focused view, so we can grab the correct window token from it.
                            View view = getCurrentFocus();
                            //If no view currently has focus, create a new one, just so we can grab a window token from it
                            if (view == null) {
                                view = new View(CouponActivity.this);
                            }
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    });
                } else {
                    btnApply.setBackgroundColor(btnApply.getContext().getResources().getColor(R.color.ice_blue));
                    btnApply.setOnClickListener(null);
                }

                couponAdapter.getFilter().filter(s.toString());

            }
        });


    }

    private void init() {
        imgBack = findViewById(R.id.imgBack);
        edIdCoupon = findViewById(R.id.edIdCoupon);
        btnApply = findViewById(R.id.btnApply);
        rcv_listCoupon = findViewById(R.id.rcv_listCoupon);
        processDialog = new SpotsDialog(CouponActivity.this, R.style.Custom2);

    }

    private void getListCouponByIdMerchant(String idMerchant) {
        processDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_coupon").child(idMerchant);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aListCoupon.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Coupon cp = ds.getValue(Coupon.class);
                    aListCoupon.add(cp);
                }
                if (aListCoupon.size() == 0) {
                    Toast.makeText(CouponActivity.this, "Hiện không có mã giảm giá nào !", Toast.LENGTH_SHORT).show();
                    processDialog.dismiss();
                    return;
                }
                couponAdapter = new CouponAdapter(CouponActivity.this, aListCoupon);
                rcv_listCoupon.setAdapter(couponAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CouponActivity.this, RecyclerView.VERTICAL, false);
                rcv_listCoupon.setLayoutManager(linearLayoutManager);
                processDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                processDialog.dismiss();
            }
        });
    }
//    private void refreshList(String idUser) {
//        Thread t1 = new Thread() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void run() {
//
//                while (true) {
//
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            listOrder = itemCartDAO.getALL(idUser, 2);
//                            if (listOrder.size() == 0) {
//                                OrderAdapter orderAdapter = new OrderAdapter(getApplicationContext(), listOrder);
//                                rcv_listCoupon.setAdapter(orderAdapter);
//                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
//                                rcv_listCoupon.setLayoutManager(linearLayoutManager);
//
//                                return;
//                            }
//                            OrderAdapter orderAdapter = new OrderAdapter(getApplicationContext(), listOrder);
//                            rcv_listCoupon.setAdapter(orderAdapter);
//                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
//                            rcv_listCoupon.setLayoutManager(linearLayoutManager);
//
//
//                        }
//                    });
//
//
//                    try {
//                        Thread.sleep(delay);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        t1.start();
//    }
}