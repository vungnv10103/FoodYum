package vungnv.com.foodyum.ui.cart;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.adapter.CartAdapter;
import vungnv.com.foodyum.databinding.FragmentCartBinding;
import vungnv.com.foodyum.model.ItemCart;
import vungnv.com.foodyum.utils.OnBackPressed;


public class CartFragment extends Fragment implements Constant, OnBackPressed {

    private Toolbar toolbar;
    private Button btnCheckout;
    private RecyclerView rcv_cart;
    public String date;

    private ItemCartDAO itemCartDAO;
    private List<ItemCart> listCart;

    private boolean isReady = false;
    private final long delay = 1000;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        vungnv.com.foodyum.databinding.FragmentCartBinding binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init(root);

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
        listCart();

        refresh();

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return root;
    }

    private void init(View view) {
        toolbar = view.findViewById(R.id.toolBarCart);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        rcv_cart = view.findViewById(R.id.rcv_cart);
        itemCartDAO = new ItemCartDAO(getContext());
    }

    private void listCart() {
        listCart = itemCartDAO.getALLDefault();
        if (listCart.size() == 0) {
            Toast.makeText(getContext(), CART_EMPTY, Toast.LENGTH_SHORT).show();
            return;
        }
        CartAdapter cartAdapter = new CartAdapter(getContext(), listCart);
        rcv_cart.setAdapter(cartAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcv_cart.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcv_cart.getContext(),
                linearLayoutManager.getOrientation());
        rcv_cart.addItemDecoration(dividerItemDecoration);
        rcv_cart.setHasFixedSize(true);
        rcv_cart.setNestedScrollingEnabled(false);
        isReady = true;


    }

    @SuppressLint("SetTextI18n")
    public void payment() {
        listCart = itemCartDAO.getALL(2);
        btnCheckout.setText(listCart.size() + "Thanh toán");
    }


    public void refresh() {
        Thread t1 = new Thread() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                while (true) {
                    listCart = itemCartDAO.getALL(2);
                    double totalPrice = 0;
                    int totalQuantity = 0;
                    for (int i = 0; i < listCart.size(); i++) {
                        totalQuantity += listCart.get(i).quantity;
                        totalPrice += listCart.get(i).price;
                    }
                    final String resultButton = totalQuantity + " Món" + "  Trang thanh toán" + "  " + totalPrice + "đ";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            btnCheckout.setText(resultButton);
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


    @Override
    public void onBackPressed() {
        if (isReady) {
            requireActivity().onBackPressed();
        }
    }
}