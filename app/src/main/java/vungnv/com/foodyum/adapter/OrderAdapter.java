package vungnv.com.foodyum.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vungnv.com.foodyum.Constant;
import vungnv.com.foodyum.DAO.ItemCartDAO;
import vungnv.com.foodyum.DAO.UsersDAO;
import vungnv.com.foodyum.R;
import vungnv.com.foodyum.activities.PaymentActivity;
import vungnv.com.foodyum.model.ItemCart;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> implements Constant {
    private final Context context;
    @SuppressLint("StaticFieldLeak")
    private static PaymentActivity payment;
    private static List<ItemCart> listItem;
    private ItemCartDAO itemCartDAO;


    public OrderAdapter(Context context, List<ItemCart> listItem, PaymentActivity paymentActivity) {
        this.context = context;
        payment = paymentActivity;
        OrderAdapter.listItem = listItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        itemCartDAO = new ItemCartDAO(context);
        ItemCart item = listItem.get(position);

        int status = item.status;
        holder.cbCheck.setChecked(status != 1);

        holder.tvName.setText(item.name);
        holder.tvPrice.setText(item.price + "đ");
        holder.tvQuantity.setText(item.quantity + "x");
        holder.cbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "" + item.id, Toast.LENGTH_SHORT).show();
                boolean isSelected = holder.cbCheck.isChecked();
                ItemCart item1 = new ItemCart();
                item1.stt = item.stt;
                if (isSelected) {
                    item1.status = 2;
                    if (itemCartDAO.updateStatusWithStt(item1) > 0) {
                        Log.d(TAG, "update status 1 -> 2 success");
                    } else {
                        Log.d(TAG, "error update status");
                    }
                } else {
                    item1.status = 1;
                    if (itemCartDAO.updateStatusWithStt(item1) > 0) {
                        Log.d(TAG, "update status 2 -> 1 success");
                    } else {
                        Log.d(TAG, "error update status");
                    }
                }
                payment.getListItemInOrder();
            }

        });
    }


    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        TextView tvQuantity;
        CheckBox cbCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvName = itemView.findViewById(R.id.tvNameProduct);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            cbCheck = itemView.findViewById(R.id.cbSelected);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(v.getContext(), "status: " + listItem.get(getAdapterPosition()).status, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
