package vungnv.com.foodyum.ui.cart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.databinding.FragmentCartBinding;
import vungnv.com.foodyum.model.ItemCart;


public class CartFragment extends Fragment implements Constant {

    private FragmentCartBinding binding;

    private Toolbar toolbar;
    private Button btnCheckout;
    private RecyclerView rcv_cart;
    public String date;

    private ItemCartDAO itemCartDAO;
    private List<ItemCart> listCart;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init(root);

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        listCart();


        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
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
        for (int i = 0; i < listCart.size(); i++) {
            Log.d(TAG, "listCart: " + listCart.get(i).name);
        }
        //productsAdapter = new ProductsAdapter(ShowListProductDetailActivity.this, aListProducts);
        // rcv_cart.setAdapter(productsAdapter);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
//        rcv_cart.setLayoutManager(linearLayoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcv_cart.getContext(),
//                linearLayoutManager.getOrientation());
//        rcv_cart.addItemDecoration(dividerItemDecoration);
//        rcv_cart.setHasFixedSize(true);
//        rcv_cart.setNestedScrollingEnabled(false);
    }


}