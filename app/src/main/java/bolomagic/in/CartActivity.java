package bolomagic.in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import bolomagic.in.AdaptorAndParse.cartAdapter;
import bolomagic.in.AdaptorAndParse.cartParse;

public class CartActivity extends AppCompatActivity {

    ListView cartListView;
    FirebaseAuth mAuth;
    String UID;
    ConstraintLayout emptyLayout;
    TextView statusTextView;
    ArrayList<cartParse> cartParseArrayList = new ArrayList<>();
    ArrayList<String> cartList = new ArrayList<>();
    double walletAmount = 0.0;
    double cartValue = 0.0;
    TextView textView1,textView2;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle("My Cart List");
        emptyLayout = findViewById(R.id.emptyLayout);
        statusTextView = findViewById(R.id.textView57);
        cartListView = findViewById(R.id.cartListView);
        textView1 = findViewById(R.id.textView46);
        textView2 = findViewById(R.id.textView48);
        button = findViewById(R.id.button);
        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();

        button.setOnClickListener(view -> {
            if (cartValue > walletAmount){
                new AlertDialog.Builder(CartActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("!! Low Balance !!")
                        .setMessage("You have not sufficient balance to place this order. Add Money to place this order.")
                        .setPositiveButton("Add Money", (dialogInterface, i) -> {
                            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                            intent.putExtra("Wallet Type","Wallet");
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel",null)
                        .setCancelable(true)
                        .show();
            }else {
                new AlertDialog.Builder(CartActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("!! Notice !!")
                        .setMessage("Do you want to place this order?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> placeOrder())
                        .setNegativeButton("No",null)
                        .setCancelable(true)
                        .show();
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID).child("Personal Information");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                walletAmount = Double.parseDouble(snapshot.child("Wallets").child("Deposit Amount").getValue().toString());
                textView2.setText("₹ "+walletAmount);
                cartList.clear();
                if (snapshot.hasChild("Cart")){
                    Iterable<DataSnapshot> snapshotIterator = snapshot.child("Cart").getChildren();
                    for (DataSnapshot next : snapshotIterator) {
                        cartList.add(next.getKey());
                    }
                    statusTextView.setText("Fetching Card Details...");
                    LoadCartProducts();
                }else {
                    emptyLayout.setVisibility(View.VISIBLE);
                    cartListView.setVisibility(View.GONE);
                    ImageView imageView = findViewById(R.id.imageView10);
                    imageView.setImageResource(R.drawable.pending_img);
                    findViewById(R.id.amountsLayout).setVisibility(View.GONE);
                    statusTextView.setText("Your cart list is empty ...!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                emptyLayout.setVisibility(View.VISIBLE);
                cartListView.setVisibility(View.GONE);
                findViewById(R.id.amountsLayout).setVisibility(View.GONE);
                ImageView imageView = findViewById(R.id.imageView10);
                imageView.setImageResource(R.drawable.pending_img);
                statusTextView.setText(error.toString());
            }
        });
    }

    public void LoadCartProducts(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Gift Cards");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Products")){
                    Iterable<DataSnapshot> dataSnapshotIterable = snapshot.child("Products").getChildren();
                    cartParseArrayList.clear();
                    cartValue = 0.0;
                    for (DataSnapshot next : dataSnapshotIterable) {
                        if (cartList.contains(next.getKey())){
                            String cardID = next.getKey();
                            String imageURL = next.child("Image URL").getValue().toString();
                            String prize = next.child("Prize").getValue().toString();
                            String cashBack = next.child("CashBack %").getValue().toString();
                            String effectivePrize = String.valueOf(Double.parseDouble(prize)-(Double.parseDouble(prize)*Double.parseDouble(cashBack))/100);
                            SimpleDateFormat s1 = new SimpleDateFormat("dd");
                            SimpleDateFormat s2 = new SimpleDateFormat("MM-yyy");
                            String t1 = s1.format(new Date());
                            int i = Integer.parseInt(t1)+7;
                            String t2 = s2.format(new Date());
                            String validity = i+"-"+t2;
                            cartValue = cartValue + Double.parseDouble(effectivePrize);
                            cartParseArrayList.add(new cartParse(cardID,imageURL,prize,cashBack,effectivePrize,validity));
                        }
                    }
                    cartAdapter cartAdapter = new cartAdapter(CartActivity.this, cartParseArrayList);
                    cartListView.setAdapter(cartAdapter);
                    textView1.setText("₹ "+cartValue);
                    button.setEnabled(true);
                    findViewById(R.id.amountsLayout).setVisibility(View.VISIBLE);
                    statusTextView.setText("Done");
                    ImageView imageView = findViewById(R.id.imageView10);
                    imageView.setImageResource(R.drawable.verified_img);
                    emptyLayout.setVisibility(View.GONE);
                    cartListView.setVisibility(View.VISIBLE);
                }else {
                    emptyLayout.setVisibility(View.VISIBLE);
                    cartListView.setVisibility(View.GONE);
                    findViewById(R.id.amountsLayout).setVisibility(View.GONE);
                    ImageView imageView = findViewById(R.id.imageView10);
                    imageView.setImageResource(R.drawable.pending_img);
                    statusTextView.setText("Gift Cards are not available.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ImageView imageView = findViewById(R.id.imageView10);
                imageView.setImageResource(R.drawable.pending_img);
                statusTextView.setText(error.toString());
                findViewById(R.id.amountsLayout).setVisibility(View.GONE);
            }
        });
    }

    public void placeOrder() {
        SimpleDateFormat orderFormat = new SimpleDateFormat("yyyMMddhhmmss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd hh:mm:ss a");
        String orderID = orderFormat.format(new Date());
        String orderTime = dateFormat.format(new Date());

        walletAmount = walletAmount - cartValue;

        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);

        userDatabaseReference.child("Personal Information").child("Wallets").child("Deposit Amount").setValue(walletAmount);
        userDatabaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(orderID).child("Name").setValue("Order Placed Successfully.");
        userDatabaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(orderID).child("Prize").setValue("-" + cartValue);
        userDatabaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(orderID).child("Time").setValue(ServerValue.TIMESTAMP);

        DatabaseReference adminDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Admin");
        userDatabaseReference.child("Gift Card History").child(orderID).child("Status").setValue("Pending");
        userDatabaseReference.child("Gift Card History").child(orderID).child("Time").setValue(orderTime);

        for (int i = 0; i < cartParseArrayList.size(); i++) {
            userDatabaseReference.child("Gift Card History").child(orderID).child("Cards").child(cartParseArrayList.get(i).getCardID()).child("Type").setValue(cartParseArrayList.get(i).getImageURL());
            userDatabaseReference.child("Gift Card History").child(orderID).child("Cards").child(cartParseArrayList.get(i).getCardID()).child("Prize").setValue(cartParseArrayList.get(i).getPrize());

            adminDatabaseReference.child("Gift Card History").child(orderID).child("Cards").child(cartParseArrayList.get(i).getCardID()).child("Type").setValue(cartParseArrayList.get(i).getImageURL());
            adminDatabaseReference.child("Gift Card History").child(orderID).child("Cards").child(cartParseArrayList.get(i).getCardID()).child("Prize").setValue(cartParseArrayList.get(i).getPrize());
        }
        Toast.makeText(this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}