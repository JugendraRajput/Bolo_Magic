package bolomagic.in.SendNotificationPack;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseIdService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        FirebaseDatabase.getInstance().getReference().child("SPL").child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Personal Information").child("token").setValue(mToken);
    }
}