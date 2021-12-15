package bolomagic.in;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import bolomagic.in.AdaptorAndParse.LifafaCreatedHistoryAdapter;
import bolomagic.in.AdaptorAndParse.LifafaCreatedHistoryParse;
import bolomagic.in.AdaptorAndParse.LifafaReceivedHistoryAdapter;
import bolomagic.in.AdaptorAndParse.LifafaReceivedHistoryParse;

public class LifafaActivity extends AppCompatActivity {

    final int[] x = {0};
    String UID;
    double winingAmount = 0.0;
    String lifafaID = "DEFAULT";
    ImageView lifafaLoadingImageView, lifafaExpiredImageView;
    TextView lifafaFailedMessageTextView;
    CardView lifafaResultLayout;
    ArrayList<LifafaCreatedHistoryParse> lifafaCreatedHistoryParseArrayList = new ArrayList<>();
    LifafaCreatedHistoryAdapter lifafaCreatedHistoryAdapter;
    boolean isLifafaCreationHistoryLoaded = false;

    ArrayList<LifafaReceivedHistoryParse> lifafaReceivedHistoryParseArrayList = new ArrayList<>();
    LifafaReceivedHistoryAdapter lifafaReceivedHistoryAdapter;
    boolean isLifafaReceivedHistoryLoaded = false;

    ListView listView;
    ImageView loadingImageView;

    DataSnapshot lifafaDataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setTitle("");
        }
        setContentView(R.layout.activity_lifafa);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            UID = mFirebaseUser.getUid();

            findViewById(R.id.button1).setOnClickListener(v -> {
                Intent intent = new Intent(LifafaActivity.this, CreateLifafaActivity.class);
                intent.putExtra("Lifafa Type", "GROUP");
                startActivity(intent);
            });

            findViewById(R.id.textView3).setOnClickListener(v -> {
                Intent intent = new Intent(LifafaActivity.this, CreateLifafaActivity.class);
                intent.putExtra("Lifafa Type", "FRIEND");
                startActivity(intent);
            });

            listView = findViewById(R.id.listView);
            loadingImageView = findViewById(R.id.imageView1);
            Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605114138/loading_bp9ico.png").into(loadingImageView);

            lifafaCreatedHistoryAdapter = new LifafaCreatedHistoryAdapter(LifafaActivity.this, lifafaCreatedHistoryParseArrayList);
            lifafaReceivedHistoryAdapter = new LifafaReceivedHistoryAdapter(LifafaActivity.this, lifafaReceivedHistoryParseArrayList);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                if (listView.getAdapter().equals(lifafaCreatedHistoryAdapter)) {
                    String lifafaID = lifafaCreatedHistoryParseArrayList.get(position).getID();
                    Intent intent = new Intent(LifafaActivity.this, LifafaDetailsActivity.class);
                    intent.putExtra("Lifafa ID", lifafaID);
                    intent.putExtra("Lifafa Type", "Sent");
                    startActivity(intent);
                }

                if (listView.getAdapter().equals(lifafaReceivedHistoryAdapter)) {
                    String lifafaID = lifafaReceivedHistoryParseArrayList.get(position).getID();
                    Intent intent = new Intent(LifafaActivity.this, LifafaDetailsActivity.class);
                    intent.putExtra("Lifafa ID", lifafaID);
                    intent.putExtra("Lifafa Type", "Received");
                    startActivity(intent);
                }
            });

            TabLayout tabLayout = findViewById(R.id.tabLayout);
            tabLayout.addTab(tabLayout.newTab().setText("Received"));
            tabLayout.addTab(tabLayout.newTab().setText("Sent"));

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getText().equals("Received")) {
                        listView.setAdapter(lifafaReceivedHistoryAdapter);
                        LoadLifafaReceivedHistory();
                    }

                    if (tab.getText().equals("Sent")) {
                        listView.setAdapter(lifafaCreatedHistoryAdapter);
                        LoadLifafaCreatedHistory();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            listView.setAdapter(lifafaReceivedHistoryAdapter);
            LoadLifafaReceivedHistory();

            lifafaID = getIntent().getStringExtra("Lifafa ID");
            if (!lifafaID.equals("DEFAULT")) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SPL/Users/" + UID + "/Personal Information/Wallets/");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        winingAmount = Double.parseDouble(snapshot.child("Wining Amount").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ShowToast(error.toString());
                    }
                });

                findViewById(R.id.button5).setOnClickListener(v -> HideReceivingLayout());
                findViewById(R.id.lifafaWalletLayout).setVisibility(View.GONE);
                findViewById(R.id.lifafaReceivingLayout).setVisibility(View.VISIBLE);

                lifafaLoadingImageView = findViewById(R.id.imageView14);
                lifafaExpiredImageView = findViewById(R.id.imageView15);
                lifafaFailedMessageTextView = findViewById(R.id.textView53);
                lifafaResultLayout = findViewById(R.id.lifafaResultLayout);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (x[0] == 0) {
                            lifafaLoadingImageView.animate().translationY(-150).setDuration(1000).start();
                            x[0] = 1;
                        }
                        if (x[0] == 1) {
                            lifafaLoadingImageView.animate().translationY(0).setDuration(1000).start();
                            x[0] = 0;
                        }
                        handler.postDelayed(this, 2000);
                    }
                }, 2000);
                GetLifafa();
            }
        } else {
            startActivity(new Intent(LifafaActivity.this, AuthActivity.class));
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void LoadLifafaCreatedHistory() {
        if (isLifafaCreationHistoryLoaded) {
            if (lifafaCreatedHistoryParseArrayList.size() < 1) {
                listView.setVisibility(View.GONE);
                loadingImageView.setVisibility(View.VISIBLE);
                Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605424833/imageedit_9_7876917501_kdsobm.png").into(loadingImageView);
            } else {
                loadingImageView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        } else {
            listView.setVisibility(View.GONE);
            loadingImageView.setVisibility(View.VISIBLE);
            if (lifafaDataSnapshot == null) {
                Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605114138/loading_bp9ico.png").into(loadingImageView);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Lifafa");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lifafaDataSnapshot = snapshot;
                        Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                        for (DataSnapshot next : snapshotIterator) {
                            if (next.child("Sender ID").getValue().toString().equals(UID)) {
                                try {
                                    String ID = next.getKey();
                                    String imageURL = next.child("Short Image URL").getValue().toString();
                                    String count = next.child("Lifafa Count").getValue().toString();
                                    String message = next.child("Message").getValue().toString();
                                    String totalAmount = next.child("Amount").getValue().toString();
                                    lifafaCreatedHistoryParseArrayList.add(new LifafaCreatedHistoryParse(ID, imageURL, count, message, totalAmount));
                                } catch (Exception e) {
                                    ShowToast("We have found error on lifafa #" + next.getKey());
                                }
                            }
                        }
                        isLifafaCreationHistoryLoaded = true;
                        if (lifafaCreatedHistoryParseArrayList.size() < 1) {
                            listView.setVisibility(View.GONE);
                            loadingImageView.setVisibility(View.VISIBLE);
                            Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605424833/imageedit_9_7876917501_kdsobm.png").into(loadingImageView);
                        } else {
                            loadingImageView.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            lifafaCreatedHistoryAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ShowToast(error.toString());
                    }
                });
            } else {
                Iterable<DataSnapshot> snapshotIterator = lifafaDataSnapshot.getChildren();
                for (DataSnapshot next : snapshotIterator) {
                    if (next.child("Sender ID").getValue().toString().equals(UID)) {
                        try {
                            String ID = next.getKey();
                            String imageURL = next.child("Short Image URL").getValue().toString();
                            String count = next.child("Lifafa Count").getValue().toString();
                            String message = next.child("Message").getValue().toString();
                            String totalAmount = next.child("Amount").getValue().toString();
                            lifafaCreatedHistoryParseArrayList.add(new LifafaCreatedHistoryParse(ID, imageURL, count, message, totalAmount));
                        } catch (Exception e) {
                            ShowToast("We have found error on lifafa #" + next.getKey());
                        }
                    }
                }
                isLifafaCreationHistoryLoaded = true;
                if (lifafaCreatedHistoryParseArrayList.size() < 1) {
                    listView.setVisibility(View.GONE);
                    loadingImageView.setVisibility(View.VISIBLE);
                    Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605424833/imageedit_9_7876917501_kdsobm.png").into(loadingImageView);
                } else {
                    loadingImageView.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    lifafaCreatedHistoryAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void LoadLifafaReceivedHistory() {
        if (isLifafaReceivedHistoryLoaded) {
            if (lifafaReceivedHistoryParseArrayList.size() < 1) {
                listView.setVisibility(View.GONE);
                loadingImageView.setVisibility(View.VISIBLE);
                Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605424833/imageedit_9_7876917501_kdsobm.png").into(loadingImageView);
            } else {
                loadingImageView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        } else {
            if (lifafaDataSnapshot == null) {
                listView.setVisibility(View.GONE);
                loadingImageView.setVisibility(View.VISIBLE);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Lifafa");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                        for (DataSnapshot next : snapshotIterator) {
                            if (next.hasChild("Received By")) {
                                if (next.child("Received By").hasChild(UID)) {
                                    try {
                                        String ID = next.getKey();
                                        String senderProfilePic = next.child("Sender Profile Pic").child(UID).child("Received On").getValue().toString();
                                        String senderName = next.child("Sender Name").getValue().toString();
                                        String message = next.child("Message").child(UID).child("Amount Received").getValue().toString();
                                        String date = next.child("Created On").child("Date").getValue().toString() +
                                                "-" + next.child("Created On").child("Month").getValue().toString();
                                        lifafaReceivedHistoryParseArrayList.add(new LifafaReceivedHistoryParse(ID, senderProfilePic, senderName, message, date));
                                    } catch (Exception e) {
                                        ShowToast("We have found error on lifafa #" + next.getKey());
                                    }
                                }
                            }
                        }
                        isLifafaReceivedHistoryLoaded = true;
                        if (lifafaReceivedHistoryParseArrayList.size() < 1) {
                            listView.setVisibility(View.GONE);
                            loadingImageView.setVisibility(View.VISIBLE);
                            Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605424833/imageedit_9_7876917501_kdsobm.png").into(loadingImageView);
                        } else {
                            loadingImageView.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            lifafaReceivedHistoryAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ShowToast(error.toString());
                    }
                });
            } else {
                Iterable<DataSnapshot> snapshotIterator = lifafaDataSnapshot.getChildren();
                for (DataSnapshot next : snapshotIterator) {
                    if (next.hasChild("Received By")) {
                        if (next.child("Received By").hasChild(UID)) {
                            try {
                                String ID = next.getKey();
                                String senderProfilePic = next.child("Sender Profile Pic").child(UID).child("Received On").getValue().toString();
                                String senderName = next.child("Sender Name").getValue().toString();
                                String message = next.child("Message").child(UID).child("Amount Received").getValue().toString();
                                String date = next.child("Created On").child("Date").getValue().toString() +
                                        "-" + next.child("Created On").child("Month").getValue().toString();
                                lifafaReceivedHistoryParseArrayList.add(new LifafaReceivedHistoryParse(ID, senderProfilePic, senderName, message, date));
                            } catch (Exception e) {
                                ShowToast("We have found error on lifafa #" + next.getKey());
                            }
                        }
                    }
                }
                isLifafaReceivedHistoryLoaded = true;
                if (lifafaReceivedHistoryParseArrayList.size() < 1) {
                    listView.setVisibility(View.GONE);
                    loadingImageView.setVisibility(View.VISIBLE);
                    Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605424833/imageedit_9_7876917501_kdsobm.png").into(loadingImageView);
                } else {
                    loadingImageView.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    lifafaReceivedHistoryAdapter.notifyDataSetChanged();
                }
            }

        }
    }

    public void ShowToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void HideReceivingLayout() {
        findViewById(R.id.lifafaWalletLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.lifafaReceivingLayout).setVisibility(View.GONE);
    }

    public void GetLifafa() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Lifafa");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(lifafaID)) {
                    if (snapshot.child(lifafaID).child("Status").getValue().toString().equals("Running")) {
                        if (snapshot.child(lifafaID).child("Sender ID").getValue().toString().equals(UID)) {
                            //You can't claim this Lifafa !
                            lifafaExpiredImageView.setVisibility(View.VISIBLE);
                            lifafaFailedMessageTextView.setVisibility(View.VISIBLE);
                            lifafaFailedMessageTextView.setText("You can't claim your own lifafa :)");
                            x[0] = 2;
                            lifafaLoadingImageView.animate().translationY(0).setDuration(0).start();
                            findViewById(R.id.button5).setVisibility(View.VISIBLE);
                        } else {
                            if (snapshot.child(lifafaID).hasChild("Received By")) {
                                if (snapshot.child(lifafaID).child("Received By").hasChild(UID)) {
                                    //Already lifafa Received...!
                                    lifafaExpiredImageView.setVisibility(View.VISIBLE);
                                    lifafaFailedMessageTextView.setVisibility(View.VISIBLE);
                                    lifafaFailedMessageTextView.setText("You have already received this lifafa :)");
                                    x[0] = 2;
                                    lifafaLoadingImageView.animate().translationY(0).setDuration(0).start();
                                    findViewById(R.id.button5).setVisibility(View.VISIBLE);
                                } else {
                                    //Receiving Process Started
                                    lifafaLoadingImageView.animate().translationY(-500).scaleY(3).scaleX(3)
                                            .setInterpolator(new AccelerateDecelerateInterpolator()).alpha(0).setDuration(500).start();
                                    lifafaResultLayout.animate().translationY(0).scaleY(1).scaleX(1)
                                            .setInterpolator(new AccelerateDecelerateInterpolator()).alpha(1).setDuration(500).start();
                                    int receivedBy = (int) snapshot.child(lifafaID).child("Received By").getChildrenCount();
                                    int MaxReceivers = Integer.parseInt(snapshot.child(lifafaID).child("Lifafa Count").getValue().toString());
                                    LifafaReceivingProcess(snapshot, lifafaID, receivedBy, MaxReceivers);
                                }
                            } else {
                                //Receiving Process Started
                                lifafaLoadingImageView.animate().translationY(-500).scaleY(3).scaleX(3)
                                        .setInterpolator(new AccelerateDecelerateInterpolator()).alpha(0).setDuration(500).start();
                                lifafaResultLayout.animate().translationY(0).scaleY(1).scaleX(1)
                                        .setInterpolator(new AccelerateDecelerateInterpolator()).alpha(1).setDuration(500).start();
                                int MaxReceivers = Integer.parseInt(snapshot.child(lifafaID).child("Lifafa Count").getValue().toString());
                                LifafaReceivingProcess(snapshot, lifafaID, 0, MaxReceivers);
                            }
                        }
                    }
                    if (snapshot.child(lifafaID).child("Status").getValue().toString().equals("Completed")) {
                        //Lifafa Completed
                        lifafaExpiredImageView.setVisibility(View.VISIBLE);
                        lifafaFailedMessageTextView.setVisibility(View.VISIBLE);
                        lifafaFailedMessageTextView.setText("Lifafa, that you want to claim has been completed.");
                        x[0] = 2;
                        lifafaLoadingImageView.animate().translationY(0).setDuration(0).start();
                        findViewById(R.id.button5).setVisibility(View.VISIBLE);
                    }
                } else {
                    //Invalid Lifafa ID
                    lifafaExpiredImageView.setVisibility(View.VISIBLE);
                    lifafaFailedMessageTextView.setVisibility(View.VISIBLE);
                    lifafaFailedMessageTextView.setText("Invalid Lifafa OR Expired !");
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

    private void LifafaReceivingProcess(DataSnapshot snapshot, String lifafaID, int receivedBy, int MaxReceivers) {
        String senderName;
        String profilePicURL = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        String message = snapshot.child(lifafaID).child("Message").getValue().toString();
        String fullImageURL = snapshot.child(lifafaID).child("Full Image URL").getValue().toString();
        senderName = snapshot.child(lifafaID).child("Sender Name").getValue().toString();
        if (message.equals("Default")) {
            message = "Enjoy Your Day :)";
        }

        int myReward = RewardCalculator(MaxReceivers);
        String status = snapshot.child(lifafaID).child("Status").getValue().toString();
        if (receivedBy >= (MaxReceivers - 1)) {
            status = "Completed";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyy || hh:MM:ss");
        String receivedOn = simpleDateFormat.format(new Date());
        String myName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Lifafa").child(lifafaID);
        databaseReference.child("Received By").child(UID).child("Amount Received").setValue(myReward);
        databaseReference.child("Received By").child(UID).child("Received On").setValue(receivedOn);
        databaseReference.child("Received By").child(UID).child("Name").setValue(myName);
        databaseReference.child("Received By").child(UID).child("Profile Pic URL").setValue(profilePicURL);
        databaseReference.child("Status").setValue(status);
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID).child("Personal Information").child("Wallets");
        databaseReference1.child("Wining Amount").setValue(winingAmount + myReward);

        TextView textView1 = findViewById(R.id.textView54);
        TextView textView2 = findViewById(R.id.textView56);
        TextView textView3 = findViewById(R.id.textView58);
        ImageView imageView = findViewById(R.id.imageView2);
        textView1.setText(senderName);
        textView2.setText("You have received ₹" + myReward);
        textView3.setText(message);
        Picasso.get().load(fullImageURL).into(imageView);
        findViewById(R.id.button5).setVisibility(View.VISIBLE);
    }

    public int RewardCalculator(int maxReceivers) {
        int receivedAmount = 0;
        int receivedBy = 0;
        int totalAmount = Integer.parseInt(lifafaDataSnapshot.child(lifafaID).child("Amount").getValue().toString());
        Iterable<DataSnapshot> iterable = lifafaDataSnapshot.child(lifafaID).child("Received By").getChildren();
        for (DataSnapshot next : iterable) {
            receivedAmount = receivedAmount + Integer.parseInt(next.child("Amount Received").getValue().toString());
            receivedBy++;
        }

        int availableReceivers = maxReceivers - receivedBy;
        int availableAmount = totalAmount - receivedAmount;

        int myRewardLimit = (availableAmount / availableReceivers) + 1;
        Random random = new Random();

        return random.nextInt(myRewardLimit);
    }
}