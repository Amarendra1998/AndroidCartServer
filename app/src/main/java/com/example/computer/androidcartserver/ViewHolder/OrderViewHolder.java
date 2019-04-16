package com.example.computer.androidcartserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.computer.androidcartserver.Interface.ItemClickListener;
import com.example.computer.androidcartserver.R;

import java.util.Objects;

import static java.util.Objects.*;

public class OrderViewHolder extends RecyclerView.ViewHolder  {
    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,txtOrderDate;
    public Button btnEdit,btnRemove,btnDetail,btnDirection;
    private ItemClickListener itemClickListener;
    public OrderViewHolder(@NonNull View itemView) {
        super((itemView));
        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_date);
        btnEdit = (Button)itemView.findViewById(R.id.btnedt);
        btnRemove = (Button)itemView.findViewById(R.id.btnremove);
        btnDirection = (Button)itemView.findViewById(R.id.btndirection);
        btnDetail = (Button)itemView.findViewById(R.id.btndetail);

    }

}
