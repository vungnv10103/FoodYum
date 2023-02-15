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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


import dmax.dialog.SpotsDialog;
import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.DAO.OrderDAO;
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

    private OrderDAO orderDAO;
    private ItemCartDAO itemCartDAO;
    private List<ItemCart> listCart;

    private boolean isReady = false;
    private final long delay = 1000;
    private SpotsDialog processDialog;

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
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String idUser = auth.getUid();

        // listCart(idUser);
        refresh(idUser);
        refreshButton(idUser);

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

                // Log.d(TAG, "onClick: "+ quantity);

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
        orderDAO = new OrderDAO(getContext());
        processDialog = new SpotsDialog(getContext(), R.style.Custom2);
    }


    private void listCart(String idUser) {
        listCart = itemCartDAO.getALL(idUser);
        if (listCart.size() == 0) {
            isReady = true;
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

    public void refreshButton(String idUser) {
        Thread t1 = new Thread() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                while (true) {
                    listCart = itemCartDAO.getALL(idUser, 2);
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
                            isReady = true;
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

    public void refresh(String idUser) {
        Thread t1 = new Thread() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                while (true) {
                    final int[] temp = {0};
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listCart = itemCartDAO.getALL(idUser, 1, 2);
                            if (listCart.size() == 0) {
                                isReady = true;
                                CartAdapter cartAdapter = new CartAdapter(getContext(), listCart);
                                rcv_cart.setAdapter(cartAdapter);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                rcv_cart.setLayoutManager(linearLayoutManager);
                                if (temp[0] == 0) {
                                    Toast.makeText(getContext(), CART_EMPTY, Toast.LENGTH_SHORT).show();
                                }
                                temp[0]++;


                                return;
                            }
                            CartAdapter cartAdapter = new CartAdapter(getContext(), listCart);
                            rcv_cart.setAdapter(cartAdapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                            rcv_cart.setLayoutManager(linearLayoutManager);
                            isReady = true;
                        }
                    });
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (temp[0] > 0) {
                        break;
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