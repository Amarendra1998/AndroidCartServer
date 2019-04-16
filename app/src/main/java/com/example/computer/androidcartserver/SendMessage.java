package com.example.computer.androidcartserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.computer.androidcartserver.Common.Common;
import com.example.computer.androidcartserver.Model.DataMessage;
import com.example.computer.androidcartserver.Model.MyResponse;
import com.example.computer.androidcartserver.Model.Token;
import com.example.computer.androidcartserver.Remote.APIService;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessage extends AppCompatActivity {
MaterialEditText edtMessage,edtTitle;
Button btnsend;
APIService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        mService = Common.getFCMClient();
        edtMessage = (MaterialEditText)findViewById(R.id.editmessage);
        edtTitle = (MaterialEditText)findViewById(R.id.edittitle);
        btnsend = (Button)findViewById(R.id.btnSend);
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Notification notification = new Notification(edtTitle.getText().toString(),edtMessage.getText().toString());
                //Sender toTopic = new Sender();
                //toTopic.to = new StringBuilder("/topic/").append(Common.topicName).toString();
               // toTopic.notification = notification;
                Map<String,String> datasend = new HashMap<>();
                datasend.put("title",edtTitle.getText().toString());
                datasend.put("message",edtMessage.getText().toString());
                DataMessage dataMessage = new DataMessage(datasend);
                mService.sendNotification(dataMessage)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.isSuccessful())
                                {

                                    Toast.makeText(SendMessage.this,"Message sent",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(SendMessage.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
