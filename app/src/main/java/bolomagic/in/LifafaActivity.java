package bolomagic.in;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class LifafaActivity extends AppCompatActivity {

    String UID;
    TextView lifafaWalletTextView;
    double walletAmount = 0.0;
    String lifafaID = "DEFAULT";
    ImageView lifafaLoadingImageView,lifafaExpiredImageView;
    TextView lifafaFailedMessageTextView;
    ConstraintLayout lifafaResultLayout;
    final int[] x = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifafa);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            UID = mFirebaseUser.getUid();
        }

        lifafaWalletTextView = findViewById(R.id.lifafaWalletTextView);

        lifafaID = getIntent().getStringExtra("Lifafa ID");
        if (!lifafaID.equals("DEFAULT")){
            findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HideReceivingLayout();
                }
            });
            findViewById(R.id.lifafaWalletLayout).setVisibility(View.GONE);
            findViewById(R.id.lifafaReceivingLayout).setVisibility(View.VISIBLE);

            lifafaLoadingImageView = findViewById(R.id.imageView14);
            lifafaExpiredImageView = findViewById(R.id.imageView15);
            lifafaFailedMessageTextView = findViewById(R.id.textView53);
            lifafaResultLayout = findViewById(R.id.lifafaResultLayout);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (x[0] == 0){
                        lifafaLoadingImageView.animate().translationY(-150).setDuration(1000).start();
                        x[0] = 1;
                    }
                    if(x[0] == 1){
                        lifafaLoadingImageView.animate().translationY(0).setDuration(1000).start();
                        x[0] = 0;
                    }
                    handler.postDelayed(this, 2000);
                }
            }, 2000);
            GetLifafa();
        }

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SPL/Users/"+UID+"/Personal Information/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Lifafa")){
                    walletAmount = Double.parseDouble(snapshot.child("Lifafa").child("Wallet Amount").getValue().toString());
                }else {
                    databaseReference.child("Lifafa").child("Wallet Amount").setValue(0);
                }
                lifafaWalletTextView.setText(String.valueOf(walletAmount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ShowToast(error.toString());
            }
        });

        findViewById(R.id.lifafaWalletRedeemButton).setOnClickListener(view -> {
            Intent intent = new Intent(LifafaActivity.this, WithdrawActivity.class);
            intent.putExtra("Withdraw Type","Lifafa");
            startActivity(intent);
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lifafa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addMoney:
                Intent intent = new Intent(LifafaActivity.this, PaymentActivity.class);
                intent.putExtra("Wallet Type","Lifafa");
                startActivity(intent);
                return true;
            case R.id.action_create:
                startActivity(new Intent(LifafaActivity.this,CreateLifafaActivity.class));
                return true;
            case R.id.action_created:
                startActivity(new Intent(LifafaActivity.this,LifafaCreatedActivity.class));
                return true;
            case R.id.action_received:
                startActivity(new Intent(LifafaActivity.this,LifafaReceivedActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ShowToast(String errorMessage){
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void HideReceivingLayout(){
        findViewById(R.id.lifafaWalletLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.lifafaReceivingLayout).setVisibility(View.GONE);
    }

    public void GetLifafa(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Lifafa");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(lifafaID)){
                    if (snapshot.child(lifafaID).child("Sender ID").getValue().toString().equals(UID)){
                        //You can't claim this Lifafa !
                        lifafaExpiredImageView.setVisibility(View.VISIBLE);
                        lifafaFailedMessageTextView.setVisibility(View.VISIBLE);
                        lifafaFailedMessageTextView.setText("You can't claim your own lifafa :)");
                        x[0] = 2;
                        lifafaLoadingImageView.animate().translationY(0).setDuration(0).start();
                        findViewById(R.id.button5).setVisibility(View.VISIBLE);
                    }else {
                        String type = snapshot.child(lifafaID).child("Receiver Type").getValue().toString();
                        if (type.equals("FRIEND")){
                            if (snapshot.child(lifafaID).child("Receiver ID").getValue().toString().equals(UID)){
                                if (snapshot.child(lifafaID).hasChild("Received By")){
                                    if (snapshot.child(lifafaID).child("Received By").hasChild(UID)){
                                        //Already lifafa Received...!
                                        lifafaExpiredImageView.setVisibility(View.VISIBLE);
                                        lifafaFailedMessageTextView.setVisibility(View.VISIBLE);
                                        lifafaFailedMessageTextView.setText("You have already received this lifafa :)");
                                        x[0] = 2;
                                        lifafaLoadingImageView.animate().translationY(0).setDuration(0).start();
                                        findViewById(R.id.button5).setVisibility(View.VISIBLE);
                                    }else {
                                        //Receiving Process Started
                                        lifafaLoadingImageView.animate().translationY(-500).scaleY(3).scaleX(3)
                                                .setInterpolator(new AccelerateDecelerateInterpolator()).alpha(0).setDuration(500).start();
                                        lifafaResultLayout.animate().translationY(0).scaleY(1).scaleX(1)
                                                .setInterpolator(new AccelerateDecelerateInterpolator()).alpha(1).setDuration(500).start();
                                        LifafaReceivingProcess(snapshot,lifafaID,0,0);
                                    }
                                }else {
                                    //Receiving Process Started
                                    lifafaLoadingImageView.animate().translationY(-500).scaleY(3).scaleX(3)
                                            .setInterpolator(new AccelerateDecelerateInterpolator()).alpha(0).setDuration(500).start();
                                    lifafaResultLayout.animate().translationY(0).scaleY(1).scaleX(1)
                                            .setInterpolator(new AccelerateDecelerateInterpolator()).alpha(1).setDuration(500).start();
                                    LifafaReceivingProcess(snapshot,lifafaID,0,0);
                                }
                            }else {
                                //Private Lifafa ID, Can't be access
                                lifafaExpiredImageView.setVisibility(View.VISIBLE);
                                lifafaFailedMessageTextView.setVisibility(View.VISIBLE);
                                lifafaFailedMessageTextView.setText("This is a private lifafa, You are not eligible to access this lifafa :)");
                                x[0] = 2;
                                lifafaLoadingImageView.animate().translationY(0).setDuration(0).start();
                                findViewById(R.id.button5).setVisibility(View.VISIBLE);
                            }
                        }

                        if (type.equals("GROUP")){
                            if (snapshot.child(lifafaID).hasChild("Received By")){
                                if (snapshot.child(lifafaID).child("Received By").hasChild(UID)){
                                    //Already lifafa Received...!
                                    lifafaExpiredImageView.setVisibility(View.VISIBLE);
                                    lifafaFailedMessageTextView.setVisibility(View.VISIBLE);
                                    lifafaFailedMessageTextView.setText("You have already received this lifafa :)");
                                    x[0] = 2;
                                    lifafaLoadingImageView.animate().translationY(0).setDuration(0).start();
                                    findViewById(R.id.button5).setVisibility(View.VISIBLE);
                                }else {
                                    //Receiving Process Started
                                    lifafaLoadingImageView.animate().translationY(-500).scaleY(3).scaleX(3)
                                            .setInterpolator(new AccelerateDecelerateInterpolator()).alpha(0).setDuration(500).start();
                                    lifafaResultLayout.animate().translationY(0).scaleY(1).scaleX(1)
                                            .setInterpolator(new AccelerateDecelerateInterpolator()).alpha(1).setDuration(500).start();
                                    int receivedBy = (int) snapshot.child(lifafaID).child("Received By").getChildrenCount();
                                    int MaxReceivers = Integer.parseInt(snapshot.child(lifafaID).child("Max Receivers").getValue().toString());
                                    LifafaReceivingProcess(snapshot,lifafaID,receivedBy,MaxReceivers);
                                }
                            }else {
                                //Receiving Process Started
                                lifafaLoadingImageView.animate().translationY(-500).scaleY(3).scaleX(3)
                                        .setInterpolator(new AccelerateDecelerateInterpolator()).alpha(0).setDuration(500).start();
                                lifafaResultLayout.animate().translationY(0).scaleY(1).scaleX(1)
                                        .setInterpolator(new AccelerateDecelerateInterpolator()).alpha(1).setDuration(500).start();
                                int MaxReceivers = Integer.parseInt(snapshot.child(lifafaID).child("Max Receivers").getValue().toString());
                                LifafaReceivingProcess(snapshot,lifafaID,0,MaxReceivers);
                            }
                        }
                    }
                }else {
                    //Invalid Lifafa ID
                    lifafaExpiredImageView.setVisibility(View.VISIBLE);
                    lifafaFailedMessageTextView.setVisibility(View.VISIBLE);
                    lifafaFailedMessageTextView.setText("Invalid Lifafa OR Expired :)");
                    x[0] = 2;
                    lifafaLoadingImageView.animate().translationY(0).setDuration(0).start();
                    findViewById(R.id.button5).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ShowToast(error.toString());
            }
        });
    }

    private void LifafaReceivingProcess(DataSnapshot snapshot, String lifafaID, int receivedBy, int MaxReceivers){
        String senderName;
        String message = snapshot.child(lifafaID).child("Message").getValue().toString();
        if (message.equals("DEFAULT")){
            message = "Enjoy Your Day :)";
        }
        try {
            senderName = snapshot.child(lifafaID).child("Sender Name").getValue().toString();
        }catch (Exception e){
            senderName = snapshot.child(lifafaID).child("Receiver Name").getValue().toString();
        }
        int availableAmount = Integer.parseInt(snapshot.child(lifafaID).child("Available Amount").getValue().toString());
        int myRewardLimit = availableAmount - MaxReceivers + receivedBy;
        Random random = new Random();
        int myReward = random.nextInt(myRewardLimit);
        availableAmount = availableAmount - myReward;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:MM:ss || dd/MM/yyy");
        String receivedOn = simpleDateFormat.format(new Date());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Lifafa").child(lifafaID);
        databaseReference.child("Available Amount").setValue(availableAmount);
        databaseReference.child("Received By").child(UID).child("Amount Received").setValue(myReward);
        databaseReference.child("Received By").child(UID).child("Received On").setValue(receivedOn);
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID).child("Personal Information").child("Lifafa");
        databaseReference1.child("Wallet Amount").setValue(walletAmount+myReward);

        TextView textView1 = findViewById(R.id.textView54);
        TextView textView2 = findViewById(R.id.textView56);
        TextView textView3 = findViewById(R.id.textView58);
        textView1.setText(senderName);
        textView2.setText("You have received ₹"+myReward);
        textView3.setText(message);
        findViewById(R.id.button5).setVisibility(View.VISIBLE);
    }
}