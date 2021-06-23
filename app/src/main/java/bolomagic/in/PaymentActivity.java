package bolomagic.in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity {

    EditText amountEt;
    final int UPI_PAYMENT = 0;
    String UID = "";
    String myName = "";
    String myEmail = "";
    FirebaseAuth mAuth;
    String walletType = "DEFAULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window mWindow = getWindow();
        mWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_payment);

        amountEt = findViewById(R.id.amountEditText);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            UID = mFirebaseUser.getUid();
            myName = mAuth.getCurrentUser().getDisplayName();
            myEmail = mAuth.getCurrentUser().getEmail();
        }
        walletType = getIntent().getStringExtra("Wallet Type");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Objects.requireNonNull(snapshot.child("Security Information").child("Account Status").getValue()).toString().equals("GOOD")){
                    @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(PaymentActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    if(snapshot.child("Security Information").child("Android ID").getValue().toString().equals(android_id)) {
                        int walletAmount = 0;
                        if (walletType.equals("Wallet")){
                            walletAmount = Integer.parseInt(snapshot.child("Personal Information").child("Wallets").child("Wining Amount").getValue().toString());
                        }
                        if (walletType.equals("Lifafa")){
                            walletAmount = Integer.parseInt(snapshot.child("Personal Information").child("Lifafa").child("Wallet Amount").getValue().toString());
                        }
                        TextView textView = findViewById(R.id.textView19);
                        textView.setText("Available Balance: "+walletAmount);
                    } else {
                        databaseReference.keepSynced(false);
                        new AlertDialog.Builder(PaymentActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("!! Notice !!")
                                .setMessage("You have login from a different device with this account. Please use app in newly login device or login here -Again")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> finish())
                                .show();
                    }
                }else {
                    databaseReference.keepSynced(false);
                    new AlertDialog.Builder(PaymentActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("!! Notice !!")
                            .setMessage("Your Account has been blocked...!")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                finish();
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        //For UPI Payment
        findViewById(R.id.button7).setOnClickListener(view -> {
            //Getting the values from the EditTexts
            String amount = amountEt.getText().toString();
            if (!amount.equals("")){
                amountEt.setEnabled(false);
                String note = "Add money to Bolo Magic of rs. "+amount+" by "+myName;
                String name = myName;
                String upiId = "8077233199@paytm";
                payUsingUpi(amount, upiId, name, note);
            }else {
                amountEt.setError("Enter Amount !");
                amountEt.requestFocus();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void Add50(View view){
        amountEt.setText("50");
    }
    @SuppressLint("SetTextI18n")
    public void Add100(View view){
        amountEt.setText("100");
    }
    @SuppressLint("SetTextI18n")
    public void Add200(View view){
        amountEt.setText("200");
    }

    //Below all methods for UPI Payment
    void payUsingUpi(String amount, String upiId, String name, String note) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            amountEt.setEnabled(true);
            Toast.makeText(PaymentActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPI_PAYMENT) {
            if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                if (data != null) {
                    String trxt = data.getStringExtra("response");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add(trxt);
                    upiPaymentDataOperation(dataList);
                } else {
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
            } else {
                ArrayList<String> dataList = new ArrayList<>();
                dataList.add("nothing");
                upiPaymentDataOperation(dataList);
            }
        }
    }

    private void upiPaymentDataOperation(final ArrayList<String> data) {
        if (isConnectionAvailable(PaymentActivity.this)) {
            String str = data.get(0);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String[] response = str.split("&");
            for (String s : response) {
                String[] equalStr = s.split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                } else {
                    amountEt.setEnabled(true);
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID).child("Personal Information");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (walletType.equals("Wallet")) {
                            int walletAmount = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("Wallets").child("Deposit Amount").getValue()).toString());
                            walletAmount = walletAmount+Integer.parseInt(amountEt.getText().toString());
                            databaseReference.child("Wallets").child("Deposit Amount").setValue(walletAmount);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");
                            String orderTime = dateFormat.format(new Date());
                            databaseReference.child("Wallets").child("Wallet History").child(orderTime).child("Name").setValue("Amount Added in Wallet.");
                            databaseReference.child("Wallets").child("Wallet History").child(orderTime).child("Amount").setValue("+"+Integer.parseInt(amountEt.getText().toString()));
                            databaseReference.child("Wallets").child("Wallet History").child(orderTime).child("Time").setValue(ServerValue.TIMESTAMP);
                        }
                        if (walletType.equals("Lifafa")){
                            double walletAmount = 0.0;
                            if (dataSnapshot.hasChild("Lifafa")){
                                walletAmount = Double.parseDouble(dataSnapshot.child("Lifafa").child("Wallet Amount").getValue().toString());
                            }
                            walletAmount = walletAmount+Integer.parseInt(amountEt.getText().toString());
                            databaseReference.child("Lifafa").child("Wallet Amount").setValue(walletAmount);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");
                            String orderTime = dateFormat.format(new Date());
                            databaseReference.child("Lifafa").child("Wallet History").child(orderTime).child("Name").setValue("Amount Added in Lifafa Wallet.");
                            databaseReference.child("Lifafa").child("Wallet History").child(orderTime).child("Amount").setValue("+"+Integer.parseInt(amountEt.getText().toString()));
                            databaseReference.child("Lifafa").child("Wallet History").child(orderTime).child("Time").setValue(ServerValue.TIMESTAMP);
                        }
                        amountEt.setEnabled(true);
                        Toast.makeText(PaymentActivity.this, "Your Transaction is succeed :)", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(PaymentActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                amountEt.setEnabled(true);
                Toast.makeText(PaymentActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else {
                amountEt.setEnabled(true);
                Toast.makeText(PaymentActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            amountEt.setEnabled(true);
            Toast.makeText(PaymentActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

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
}