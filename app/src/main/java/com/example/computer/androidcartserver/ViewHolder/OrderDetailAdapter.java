package com.example.computer.androidcartserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.computer.androidcartserver.Model.Order;
import com.example.computer.androidcartserver.R;

import java.util.List;

class MyviewHolder extends RecyclerView.ViewHolder{
   public TextView name,price,discount,quantity;
    public MyviewHolder(@NonNull View itemView) {
        super(itemView);
        name = (TextView)itemView.findViewById(R.id.product_name);
        price = (TextView)itemView.findViewById(R.id.product_price);
        discount = (TextView)itemView.findViewById(R.id.product_discount);
        quantity = (TextView)itemView.findViewById(R.id.product_quantity);

    }
}
public class OrderDetailAdapter extends RecyclerView.Adapter<MyviewHolder>{
    List<Order> myOrders;
    public OrderDetailAdapter(List<Order> myOrders){
        this.myOrders = myOrders;
    }
    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_details_layout,viewGroup,false);
        return new MyviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder myviewHolder, int position) {
    Order order = myOrders.get(position);
    myviewHolder.name.setText(String.format("Name:%s",order.getProductName()));
        myviewHolder.quantity.setText(String.format("Quantity:%s",order.getQuantity()));
        myviewHolder.discount.setText(String.format("Discount:%s",order.getDiscount()));
        myviewHolder.price.setText(String.format("Price:%s",order.getPrice()));

    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}