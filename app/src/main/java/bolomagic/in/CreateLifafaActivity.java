package bolomagic.in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import bolomagic.in.SendNotificationPack.APIService;
import bolomagic.in.SendNotificationPack.Client;
import bolomagic.in.SendNotificationPack.Data;
import bolomagic.in.SendNotificationPack.MyResponse;
import bolomagic.in.SendNotificationPack.NotificationSender;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateLifafaActivity extends AppCompatActivity {

    String receiver = "NULL";
    Button lifafaButton1,lifafaButton2,lifafaSendButton;
    String UID = "DEFAULT";
    String LifafaID = "DEFAULT";
    FirebaseAuth mAuth;
    double walletAmount = 0.0;
    boolean temp = true;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            setContentView(R.layout.activity_create_lifafa);
            UID = mAuth.getCurrentUser().getUid();
            lifafaButton1 = findViewById(R.id.lifafaButton1);
            lifafaButton2 = findViewById(R.id.lifafaButton2);
            lifafaSendButton = findViewById(R.id.lifafaSendButton);

            apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SPL/Users/").child(UID).child("Personal Information").child("Lifafa").child("Wallet Amount");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        walletAmount = Double.parseDouble(snapshot.getValue().toString());
                    }catch (Exception exception){
                        ShowToast(exception.toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ShowToast(error.toString());
                }
            });

            lifafaButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.constraintLayoutCount).setVisibility(View.GONE);
                    lifafaButton1.setBackgroundResource(R.drawable.blue_background_round);
                    lifafaButton2.setBackgroundResource(R.drawable.cream_background_round);
                    receiver = "FRIEND";
                }
            });

            lifafaButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.constraintLayout).setVisibility(View.GONE);
                    findViewById(R.id.constraintLayoutCount).setVisibility(View.VISIBLE);
                    lifafaButton1.setBackgroundResource(R.drawable.cream_background_round);
                    lifafaButton2.setBackgroundResource(R.drawable.blue_background_round);
                    receiver = "GROUP";
                }
            });

            lifafaSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText emailEditText = findViewById(R.id.editTextTextEmailAddress);
                    EditText amountEditText = findViewById(R.id.editTextTextAmount);
                    EditText messageEditText = findViewById(R.id.editTextTextMessage);
                    EditText maxUsersEditText = findViewById(R.id.editTextTexMaxUsers);
                    final String email = emailEditText.getText().toString();
                    final String maxUsers = maxUsersEditText.getText().toString();
                    final String amount = amountEditText.getText().toString();
                    String message = messageEditText.getText().toString();
                    if (message.equals("")){
                        message = "DEFAULT";
                    }

                    if (amount.equals("")){
                        amountEditText.setError("Enter Maximum Amount");
                        amountEditText.requestFocus();
                    }else if(walletAmount<Double.parseDouble(amount)){
                        amountEditText.setError("Too much Amount");
                        amountEditText.requestFocus();
                        ShowToast("You don't have sufficient Balance.");
                    }else {
                        if (Integer.parseInt(amount) < 1){
                            amountEditText.setError("Lifafa Amount can't be less then Rs. 1");
                            amountEditText.requestFocus();
                        }else {
                            final String finalMessage = message;
                            if (receiver.equals("NULL")){
                                ShowToast("Please select the receiver...!");
                            }
                            if (receiver.equals("FRIEND")) {
                                if (email.equals("")){
                                    emailEditText.setError("Enter Friend Email");
                                    emailEditText.requestFocus();
                                }else {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users");
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                                            for (final DataSnapshot next : snapshotIterator) {
                                                if (next.child("Personal Information").child("Email").getValue().toString().equals(email)){
                                                    final String token = next.child("Personal Information").child("token").getValue().toString();
                                                    temp = false;
                                                    new AlertDialog.Builder(CreateLifafaActivity.this)
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .setTitle("Warning")
                                                            .setMessage("Friend Name: "+next.child("Personal Information").child("Name").getValue().toString()+"\n" +
                                                                    "We have found you friend. is this correct ?")
                                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    CreateLifafaForFriend("1",next.getKey(),amount,finalMessage,token,next.child("Personal Information").child("Name").getValue().toString());
                                                                }
                                                            }).setNegativeButton("No",null)
                                                            .show();
                                                    break;
                                                }
                                            }
                                            if (temp){
                                                new AlertDialog.Builder(CreateLifafaActivity.this)
                                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                                        .setTitle("Warning")
                                                        .setMessage("We have not found your friend, registered with "+email+" email." +
                                                                "\nWe suggest you to change it OR ask to your friend again.")
                                                        .setPositiveButton("Change", null)
                                                        .show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            ShowToast(error.toString());
                                        }
                                    });
                                }
                            }
                            if (receiver.equals("GROUP")){
                                if (!maxUsers.equals("")){
                                    if (Integer.parseInt(amount) < Integer.parseInt(maxUsers)){
                                        amountEditText.setError("Lifafa Amount  Can't be less then max users.");
                                        amountEditText.requestFocus();
                                    }else {
                                        if (Integer.parseInt(amount) < 1 ){
                                            amountEditText.setError("Lifafa Amount can't be less then Rs. 1");
                                            amountEditText.requestFocus();
                                        }else {
                                            new AlertDialog.Builder(CreateLifafaActivity.this)
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .setTitle("!! Notice !!")
                                                    .setMessage("This lifafa can be access by any one.\nWould you like to continue ?")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            CreateLifafaForGroup(maxUsers, amount, finalMessage);
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }
                                }else {
                                    maxUsersEditText.setError("Enter Maximum Users");
                                    maxUsersEditText.requestFocus();
                                }
                            }
                        }
                    }
                }
            });
        }else {
            finish();
            startActivity(new Intent(CreateLifafaActivity.this, AuthActivity.class));
        }
    }

    protected void CreateLifafaForFriend(String maxUsers, String friendUID, String maxAmount, String message, String friendToken, String friendName){
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
        LifafaID = lifafaID;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SPL/Lifafa/");
        databaseReference.child(lifafaID).child("Sender ID").setValue(UID);
        databaseReference.child(lifafaID).child("Receiver Type").setValue("FRIEND");
        databaseReference.child(lifafaID).child("Receiver ID").setValue(friendUID);
        databaseReference.child(lifafaID).child("Sender Name").setValue(mAuth.getCurrentUser().getDisplayName());
        databaseReference.child(lifafaID).child("Max Amount").setValue(maxAmount);
        databaseReference.child(lifafaID).child("Max Receivers").setValue(maxUsers);
        databaseReference.child(lifafaID).child("Available Amount").setValue(maxAmount);
        databaseReference.child(lifafaID).child("Message").setValue(message);
        databaseReference.child(lifafaID).child("Status").setValue("Running");
        databaseReference.child(lifafaID).child("Created On").child("Year").setValue(simpleDateFormat1.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("Month").setValue(simpleDateFormat2.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("Date").setValue(simpleDateFormat3.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("hh").setValue(simpleDateFormat4.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("mm").setValue(simpleDateFormat5.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("ss").setValue(simpleDateFormat6.format(new Date()));
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("SPL/Users/").child(UID).child("Personal Information").child("Lifafa").child("Wallet Amount");
        dR.setValue(walletAmount-Double.parseDouble(maxAmount));
        sendNotifications(friendToken,"Lifafa Received", "We have received new Lifafa for you from "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        ShowToast("Lifafa has been created successfully. Please check it in your history.");
        createReferLink(lifafaID);
    }

    protected void CreateLifafaForGroup(String maxUsers, String maxAmount, String message){
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
        LifafaID = lifafaID;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SPL/Lifafa/");
        databaseReference.child(lifafaID).child("Sender ID").setValue(UID);
        databaseReference.child(lifafaID).child("Receiver Type").setValue("GROUP");
        databaseReference.child(lifafaID).child("Receiver Name").setValue("Every One");
        databaseReference.child(lifafaID).child("Max Amount").setValue(maxAmount);
        databaseReference.child(lifafaID).child("Max Receivers").setValue(maxUsers);
        databaseReference.child(lifafaID).child("Available Amount").setValue(maxAmount);
        databaseReference.child(lifafaID).child("Message").setValue(message);
        databaseReference.child(lifafaID).child("Status").setValue("Running");
        databaseReference.child(lifafaID).child("Created On").child("Year").setValue(simpleDateFormat1.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("Month").setValue(simpleDateFormat2.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("Date").setValue(simpleDateFormat3.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("hh").setValue(simpleDateFormat4.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("mm").setValue(simpleDateFormat5.format(new Date()));
        databaseReference.child(lifafaID).child("Created On").child("ss").setValue(simpleDateFormat6.format(new Date()));
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("SPL/Users/").child(UID).child("Personal Information").child("Lifafa").child("Wallet Amount");
        dR.setValue(walletAmount-Double.parseDouble(maxAmount));
        ShowToast("Lifafa has been created successfully. Please check it in your history.");
        createReferLink(lifafaID);
    }

    public void sendNotifications(String token, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, token);
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(CreateLifafaActivity.this, "Notification failed to send\n" + response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Toast.makeText(CreateLifafaActivity.this, "Failure\n" + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createReferLink(String prodid){
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.bolomagic.in/lifafa/id="+prodid))
                .setDynamicLinkDomain("bolomagic.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setIosParameters(new DynamicLink.IosParameters.Builder("https://www.bolomagic.in/lifafa/id="+prodid).build())
                .buildDynamicLink();
        Uri dynamicLinkUri = dynamicLink.getUri();
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLinkUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            ShowToast(shortLink.toString());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SPL/Lifafa/");
                            databaseReference.child(LifafaID).child("Link").setValue(shortLink.toString());
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,  shortLink.toString());
                            intent.setType("text/plain");
                            startActivity(intent);
                        } else {
                            ShowToast(String.valueOf(task.getException()));
                        }
                    }
                });
    }

    public void ShowToast(String errorMessage){
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}