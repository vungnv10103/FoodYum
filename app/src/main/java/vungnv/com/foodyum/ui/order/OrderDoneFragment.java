package vungnv.com.foodyum.ui.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.OrderHistoryDAO;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.adapter.OrderDoneAdapter;
import vungnv.com.foodyum.model.Order;

public class OrderDoneFragment extends Fragment implements Constant {
    private RecyclerView rcvListOrderDone;
    private OrderDoneAdapter orderAdapter;


    public OrderDoneFragment() {

    }

    public static OrderDoneFragment newInstance() {
        return new OrderDoneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_order_done, container, false);

        init(view);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String idUser = user.getUid();
        getListOrder(idUser);
        return view;

    }
    private void init(View view) {
        rcvListOrderDone = view.findViewById(R.id.rcvListOrderDone);
    }
    private void getListOrder(String idUser) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order_by_idUserClient").child(idUser);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> listOrder = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Order order = ds.getValue(Order.class);
                    assert order != null;
                    if (order.status == 2) {
                        listOrder.add(order);
                    }
                }
                Collections.reverse(listOrder);

                orderAdapter = new OrderDoneAdapter(getContext(), listOrder);
                rcvListOrderDone.setAdapter(orderAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                rcvListOrderDone.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });

    }

}