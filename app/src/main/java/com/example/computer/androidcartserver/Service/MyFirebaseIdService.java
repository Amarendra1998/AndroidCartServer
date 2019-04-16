package com.example.computer.androidcartserver.Service;

import com.example.computer.androidcartserver.Common.Common;
import com.example.computer.androidcartserver.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        updateToServer(refreshToken);
    }

    private void updateToServer(String refreshToken) {
        if (Common.currentUser!=null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");
            Token token = new Token(refreshToken, true);
            tokens.child(Common.currentUser.getPhone()).setValue(token);
        }
    }
}
