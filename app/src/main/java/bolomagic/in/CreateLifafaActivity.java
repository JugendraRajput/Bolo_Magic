package bolomagic.in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import bolomagic.in.AdaptorAndParse.LifafaThemeAdapter;
import bolomagic.in.AdaptorAndParse.LifafaThemeParse;

public class CreateLifafaActivity extends AppCompatActivity {

    String UID = "DEFAULT";
    FirebaseAuth mAuth;
    double walletAmount = 0.0;

    String lifafaType = "DEFAULT";
    String shortImageURL = "Default";
    String fullImageURL = "Default";
    int lastSelected = 0;
    boolean isAccountReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
            ActionBar actionBar = getSupportActionBar();
            lifafaType = getIntent().getStringExtra("Lifafa Type");
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                if (lifafaType.equals("GROUP")){
                    setTitle("Create Lucky Lifafa");
                }
                if (lifafaType.equals("FRIEND")){
                    setTitle("Create Friend Lifafa");
                }
            }
            setContentView(R.layout.activity_create_lifafa);
            UID = mAuth.getCurrentUser().getUid();

            ArrayList<LifafaThemeParse> lifafaThemeParseArrayList = new ArrayList<>();
            LifafaThemeAdapter lifafaThemeAdapter = new LifafaThemeAdapter(lifafaThemeParseArrayList);
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(lifafaThemeAdapter);
            ItemClickSupport.addTo(recyclerView)
                    .setOnItemClickListener((view, i, v) -> {
                        if (i != lastSelected){
                            fullImageURL = lifafaThemeParseArrayList.get(i).getFullImageURL();
                            shortImageURL = lifafaThemeParseArrayList.get(i).getShortImageURL();
                            lifafaThemeParseArrayList.get(i).setIsSelected("true");
                            lifafaThemeParseArrayList.get(lastSelected).setIsSelected("false");
                            Picasso.get().load(fullImageURL).into((ImageView) findViewById(R.id.imageView));
                            lifafaThemeAdapter.notifyDataSetChanged();
                            lastSelected = i;
                        }
                    });

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Lifafa Theme");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Iterable<DataSnapshot> iterable = snapshot.getChildren();
                    int i = 0;
                    for (DataSnapshot next : iterable){
                        String id = next.getKey();
                        String ShortImageURL = next.child("Short Image URL").getValue().toString();
                        String FullImageURL = next.child("Full Image URL").getValue().toString();
                        String title = next.child("Title").getValue().toString();
                        String isSelected = "false";
                        if (i == 0){
                            isSelected = "true";
                            fullImageURL = FullImageURL;
                            shortImageURL = ShortImageURL;
                            Picasso.get().load(fullImageURL).into((ImageView) findViewById(R.id.imageView));
                        }
                        lifafaThemeParseArrayList.add(new LifafaThemeParse(id,ShortImageURL,FullImageURL,title,isSelected));
                        i++;
                    }
                    lifafaThemeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CreateLifafaActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            EditText editText1 = findViewById(R.id.editText1);
            EditText editText2 = findViewById(R.id.editText2);
            EditText editText3 = findViewById(R.id.editText3);
            Button button = findViewById(R.id.button);

            if (lifafaType.equals("FRIEND")){
                editText1.setText("1");
                editText1.setEnabled(false);
                TextView textView = findViewById(R.id.textView);
                textView.setText("This amount will be randomly distributed to your friend");
            }else {
                editText1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int totalLifafa = 0;
                        try {
                            totalLifafa = Integer.parseInt(editText1.getText().toString());
                            if (totalLifafa < 1){
                                editText1.setError("No. of Lifafa can't be less then 1");
                                editText1.requestFocus();
                            }else {
                                TextView textView = findViewById(R.id.textView);
                                textView.setText("This amount will be randomly distributed between "+totalLifafa+" people");
                            }
                        }catch (Exception e){
                            editText1.setError("Enter valid No. of Lifafa !");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            button.setOnClickListener(v -> {
                int totalLifafa = 0;
                int totalAmount = 0;
                boolean b1 = false;
                boolean b2 = false;
                String message = "Default";
                try {
                    totalLifafa = Integer.parseInt(editText1.getText().toString());
                    if (totalLifafa < 1){
                        editText1.setError("No. of Lifafa can't be less then 1");
                        editText1.requestFocus();
                    }else {
                        b1 = true;
                    }
                }catch (Exception e){
                    editText1.setError("Enter valid No. of Lifafa !");
                    editText1.requestFocus();
                }
                try {
                    totalAmount = Integer.parseInt(editText2.getText().toString());
                    if (totalAmount < totalLifafa){
                        editText2.setError("Amount can't be less then "+totalLifafa);
                        editText2.requestFocus();
                    }else {
                        b2 = true;
                    }
                }catch (Exception e){
                    editText2.setError("Enter valid Amount !");
                    editText2.requestFocus();
                }
                if (b1 && b2){
                    message = editText3.getText().toString();
                    if (message.equals("")){
                        message = "Default";
                    }
                    if (isAccountReady){
                        if (totalAmount <= walletAmount){
                            editText1.setEnabled(false);
                            editText2.setEnabled(false);
                            editText3.setEnabled(false);
                            button.setEnabled(false);
                            CreateLifafa(String.valueOf(totalLifafa),String.valueOf(totalAmount),message);
                        }else {
                            ShowToast("You don't have sufficient balance !");
                        }
                     }else {
                        ShowToast("Something went wrong !");
                    }
                }
            });

            FirebaseDatabase.getInstance().getReference("SPL/Users/").child(UID).child("Personal Information").child("Wallets").child("Deposit Amount").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        walletAmount = Double.parseDouble(snapshot.getValue().toString());
                        isAccountReady = true;
                    }catch (Exception exception){
                        isAccountReady = false;
                        ShowToast(exception.toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ShowToast(error.toString());
                }
            });
        }else {
            finish();
            startActivity(new Intent(CreateLifafaActivity.this, AuthActivity.class));
        }
    }

    protected void CreateLifafa(String maxUsers, String maxAmount, String message){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyMMddhhmmss");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyy");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM");
        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("dd");
        SimpleDateFormat simpleDateFormat4 = new SimpleDateFormat("hh");
        SimpleDateFormat simpleDateFormat5 = new SimpleDateFormat("mm");
        SimpleDateFormat simpleDateFormat6 = new SimpleDateFormat("ss");
        Random r = new Random();
        int O1 = r.nextInt(9);
        int O2 = r.nextInt(9);
        String lifafaID = simpleDateFormat.format(new Date())+O1+O2;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SPL/Lifafa/");
        databaseReference.child(lifafaID).child("Sender ID").setValue(UID);
        databaseReference.child(lifafaID).child("Sender Name").setValue(mAuth.getCurrentUser().getDisplayName());
        databaseReference.child(lifafaID).child("Sender Profile Pic").setValue(mAuth.getCurrentUser().getPhotoUrl().toString());
        databaseReference.child(lifafaID).child("Amount").setValue(maxAmount);
        databaseReference.child(lifafaID).child("Short Image URL").setValue(shortImageURL);
        databaseReference.child(lifafaID).child("Full Image URL").setValue(fullImageURL);
        databaseReference.child(lifafaID).child("Lifafa Count").setValue(maxUsers);
        databaseReference.child(lifafaID).child("Message").setValue(message);
        databaseReference.child(lifafaID).child("Status").setValue("Running");
        databaseReference.child(lifafaID).child("Created On").child("Year").setValue(simpleDateFormat1.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("Month").setValue(simpleDateFormat2.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("Date").setValue(simpleDateFormat3.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("hh").setValue(simpleDateFormat4.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("mm").setValue(simpleDateFormat5.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("ss").setValue(simpleDateFormat6.format(new Date()));
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("SPL/Users/").child(UID).child("Personal Information").child("Wallets").child("Deposit Amount");
        dR.setValue(walletAmount-Double.parseDouble(maxAmount));
        ShowToast("Lifafa has been created successfully.");
        createReferLink(lifafaID);
    }

    public void createReferLink(String lifafaID){
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.bolomagic.in/lifafa/id="+lifafaID))
                .setDynamicLinkDomain("bolomagic.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setIosParameters(new DynamicLink.IosParameters.Builder("https://www.bolomagic.in/lifafa/id="+lifafaID).build())
                .buildDynamicLink();
        Uri dynamicLinkUri = dynamicLink.getUri();
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLinkUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Uri shortLink = task.getResult().getShortLink();
                        ShowToast(shortLink.toString());
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SPL/Lifafa/");
                        databaseReference.child(lifafaID).child("Link").setValue(shortLink.toString());
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT,  "I have created lucky lifafa on Bolo Magic. Claim it from "+shortLink.toString());
                        intent.setType("text/plain");
                        startActivity(intent);
                    } else {
                        ShowToast(String.valueOf(task.getException()));
                    }
                });
    }

    public void ShowToast(String errorMessage){
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}