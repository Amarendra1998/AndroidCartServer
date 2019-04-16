package com.example.computer.androidcartserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.computer.androidcartserver.Common.Common;
import com.example.computer.androidcartserver.Model.DataMessage;
import com.example.computer.androidcartserver.Model.MyResponse;
import com.example.computer.androidcartserver.Model.Request;
import com.example.computer.androidcartserver.Model.Token;
import com.example.computer.androidcartserver.Remote.APIService;
import com.example.computer.androidcartserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {
     RecyclerView recyclerView;
     RecyclerView.LayoutManager layoutManager;
     FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;
     FirebaseDatabase db;
     DatabaseReference requests;
     MaterialSpinner spinner;
     APIService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        mService = Common.getFCMClient();
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");

        recyclerView = (RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        loadOrders();
    }

    private void loadOrders() {
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requests,Request.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull final Request model) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialoge(adapter.getRef(position).getKey(),adapter.getItem(position));

                    }
                });

                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(position).getKey(),adapter.getItem(position));

                    }
                });

                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trackingorder = new Intent(OrderStatus.this,OrderDetail.class);
                        Common.currentRequest = model;
                        trackingorder.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(trackingorder);
                    }
                });

                viewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trackingorder = new Intent(OrderStatus.this,TrackingOrder.class);
                        Common.currentRequest = model;
                        startActivity(trackingorder);
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemview = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.order_layout,viewGroup,false);

                return new OrderViewHolder(itemview);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        /*
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, final int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());

                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialoge(adapter.getRef(position).getKey(),adapter.getItem(position));

                    }
                });

                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(position).getKey(),adapter.getItem(position));

                    }
                });

                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trackingorder = new Intent(OrderStatus.this,OrderDetail.class);
                        Common.currentRequest = model;
                        trackingorder.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(trackingorder);
                    }
                });

                viewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trackingorder = new Intent(OrderStatus.this,TrackingOrder.class);
                        Common.currentRequest = model;
                        startActivity(trackingorder);
                    }
                });

            }
        };*/

    }

    /*@Override
   public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.Update))
            showUpdateDialoge(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        else if (item.getTitle().equals(Common.Delete))
            deleteOrder(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        return super.onContextItemSelected(item);
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void deleteOrder(String key, Request item) {
    requests.child(key).removeValue();
    adapter.notifyDataSetChanged();
        //
    }

    private void showUpdateDialoge(String key, final Request item) {
        //
        final AlertDialog.Builder alertdialoge = new AlertDialog.Builder(OrderStatus.this);
        alertdialoge.setTitle("Update Order");
        alertdialoge.setMessage("Please choose status");
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout,null);
        spinner =  (MaterialSpinner) view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","On My Way","Shipped");
        alertdialoge.setView(view);
        final String localkey = key;
        alertdialoge.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                  item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                  requests.child(localkey).setValue(item);
                  adapter.notifyDataSetChanged();
                  sendOrderStatusToUser(localkey,item);
            }
        });
        alertdialoge.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
            }
        });
        alertdialoge.show();
    }

    private void sendOrderStatusToUser(final String key, final Request item) {
          DatabaseReference tokens = db.getReference("Tokens");
          tokens.orderByKey().equalTo(item.getPhone())
                  .addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                          {
                              Token token = postSnapshot.getValue(Token.class);
                             // Notification notification = new Notification("Free Eeze","Your order"+key+"was updated");
                             // Sender sender = new Sender(token.getToken(),notification);
                              Map<String,String> datasend = new HashMap<>();
                              datasend.put("title","FoodCart");
                              datasend.put("message","Your order"+key+"was updated");
                              DataMessage dataMessage = new DataMessage(token.getToken(),datasend);
                              mService.sendNotification(dataMessage)
                                      .enqueue(new Callback<MyResponse>() {
                                          @Override
                                          public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                              if (response.body().success==1)
                                              {
                                                  Toast.makeText(OrderStatus.this,"Order was updated",Toast.LENGTH_SHORT).show();
                                              }
                                              else
                                              {
                                                  Toast.makeText(OrderStatus.this,"Order was updated but failed to send notification",Toast.LENGTH_SHORT).show();
                                              }
                                          }

                                          @Override
                                          public void onFailure(Call<MyResponse> call, Throwable t) {
                                              Log.e("ERROR",t.getMessage());
                                          }
                                      });
                          }
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });
    }
}
