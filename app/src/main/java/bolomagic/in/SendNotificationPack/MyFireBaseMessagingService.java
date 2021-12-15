package bolomagic.in.SendNotificationPack;

import android.app.Notification;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import bolomagic.in.NotificationUtils;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    String title, message;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        title = remoteMessage.getData().get("Title");
        message = remoteMessage.getData().get("Message");
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(message)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationUtils mNotificationUtils = new NotificationUtils(this);
                Notification.Builder nb = mNotificationUtils.getAndroidChannelNotification(title, message);
                mNotificationUtils.getManager().notify(101, nb.build());
            } else {
                Toast.makeText(this, title + "\n" + message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            FirebaseDatabase.getInstance().getReference().child("SPL").child("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Personal Information").child("token").setValue(mToken);
        }
    }
}