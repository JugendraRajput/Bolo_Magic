package bolomagic.in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import bolomagic.in.AdaptorAndParse.withdrawalMethodsAdapter;
import bolomagic.in.AdaptorAndParse.withdrawalMethodsParse;

import java.util.logging.Logger;

public class WithdrawActivity extends AppCompatActivity {

    String withdrawType = "NULL";
    int walletAmount = 0;
    TextView balanceTextView, noticeTextView;
    Button withdrawButton;
    FirebaseAuth mAuth;
    String UID;
    boolean isInstantWithdraw = false;

    ArrayList<withdrawalMethodsParse> withdrawalMethodsParseArrayList = new ArrayList<>();
    ListView listView;

    //Current Variables
    String withdrawMethod = "DEFAULT";
    String withdrawValue = "DEFAULT";
    
    //For Getting Details
    TextView textView1;
    EditText editText1;
    Button button1;

    EditText amountEditText;

    private static final Logger log = Logger.getLogger(WithdrawActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            UID = mFirebaseUser.getUid();
        }

        withdrawType = getIntent().getStringExtra("Withdraw Type");
        balanceTextView = findViewById(R.id.balanceTextView);
        noticeTextView = findViewById(R.id.noticeTextView);
        balanceTextView.setText("Winnings Balance : "+R.string.loading);
        listView = findViewById(R.id.withdrawalMethodsListView);
        withdrawButton = findViewById(R.id.withdrawButton);
        
        textView1 = findViewById(R.id.textView1);
        editText1 = findViewById(R.id.editText1);
        button1 = findViewById(R.id.button1);

        amountEditText = findViewById(R.id.editTextNumber);
        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noticeTextView.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        button1.setOnClickListener(v -> {
            String value = editText1.getText().toString();
            String hint = editText1.getHint().toString();
            if (value.equals("")){
                editText1.setError(hint);
                editText1.requestFocus();
            }else {
                if (hint.equals("Enter UPI ID")){
                    FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID)
                            .child("Withdrawal Methods").child("upi").setValue(value).addOnCompleteListener(task -> {
                                findViewById(R.id.entryLayout).setVisibility(View.GONE);
                                findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);
                            });
                }
                if (hint.equals("Enter Paytm no.")){
                    FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID)
                            .child("Withdrawal Methods").child("paytmWallet").setValue(value).addOnCompleteListener(task -> {
                                findViewById(R.id.entryLayout).setVisibility(View.GONE);
                                findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);
                            });
                }
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            try{
                for (int i=0;i<=withdrawalMethodsParseArrayList.size();i++){
                    if(position==i){
                        withdrawMethod = withdrawalMethodsParseArrayList.get(position).getMethodID();
                        withdrawValue = withdrawalMethodsParseArrayList.get(position).getMethodValue();
                        if (withdrawMethod.equals("upi") && withdrawValue.equals("Setup UPI")){
                            //Setup UPI of user
                            textView1.setText("Please provide your UPI ID for transfering your amount to your bank account");
                            editText1.setHint("Enter UPI ID");
                            editText1.setText("");
                            findViewById(R.id.mainLayout).setVisibility(View.GONE);
                            findViewById(R.id.entryLayout).setVisibility(View.VISIBLE);
                        }

                        if (withdrawMethod.equals("paytmWallet") && withdrawValue.equals("Setup PayTm no. for wallet")){
                            //Setup PayTm No. of user
                            textView1.setText("Please provide your Paytm no for transferring your amount to your paytm wallet");
                            editText1.setHint("Enter Paytm no.");
                            editText1.setText("");
                            findViewById(R.id.mainLayout).setVisibility(View.GONE);
                            findViewById(R.id.entryLayout).setVisibility(View.VISIBLE);
                        }
                        listView.getChildAt(i).setBackgroundResource(R.drawable.selected_listview_bg);
                    }else{
                        listView.getChildAt(i).setBackgroundResource(R.drawable.unselected_listview_bg);
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                withdrawalMethodsParseArrayList.clear();
                if (Objects.requireNonNull(snapshot.child("Security Information").child("Account Status").getValue()).toString().equals("GOOD")){
                    @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(WithdrawActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    if(snapshot.child("Security Information").child("Android ID").getValue().toString().equals(android_id)) {
                        if (withdrawType.equals("Wallet")){
                            walletAmount = Integer.parseInt(snapshot.child("Personal Information").child("Wallets").child("Wining Amount").getValue().toString());
                        }
                        if (withdrawType.equals("Lifafa")){
                            walletAmount = Integer.parseInt(snapshot.child("Personal Information").child("Lifafa").child("Wallet Amount").getValue().toString());
                        }
                        balanceTextView.setText("Winnings Balance : ₹ "+walletAmount);

                        if (walletAmount == 1){
                            noticeTextView.setText("Withdrawal amount ₹ 1");
                        }
                        if (walletAmount < 1){
                            noticeTextView.setText("You have not sufficient withdrawal amount !");
                        }
                        if (walletAmount > 1){
                            noticeTextView.setText("Withdrawable amount ₹ 1 to ₹ "+walletAmount);
                        }
                        noticeTextView.setVisibility(View.VISIBLE);

                        if (!snapshot.hasChild("Withdraw Requests")){
                            //For Now auto payments are not allowed. If this wil be launched in future then set isInstantWithdraw = true
                            isInstantWithdraw = false;
                        }else {
                            isInstantWithdraw = false;
                        }
                        if (snapshot.child("Withdrawal Methods").hasChild("upi")){
                            withdrawalMethodsParseArrayList.add(new withdrawalMethodsParse("upi",
                                    "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/UPI-Logo-vector.svg/1200px-UPI-Logo-vector.svg.png",
                                    "UPI",snapshot.child("Withdrawal Methods").child("upi").getValue().toString()));
                        }else {
                            withdrawalMethodsParseArrayList.add(new withdrawalMethodsParse("upi",
                                    "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/UPI-Logo-vector.svg/1200px-UPI-Logo-vector.svg.png",
                                    "UPI","Setup UPI"));
                        }
                        if (snapshot.child("Withdrawal Methods").hasChild("paytmWallet")){
                            withdrawalMethodsParseArrayList.add(new withdrawalMethodsParse("paytmWallet",
                                    "https://cdn.iconscout.com/icon/free/png-512/paytm-226448.png",
                                    "PayTm Wallet",snapshot.child("Withdrawal Methods").child("paytmWallet").getValue().toString()));
                        }else {
                            withdrawalMethodsParseArrayList.add(new withdrawalMethodsParse("paytmWallet",
                                    "https://cdn.iconscout.com/icon/free/png-512/paytm-226448.png",
                                    "PayTm Wallet","Setup PayTm no. for wallet"));
                        }

                        withdrawalMethodsAdapter withdrawalMethodsAdapter = new withdrawalMethodsAdapter(WithdrawActivity.this, withdrawalMethodsParseArrayList);
                        listView.setAdapter(withdrawalMethodsAdapter);
                    } else {
                        databaseReference.keepSynced(false);
                        new AlertDialog.Builder(WithdrawActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("!! Notice !!")
                                .setMessage("You have login from a different device with this account. Please use app in newly login device or login here -Again")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> finish())
                                .show();
                    }
                }else {
                    databaseReference.keepSynced(false);
                    new AlertDialog.Builder(WithdrawActivity.this)
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
                Toast.makeText(WithdrawActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        withdrawButton.setOnClickListener(v -> {
            noticeTextView.setVisibility(View.GONE);
            String editTextValue = amountEditText.getText().toString();
            if (editTextValue.equals("")){
                noticeTextView.setText("Enter amount to withdraw !");
                noticeTextView.setVisibility(View.VISIBLE);
            }else {
                if (withdrawMethod.equals("DEFAULT")){
                    listView.requestFocus();
                    Toast.makeText(WithdrawActivity.this, "Please select withdraw method !", Toast.LENGTH_SHORT).show();
                }else {
                    int withdrawAmount = Integer.parseInt(editTextValue);
                    if (withdrawAmount == 0){
                        noticeTextView.setText("You are not able to withdraw ₹ 0");
                        noticeTextView.setVisibility(View.VISIBLE);
                    }else {
                        if (isInstantWithdraw){
                            if (walletAmount >= 5){
                                if (withdrawAmount <= 5){
                                    //Start first withdraw
                                    Toast.makeText(this, "InstantWithdraw not allowed right now !", Toast.LENGTH_SHORT).show();
                                }else {
                                    noticeTextView.setText("First withdraw can be ₹ 5 OR less !");
                                    noticeTextView.setVisibility(View.VISIBLE);
                                }
                            }else {
                                noticeTextView.setText("You have not sufficient balance to withdraw !");
                                noticeTextView.setVisibility(View.VISIBLE);
                            }
                        }else {
                            if (withdrawAmount <= walletAmount){
                                withdrawHandler(withdrawAmount, amountEditText);
                            }else {
                                noticeTextView.setText("You have not sufficient balance to withdraw !");
                                noticeTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });
    }

    public void withdrawHandler(int withdrawAmount, EditText editText){
        new AlertDialog.Builder(WithdrawActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("!! Notice !!")
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyMMddhhmmss");
                    String withdrawID = simpleDateFormat.format(new Date());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Withdraw Requests");
                    databaseReference.child(withdrawID).child("Withdraw Amount").setValue(withdrawAmount);
                    databaseReference.child(withdrawID).child("UID").setValue(UID);
                    databaseReference.child(withdrawID).child("Withdraw Method").setValue(withdrawMethod);
                    databaseReference.child(withdrawID).child("Method Value").setValue(withdrawValue);
                    databaseReference.child(withdrawID).child("Withdraw Type").setValue(withdrawType);
                    databaseReference.child(withdrawID).child("Withdraw Time").setValue(ServerValue.TIMESTAMP);
                    databaseReference.child(withdrawID).child("Status").setValue("Pending");
                    DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);
                    if (withdrawType.equals("Wallet")){
                        userDatabaseReference.child("Personal Information").child("Wallets").child("Wining Amount").setValue(walletAmount-withdrawAmount);
                    }
                    if (withdrawType.equals("Lifafa")){
                        userDatabaseReference.child("Personal Information").child("Lifafa").child("Wallet Amount").setValue(walletAmount-withdrawAmount);
                    }
                    userDatabaseReference.child("Withdraw Requests").child(withdrawID).child("Withdraw Time").setValue(ServerValue.TIMESTAMP);
                    editText.setText("");
                    Toast.makeText(WithdrawActivity.this, "Request Successful...!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel",null)
                .show();
    }
}