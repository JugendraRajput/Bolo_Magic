package bolomagic.in;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import bolomagic.in.AdaptorAndParse.CheckInAdapter;
import bolomagic.in.AdaptorAndParse.CheckInParse;
import bolomagic.in.AdaptorAndParse.ReferAdaptor;
import bolomagic.in.AdaptorAndParse.ReferParse;
import bolomagic.in.CustomDialog.CustomDialogShare;

public class ReferActivity extends AppCompatActivity {

    public static String sharingMessage = "Bolo Magic - Play and Earn Money. Download now: https://bolomagic.in";
    static int myTodayDay = 0;
    static int friendTodayDay = 0;
    static boolean doCheckIn = false;
    static DataSnapshot userDataSnapshot;
    TextView myReferCodeTextVew, friendReferCodeTextView, referBottomMessageTextView;
    EditText referEditText;
    Button submitButton;
    String todayDateOnServer = "DEFAULT";
    String UID;
    ArrayList<ReferParse> referParses = new ArrayList<>();
    ProgressDialog progressDialog;
    boolean isSecurityChecked = false;
    boolean isDeviceRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        setTitle("Refer And Earn");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();

        myReferCodeTextVew = findViewById(R.id.textView26);
        friendReferCodeTextView = findViewById(R.id.textView27);
        referBottomMessageTextView = findViewById(R.id.textView28);

        referEditText = findViewById(R.id.editText);

        submitButton = findViewById(R.id.button15);

        submitButton.setOnClickListener(v -> ApplyRefer());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Fetching...");
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                userDataSnapshot = snapshot;
                if (snapshot.child("Security Information").child("Account Status").getValue().toString().equals("GOOD")) {
                    String timeString = snapshot.child("Personal Information").child("Last Active").getValue().toString();
                    todayDateOnServer = getDate(Long.parseLong(timeString));
                    myReferCodeTextVew.setText(String.format("Refer Code: %s", snapshot.child("Personal Information").child("Refer Details").child("Refer Code").getValue()));
                    if (!snapshot.child("Personal Information").child("Wallets").child("Refer Check In History").hasChild(todayDateOnServer)) {
                        doCheckIn = true;
                    } else {
                        doCheckIn = false;
                    }
                    if (snapshot.child("Personal Information").child("Wallets").hasChild("Refer Check In History")) {
                        myTodayDay = (int) snapshot.child("Personal Information").child("Wallets").child("Refer Check In History").getChildrenCount();
                    }
                    if (snapshot.child("Personal Information").child("Wallets").hasChild("Friend Refer Check In History")) {
                        friendTodayDay = (int) snapshot.child("Personal Information").child("Wallets").child("Friend Refer Check In History").getChildrenCount();
                    }
                    AssignCheckIn(myTodayDay);
                    ArrangeReferHistory(snapshot);
                    if (!snapshot.child("Personal Information").child("Refer Details").child("Friend Refer Code").getValue().toString().equals("NEVER_USE")) {
                        friendReferCodeTextView.setText(snapshot.child("Personal Information").child("Refer Details").child("Friend Refer Code").getValue().toString());
                        referBottomMessageTextView.setVisibility(View.VISIBLE);
                        friendReferCodeTextView.setVisibility(View.VISIBLE);
                        referEditText.setVisibility(View.GONE);
                        submitButton.setVisibility(View.GONE);
                    } else {
                        @SuppressLint("HardwareIds")
                        String android_id = Settings.Secure.getString(ReferActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                        FirebaseDatabase.getInstance().getReference().child("SPL").child("Referral Registered Devices").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                isSecurityChecked = true;
                                if (snapshot.hasChild(android_id)) {
                                    isDeviceRegistered = true;
                                    friendReferCodeTextView.setText("Error: We have found duplicate account on same device.");
                                    friendReferCodeTextView.setVisibility(View.VISIBLE);
                                    friendReferCodeTextView.setPadding(12, 12, 12, 12);
                                    referEditText.setVisibility(View.GONE);
                                    submitButton.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ReferActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(ReferActivity.this, "Your account has been blocked...!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReferActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Share(View view) {
        CustomDialogShare customDialogShare = new CustomDialogShare(this);
        customDialogShare.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialogShare.show();
    }

    public void AssignCheckIn(int i) {
        RecyclerView recyclerView = findViewById(R.id.recycleView);
        List<CheckInParse> checkInParseList = new ArrayList<>();
        CheckInAdapter checkInAdapter = new CheckInAdapter(checkInParseList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(checkInAdapter);

        checkInParseList.clear();
        for (int j = 1; j < 31; j++) {
            if (j <= i) {
                checkInParseList.add(new CheckInParse("https://res.cloudinary.com/dsznqkutd/image/upload/v1623817106/checked_wbucwx.png", "Day " + j));
            } else {
                checkInParseList.add(new CheckInParse("https://res.cloudinary.com/dsznqkutd/image/upload/v1623817105/unchecked_ylmss3.png", "Day " + j));
            }
        }
        checkInAdapter.notifyDataSetChanged();
    }

    public void OpenCheckInActivity(View view) {
        if (doCheckIn) {
            startActivity(new Intent(ReferActivity.this, CheckInActivity.class));
        } else {
            Toast.makeText(this, "Please check-In next day", Toast.LENGTH_SHORT).show();
        }
    }

    public void ArrangeReferHistory(DataSnapshot dataSnapshot) {
        referParses.clear();
        if (dataSnapshot.child("Personal Information").child("Refer Details").hasChild("Refer Activity")) {
            Iterable<DataSnapshot> snapshotIterator = dataSnapshot.child("Personal Information").child("Refer Details").child("Refer Activity").child("Refer Users").getChildren();
            for (DataSnapshot next : snapshotIterator) {
                String friendUID = next.getKey();
                String friendName = next.child("Friend Name").getValue().toString();
                String referDate = next.child("Time").getValue().toString();
                referDate = getDate(Long.parseLong(referDate));
                ReferParse referParse = new ReferParse("Date: " + referDate, friendName, friendUID);
                referParses.add(referParse);
            }
            ReferAdaptor referAdaptor = new ReferAdaptor(ReferActivity.this, R.layout.refer_layout_view, referParses);
            ListView referHistoryListView = findViewById(R.id.referHistoryListView);
            referHistoryListView.setAdapter(referAdaptor);
            findViewById(R.id.referHistoryLayout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.referHistoryLayout).setVisibility(View.GONE);
        }
    }

    public void Close(View view) {
        findViewById(R.id.constraintLayout14).setVisibility(View.GONE);
        findViewById(R.id.imageView9).setVisibility(View.GONE);
    }

    public String getDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyy");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        return simpleDateFormat.format(date);
    }

    public void ApplyRefer() {
        if (isSecurityChecked) {
            final String friendReferCode = referEditText.getText().toString();
            if (friendReferCode.equals("")) {
                Toast.makeText(this, "Please enter friend Refer Code...!", Toast.LENGTH_SHORT).show();
            } else {
                final ProgressDialog referProgressDialog = new ProgressDialog(this);
                referProgressDialog.setCancelable(false);
                referProgressDialog.setCanceledOnTouchOutside(false);
                referProgressDialog.setMessage("Fetching...");
                referProgressDialog.show();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                        boolean temp = true;
                        for (DataSnapshot next : snapshotIterator) {
                            if (!next.getKey().equals(UID)) {
                                if (next.child("Personal Information").child("Refer Details").hasChild("Refer Code")) {
                                    String friendReferCodeOnServer = next.child("Personal Information").child("Refer Details").child("Refer Code").getValue().toString();
                                    if (friendReferCodeOnServer.equals(friendReferCode)) {

                                        databaseReference.child(next.getKey()).child("Personal Information").child("Refer Details").child("Refer Activity").child("Refer Users").child(UID).child("Time").setValue(ServerValue.TIMESTAMP);
                                        databaseReference.child(next.getKey()).child("Personal Information").child("Refer Details").child("Refer Activity").child("Refer Users").child(UID).child("Friend Name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                                        databaseReference.child(UID).child("Personal Information").child("Refer Details").child("Friend UID").setValue(next.getKey());
                                        databaseReference.child(UID).child("Personal Information").child("Refer Details").child("Friend Refer Code Applied On").setValue(ServerValue.TIMESTAMP);
                                        databaseReference.child(UID).child("Personal Information").child("Refer Details").child("Friend Refer Code").setValue(referEditText.getText().toString());
                                        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(ReferActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                                        FirebaseDatabase.getInstance().getReference().child("SPL").child("Referral Registered Devices").child(android_id).child(UID).child("Date").setValue(ServerValue.TIMESTAMP);

                                        friendReferCodeTextView.setText(referEditText.getText().toString());
                                        referBottomMessageTextView.setVisibility(View.VISIBLE);
                                        referBottomMessageTextView.setText("Refer Code applied successfully. You and Your friend received their reward soon.\nRefer code  you use:");
                                        friendReferCodeTextView.setVisibility(View.VISIBLE);
                                        referEditText.setVisibility(View.GONE);
                                        submitButton.setVisibility(View.GONE);

                                        Toast.makeText(ReferActivity.this, "Refer Code applied", Toast.LENGTH_SHORT).show();

                                        temp = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (temp) {
                            Toast.makeText(ReferActivity.this, "Refer Code not found...!", Toast.LENGTH_SHORT).show();
                        }
                        referProgressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ReferActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(this, "Security is running for your device.\nPlease wait...", Toast.LENGTH_SHORT).show();
        }
    }
}
