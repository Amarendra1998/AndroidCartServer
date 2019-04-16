package com.example.computer.androidcartserver;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
private Button btnsignin,btnsignup;
private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnsignin = (Button)findViewById(R.id.btnsignin);
        btnsignup = (Button)findViewById(R.id.btnsignup);
        textView = (TextView)findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/Pozotwo.ttf");
        textView.setTypeface(face);
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mine  = new Intent(MainActivity.this,SignIn.class);
                startActivity(mine);
            }
        });
    }
}
