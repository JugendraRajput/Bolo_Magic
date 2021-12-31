package bolomagic.in;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CheckInActivity extends AppCompatActivity {

    TextView timerTextView;
    boolean checkedIn = false;
    int i = 60;

    int myPrize = 0;
    int friendPrize = 0;
    String myPrizeType = "Bonus Amount";
    String friendPrizeType = "Bonus Amount";
    String UID = "";
    String friendUID = "DEFAULT";

    CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long l) {
            String string;
            if (i > 1) {
                string = "seconds";
            } else {
                string = "second";
            }
            String finalString = "<b>" + i + "</b>" + " " + string + " remaining";
            timerTextView.setText(Html.fromHtml(finalString));
            i = i - 1;
            if (!isConnectionAvailable(CheckInActivity.this)) {
                Toast.makeText(CheckInActivity.this, "Connection Lost :(", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish() {
            try {
                if (isConnectionAvailable(CheckInActivity.this)) {
                    ProgressDialog progressDialog = new ProgressDialog(CheckInActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Fetching...");
                    progressDialog.show();
                    DatabaseReference myDatabaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyy");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                    String code = simpleDateFormat.format(new Date());
                    if (ReferActivity.doCheckIn) {
                        if (!(ReferActivity.myTodayDay > 30)) {
                            myDatabaseReference.child("Personal Information").child("Wallets").child("Refer Check In History").child(code).child("Date").setValue(ServerValue.TIMESTAMP);
                            myDatabaseReference.child("Personal Information").child("Wallets").child("Refer Check In History").child(code).child("Reward").setValue(myPrize);

                            int myWallet = Integer.parseInt(String.valueOf(ReferActivity.userDataSnapshot.child("Personal Information").child("Wallets").child(myPrizeType).getValue()));
                            myDatabaseReference.child("Personal Information").child("Wallets").child(myPrizeType).setValue(myWallet + myPrize);
                        }
                        if (!friendUID.equals("DEFAULT")) {
                            if (!(ReferActivity.friendTodayDay > 30)) {
                                myDatabaseReference.child("Personal Information").child("Wallets").child("Friend Refer Check In History").child(code).child("Date").setValue(ServerValue.TIMESTAMP);
                                myDatabaseReference.child("Personal Information").child("Wallets").child("Friend Refer Check In History").child(code).child("Reward").setValue(friendPrize);

                                DatabaseReference friendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(friendUID);
                                String myName = ReferActivity.userDataSnapshot.child("Personal Information").child("Name").getValue().toString();
                                friendDatabaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(code).child("Name").setValue("Amount Received by Referral Check-In by " + myName);
                                friendDatabaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(code).child("Amount").setValue(friendPrize);
                                friendDatabaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(code).child("Time").setValue(ServerValue.TIMESTAMP);

                                friendDatabaseReference.child("Personal Information").child("Wallets").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int friendWallet = Integer.parseInt(snapshot.child(friendPrizeType).getValue().toString());
                                        friendDatabaseReference.child("Personal Information").child("Wallets").child(friendPrizeType).setValue(friendWallet + friendPrize);
                                        if (!(ReferActivity.myTodayDay > 30)) {
                                            Toast.makeText(CheckInActivity.this, "Check-In successful", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(CheckInActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(CheckInActivity.this, "Check-In successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    } else {
                        myDatabaseReference.child("Personal Information").child("Wallets").child("Respect").child(code).child("Respect").setValue(1);
                        Toast.makeText(CheckInActivity.this, "Respect Increased :)", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    timerTextView.setVisibility(View.GONE);
                    countDownTimer.cancel();
                    ReferActivity.doCheckIn = false;
                    checkedIn = true;
                } else {
                    new AlertDialog.Builder(CheckInActivity.this)
                            .setIcon(R.drawable.ic_launcher_foreground)
                            .setTitle("!! Warning !!")
                            .setMessage("Connection Lost :(")
                            .setCancelable(false)
                            .setPositiveButton("Okay", (dialogInterface, i) -> {
                                countDownTimer.cancel();
                                finish();
                            })
                            .show();
                }
            } catch (Exception e) {
                Toast.makeText(CheckInActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable();
        }
        return false;
    }

    static class webCont extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UID = firebaseAuth.getCurrentUser().getUid();

        timerTextView = findViewById(R.id.textView14);

        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new webCont());
        webView.loadUrl("https://www.instagram.com/gameskharidolite/" );

        if (ReferActivity.doCheckIn) {
            if (!(ReferActivity.myTodayDay > 30)) {
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("SPL").child("Refer Screen")
                        .child("Receiver Prize List").child("Day " + (ReferActivity.myTodayDay + 1));
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myPrize = Integer.parseInt(snapshot.child("Prize").getValue().toString());
                        myPrizeType = snapshot.child("Prize Type").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CheckInActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (!(ReferActivity.friendTodayDay > 30)) {
                if (ReferActivity.userDataSnapshot.child("Personal Information").child("Refer Details").hasChild("Friend UID")) {
                    friendUID = ReferActivity.userDataSnapshot.child("Personal Information").child("Refer Details").child("Friend UID").getValue().toString();
                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("SPL").child("Refer Screen")
                            .child("Sender Prize List").child("Day " + (ReferActivity.friendTodayDay + 1));
                    databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            friendPrize = Integer.parseInt(snapshot.child("Prize").getValue().toString());
                            friendPrizeType = snapshot.child("Prize Type").getValue().toString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(CheckInActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

        countDownTimer.start();

        findViewById(R.id.imageView10).setOnClickListener(view -> {
            if (checkedIn) {
                finish();
            } else {
                new AlertDialog.Builder(CheckInActivity.this)
                        .setIcon(R.drawable.ic_launcher_foreground)
                        .setTitle("!! Warning !!")
                        .setMessage("If you will close this screen than you will lose the progress.\n Are you sure?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            countDownTimer.cancel();
                            finish();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (checkedIn) {
            finish();
        } else {
            new AlertDialog.Builder(CheckInActivity.this)
                    .setIcon(R.drawable.ic_launcher_foreground)
                    .setTitle("!! Warning !!")
                    .setMessage("If you will close this screen than you will lose the progress.\n Are you sure?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        countDownTimer.cancel();
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }
}
