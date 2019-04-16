package com.example.computer.androidcartserver.Remote;

import com.example.computer.androidcartserver.Model.DataMessage;
import com.example.computer.androidcartserver.Model.MyResponse;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAArp6VK8s:APA91bGKvrntcVeOO7vk00EBUBwwUIMkJshNXCw0hPofgrqyGsQvNCrY1JmhEy_dhO8rjfmsHZ5vvVy1PwlYOio6d3Xh-HP16fKHQeatx9mbANzD9oDeVs-67a9Fcxn1jSD-v6tTkXuDEw9M5FEWC2Mn7xN48gmYjQ"
            }
    )
    @POST("fcm/send")
    retrofit2.Call<MyResponse> sendNotification(@Body DataMessage body);
}
