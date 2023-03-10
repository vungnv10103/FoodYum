package vungnv.com.foodyum.ui.order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.NonNull;
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
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.adapter.OrderCancelAdapter;
import vungnv.com.foodyum.model.Order;

public class OrderCancelFragment extends Fragment implements Constant {

    private OrderCancelAdapter orderAdapter;
    private RecyclerView rcvListOrderCancel;

    public OrderCancelFragment() {

    }

    public static OrderCancelFragment newInstance() {
        return new OrderCancelFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_cancel, container, false);

        init(view);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String idUser = user.getUid();
        getListOrder(idUser);

        return view;
    }

    private void init(View view) {
        rcvListOrderCancel = view.findViewById(R.id.rcvListOrderCancel);
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
                    if (order.status == 0) {
                        listOrder.add(order);
                    }
                }
                Collections.reverse(listOrder);
                orderAdapter = new OrderCancelAdapter(getContext(), listOrder);
                rcvListOrderCancel.setAdapter(orderAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                rcvListOrderCancel.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });

    }

}