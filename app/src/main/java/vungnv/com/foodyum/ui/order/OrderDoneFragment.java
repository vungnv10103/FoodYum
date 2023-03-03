package vungnv.com.foodyum.ui.order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vungnv.com.foodyum.R;

public class OrderDoneFragment extends Fragment {


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
        return inflater.inflate(R.layout.fragment_order_done, container, false);
    }
}