package vungnv.com.foodyum.ui.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.adapter.OrderAdapter;
import vungnv.com.foodyum.adapter.OrderHistoryAdapter;
import vungnv.com.foodyum.model.Order;
import vungnv.com.foodyum.utils.OnBackPressed;

public class OrderWaitingFragment extends Fragment implements Constant, OnBackPressed {
    private RecyclerView rcvListOrderWaiting;
    private OrderHistoryAdapter orderAdapter;
    private final ArrayList<Order> aListOrder = new ArrayList<>();

    private String idUser = "";
    private ArrayList<String> listIDMerchant;
    private ValueEventListener valueEventListener;

    public OrderWaitingFragment() {

    }

    public static OrderWaitingFragment newInstance() {
        return new OrderWaitingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_waiting, container, false);

        init(view);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        idUser = user.getUid();
        getIDMerchant();
        return view;
    }

    private void init(View view) {
        rcvListOrderWaiting = view.findViewById(R.id.rcvListOrderWaiting);
    }

    private void getIDMerchant() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_order");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIDMerchant = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    listIDMerchant.add(childSnapshot.getKey());

                }
                for (int i = 0; i < listIDMerchant.size(); i++) {
                    getListOrder(listIDMerchant.get(i));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.getMessage());
            }
        });
    }

    //    private void getListOrder(String idMerchant) {
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(idMerchant);
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                  aListOrder.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    Order order = ds.getValue(Order.class);
//                    assert order != null;
//                    if (order.status == 1 && order.idUser.equals(idUser)) {
//                         Log.d(TAG, "data: " + order.id + " - idMerchant: " + idMerchant + " - item: " + order.items);
//                         aListOrder.add(order);
//                    }
//                }
//                orderAdapter = new OrderHistoryAdapter(getContext(), aListOrder);
//                rcvListOrderWaiting.setAdapter(orderAdapter);
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
//                rcvListOrderWaiting.setLayoutManager(linearLayoutManager);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d(TAG, "onCancelled: " + error.getMessage());
//            }
//        });
//    }
    private final ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Order order = snapshot.getValue(Order.class);
            if (order != null && order.status == 1 && order.idUser.equals(idUser)) {
                aListOrder.add(order);
                orderAdapter = new OrderHistoryAdapter(getContext(), aListOrder);
                orderAdapter.notifyItemInserted(aListOrder.size() - 1);
                rcvListOrderWaiting.setAdapter(orderAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                rcvListOrderWaiting.setLayoutManager(linearLayoutManager);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Order order = snapshot.getValue(Order.class);
            if (order != null && order.status == 1 && order.idUser.equals(idUser)) {
                int index = getIndexByKey(aListOrder, snapshot.getKey());
                if (index >= 0) {
                    aListOrder.set(index, order);
                    orderAdapter = new OrderHistoryAdapter(getContext(), aListOrder);
                    orderAdapter.notifyItemChanged(index);
                    rcvListOrderWaiting.setAdapter(orderAdapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                    rcvListOrderWaiting.setLayoutManager(linearLayoutManager);
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            int index = getIndexByKey(aListOrder, snapshot.getKey());
            if (index >= 0) {
                aListOrder.remove(index);
                orderAdapter.notifyItemRemoved(index);
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d(TAG, "onCancelled: " + error.getMessage());
        }
    };

    private int getIndexByKey(List<Order> list, String key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id.equals(key)) {
                return i;
            }
        }
        return -1;
    }

    private void getListOrder(String idMerchant) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(idMerchant);
        ref.addChildEventListener(childEventListener);
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference databaseReference = firebaseDatabase.getReference("list_order");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                listIDMerchant = new ArrayList<>();
//                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
//                    listIDMerchant.add(childSnapshot.getKey());
//                }
//                for (int i = 0; i < listIDMerchant.size(); i++) {
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(listIDMerchant.get(i));
//                    valueEventListener = ref.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            List<Order> newOrderList = new ArrayList<>();
//                            for (DataSnapshot ds : snapshot.getChildren()) {
//                                Order order = ds.getValue(Order.class);
//                                assert order != null;
//                                if (order.status == 1 && order.idUser.equals(idUser)) {
//                                    newOrderList.add(order);
//                                }
//                            }
//                            if (newOrderList.isEmpty()) {
//                                Toast.makeText(getContext(), "Hết đơn", Toast.LENGTH_SHORT).show();
//                                if (orderAdapter != null) {
//                                    orderAdapter.updateList(newOrderList);
//                                }
//                            } else {
//                                if (orderAdapter == null) {
//                                    orderAdapter = new OrderHistoryAdapter(getContext(), newOrderList);
//                                    rcvListOrderWaiting.setAdapter(orderAdapter);
//                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
//                                    rcvListOrderWaiting.setLayoutManager(linearLayoutManager);
//                                } else {
//                                    orderAdapter.updateList(newOrderList);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Log.d(TAG, "onCancelled: " + error.getMessage());
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d(TAG, error.getMessage());
//            }
//        });
//
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (valueEventListener != null) {
//            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//            DatabaseReference databaseReference = firebaseDatabase.getReference("list_order");
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    listIDMerchant = new ArrayList<>();
//                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
//                        listIDMerchant.add(childSnapshot.getKey());
//
//                    }
//                    for (int i = 0; i < listIDMerchant.size(); i++) {
//                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(listIDMerchant.get(i));
//                        ref.removeEventListener(valueEventListener);
//                    }
//
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.d(TAG, error.getMessage());
//                }
//            });
//        }
//    }

    @Override
    public void onBackPressed() {
        requireActivity().onBackPressed();
    }
}