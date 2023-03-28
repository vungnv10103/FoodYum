package vungnv.com.foodyum.ui.cart;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;


import dmax.dialog.SpotsDialog;
import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.activities.PaymentActivity;
import vungnv.com.foodyum.adapter.CartAdapter;
import vungnv.com.foodyum.databinding.FragmentCartBinding;
import vungnv.com.foodyum.model.ItemCart;

import vungnv.com.foodyum.utils.OnBackPressed;


public class CartFragment extends Fragment implements Constant, OnBackPressed {

    private Toolbar toolbar;
    private ImageButton imgOrderHistory;
    private Button btnCheckout;
    private RecyclerView rcv_cart;
    public String date;

    private ItemCartDAO itemCartDAO;
    private List<ItemCart> listCart;

    private boolean isReady = false;
    private final long delay = 1000;
    private SpotsDialog processDialog;
    private int temp = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                            ViewGroup container, Bundle savedInstanceState) {


        vungnv.com.foodyum.databinding.FragmentCartBinding binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init(root);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        showListInCart();
        refreshButton();

        imgOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "updating...", Toast.LENGTH_SHORT).show();
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = btnCheckout.getText().toString().trim();
                if (result.equals("Thanh Toán")) {
                    return;
                }
                int quantity = Integer.parseInt(result.substring(0, result.indexOf(" ")));

                if (quantity == 0) {
                    Toast.makeText(getContext(), NO_CHOOSE, Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(getContext(), PaymentActivity.class));
//                requireActivity().finishAffinity();
            }
        });

        return root;
    }

    private void init(View view) {
        toolbar = view.findViewById(R.id.toolBarCart);
        imgOrderHistory = view.findViewById(R.id.imgOrderHistory);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        rcv_cart = view.findViewById(R.id.rcv_cart);
        itemCartDAO = new ItemCartDAO(getContext());
        processDialog = new SpotsDialog(getContext(), R.style.Custom2);
    }

    public void showListInCart() {
        processDialog.show();
        String idUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        listCart = itemCartDAO.getALL(idUser, 1, 2);
        if (listCart.size() == 0) {
            isReady = true;
            CartAdapter cartAdapter = new CartAdapter(getContext(), listCart, this);
            rcv_cart.setAdapter(cartAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            rcv_cart.setLayoutManager(linearLayoutManager);
            if (temp == 0) {
                Toast.makeText(getContext(), CART_EMPTY, Toast.LENGTH_SHORT).show();
            }
            temp++;
        }
        CartAdapter cartAdapter = new CartAdapter(getContext(), listCart, this);
        rcv_cart.setAdapter(cartAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        rcv_cart.setLayoutManager(linearLayoutManager);
        isReady = true;

        processDialog.dismiss();

    }

    public void refreshButton() {
        processDialog.show();
        String idUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        listCart = itemCartDAO.getALL(idUser, 2);
        double totalPrice = 0;
        int totalQuantity = 0;

        for (int i = 0; i < listCart.size(); i++) {
            totalQuantity += Integer.parseInt(listCart.get(i).quantity);
            totalPrice += listCart.get(i).price;
        }
        final String resultButton = totalQuantity + " Món" + "  Trang thanh toán" + "  " + totalPrice + "đ";
        btnCheckout.setText(resultButton);
        isReady = true;
        processDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (isReady) {
            requireActivity().onBackPressed();
        }
    }

}