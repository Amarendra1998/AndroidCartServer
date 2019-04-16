package com.example.computer.androidcartserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.computer.androidcartserver.Common.Common;
import com.example.computer.androidcartserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {
     EditText edtpassword,edtphone;
     Button btnsign;
     FirebaseDatabase db;
     DatabaseReference user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        edtphone = (EditText)findViewById(R.id.editphone);
        edtpassword = (EditText)findViewById(R.id.editpass);
        btnsign = (Button)findViewById(R.id.btnsignin);
        db = FirebaseDatabase.getInstance();
        user= db.getReference("User");
        btnsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(edtphone.getText().toString(),edtpassword.getText().toString());
            }
        });
    }

    private void signInUser(String phone, String password) {
      final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
      mDialog.setMessage("Please waiting....");
      mDialog.show();

      final String localPhone = phone;
      final String localPassword =password;
      user.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (dataSnapshot.child(localPhone).exists()) {
                  mDialog.dismiss();
                  User user = dataSnapshot.child(localPhone).getValue(User.class);
                  user.setPhone(localPhone);
                  if (Boolean.parseBoolean(user.getIsStaff()))//if IsStaff is true
                  {
                      if (user.getPassword().equals(localPassword)){
                          Intent login = new Intent(SignIn.this,Home.class);
                          Common.currentUser = user;
                          startActivity(login);
                          finish();
                      }else {
                          Toast.makeText(SignIn.this,"Wrong password..",Toast.LENGTH_SHORT).show();
                      }
                  }else {
                      Toast.makeText(SignIn.this,"Please login with staff account..",Toast.LENGTH_SHORT).show();
                  }
              }else {
                  mDialog.dismiss();
                  Toast.makeText(SignIn.this,"User does not exist in database",Toast.LENGTH_SHORT).show();

              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }
}
