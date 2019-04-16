package com.example.computer.androidcartserver.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;

import com.example.computer.androidcartserver.Model.Request;
import com.example.computer.androidcartserver.Model.User;
import com.example.computer.androidcartserver.Remote.APIService;
import com.example.computer.androidcartserver.Remote.FCMRetrofitClient;
import com.example.computer.androidcartserver.Remote.GeoCoordinates;
import com.example.computer.androidcartserver.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Retrofit;

public class Common {
public static User currentUser;
    public static String PHONE_TEXT = "userPhone";
    public static Request currentRequest;
    public static String topicName = "NEWS";
    public static final String Update = "Update";
    public static final String Delete = "Delete";
    public static final int PICK_IMAGE_RIQUEST = 71;
    public static final String baseUrl = "https://maps.googleapis.com";
    public static final String fcmUrl = "https://fcm.googleapis.com/";
    public static String convertCodeToStatus(String code)
    {
        if (code.equals("0"))
            return "Placed";
        else if (code.equals("1"))
            return "On My Way";
        else
            return "Shipped";
    }
    public static GeoCoordinates getGeoCodeServices(){
        return FCMRetrofitClient.getClient(baseUrl).create(GeoCoordinates.class);
    }
    public static APIService getFCMClient(){
        return RetrofitClient.getClient(fcmUrl).create(APIService.class);
    }

    public static Bitmap scaleBitMap(Bitmap bitmap,int newWidth,int newHeight)
    {
        Bitmap scaleBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);
        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas= new Canvas(scaleBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaleBitmap;
    }
    public static String getDate(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(DateFormat.format("dd-MM-yyyy HH:mm",calendar).toString());
        return date.toString();
    }
}
