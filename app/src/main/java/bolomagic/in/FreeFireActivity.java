package bolomagic.in;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import bolomagic.in.AdaptorAndParse.GameCardAdapter;
import bolomagic.in.AdaptorAndParse.GameCardParse;
import bolomagic.in.HashMap.GameCardOrder;

public class FreeFireActivity extends AppCompatActivity {

    ImageView iconImage;
    Button changeButton;
    TextView titleText, subtitleText, offerMessageText;
    String iconURL, title, subtitle;
    ListView listView;
    ArrayList<GameCardParse> gameCardParseArrayList = new ArrayList<>();
    GameCardAdapter gameCardAdapter;

    String upiID = "8077233199@paytm";

    int selectedPosition = -1;

    String UID = "DEFAULT";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_fire);

        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        iconImage = findViewById(R.id.iconImageView);
        titleText = findViewById(R.id.titleText);
        subtitleText = findViewById(R.id.subtitleText);
        changeButton = findViewById(R.id.changeButton);
        offerMessageText = findViewById(R.id.offerText);

        iconURL = getIntent().getStringExtra("icon_url");
        title = getIntent().getStringExtra("game_name");
        subtitle = getIntent().getStringExtra("game_developer");

        Picasso.get().load(iconURL).placeholder(R.drawable.loading).into(iconImage);
        titleText.setText(title);
        subtitleText.setText(subtitle);

        changeButton.setOnClickListener(v -> {
            Toast.makeText(FreeFireActivity.this, "Please select another one", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.button19).setOnClickListener(v -> {
            if (selectedPosition != -1){
                if (UID.equals("DEFAULT")){
                    Toast.makeText(FreeFireActivity.this, "Something went wrong with UID, Login Again !", Toast.LENGTH_SHORT).show();
                }else {
                    makePayment();
                }
            }else {
                Toast.makeText(FreeFireActivity.this, "Please select one of them !", Toast.LENGTH_SHORT).show();
            }
        });

        listView = findViewById(R.id.listView);
        gameCardAdapter = new GameCardAdapter(this, gameCardParseArrayList);
        listView.setAdapter(gameCardAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            listView.getChildAt(position).setBackgroundResource(R.drawable.alpha_blue_background_round);
            if (position != selectedPosition && selectedPosition != -1){
                listView.getChildAt(selectedPosition).setBackgroundResource(R.drawable.transparent_blue_background_round);
            }
            selectedPosition = position;
            TextView textView1 = findViewById(R.id.textView35);
            textView1.setText("₹ "+gameCardParseArrayList.get(position).getPrize()+" /-");
            TextView textView2 = findViewById(R.id.textView38);
            String quantity = gameCardParseArrayList.get(position).getQuantity();
            String offer = gameCardParseArrayList.get(position).getOfferPercent();
            int bonus = (Integer.parseInt(quantity)*Integer.parseInt(offer))/100;
            int totalQuantity = Integer.parseInt(quantity) + bonus;
            textView2.setText(totalQuantity+" "+gameCardParseArrayList.get(position).getUnitType());
        });

        loadUserData();
    }

    public void loadUserData() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
        UID = "0";
        if (mFirebaseUser != null) {
            UID = mFirebaseUser.getUid();
        }
        if (UID.equals("0")) {
            Toast.makeText(FreeFireActivity.this, "Environment is not cool !", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Application Details");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    upiID = snapshot.child("UPI ID").getValue().toString();
                    loadPrizeList(title);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(FreeFireActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void loadPrizeList(String gameName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SPL/Gift Cards/Game Cards/" + gameName);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Status") && Objects.requireNonNull(snapshot.child("Status").getValue()).toString().equals("Active")) {
                    String offerMsg = Objects.requireNonNull(snapshot.child("Offer Message").getValue()).toString();
                    offerMessageText.setText(offerMsg);
                    if (snapshot.hasChild("Cards")) {
                        Iterable<DataSnapshot> iterable = snapshot.child("Cards").getChildren();
                        for (DataSnapshot next : iterable) {
                            String id = next.getKey();
                            String offerPercent = Objects.requireNonNull(next.child("Offer Percent").getValue()).toString();
                            String prize = Objects.requireNonNull(next.child("Prize").getValue()).toString();
                            String quantity = Objects.requireNonNull(next.child("Quantity").getValue()).toString();
                            String unitType = Objects.requireNonNull(next.child("Unit Type").getValue()).toString();
                            String icon_url = Objects.requireNonNull(next.child("Icon URL").getValue()).toString();
                            gameCardParseArrayList.add(new GameCardParse(id,offerPercent,prize,quantity,unitType,icon_url));
                        }
                        gameCardAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(FreeFireActivity.this, "No Offer available !", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(FreeFireActivity.this, "Offer has been ended !", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FreeFireActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void makePayment(){
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String amount = gameCardParseArrayList.get(selectedPosition).getPrize();
        String note = "Payment for Game Card on Bolo Magic of rs. " + amount + "\nUID: " + UID+"\nName: "+name;
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiID)
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
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, 0);
        } else {
            Toast.makeText(FreeFireActivity.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                if (data != null) {
                    String txt = data.getStringExtra("response");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add(txt);
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
        if (isConnectionAvailable()) {
            String str = data.get(0);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String[] response = str.split("&");
            for (String s : response) {
                String[] equalStr = s.split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(FreeFireActivity.this, "Your Transaction is succeed :)", Toast.LENGTH_SHORT).show();
                processOrder();
            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(FreeFireActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FreeFireActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(FreeFireActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    protected void processOrder(){
        Toast.makeText(FreeFireActivity.this, "OrderPlaced", Toast.LENGTH_SHORT).show();
        SimpleDateFormat orderIDFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        SimpleDateFormat orderDateFormat = new SimpleDateFormat("dd-MM-yyy || HH:mm:ss");
        String orderID = orderIDFormat.format(new Date());
        String orderDate = orderDateFormat.format(new Date());
        String id = gameCardParseArrayList.get(selectedPosition).getId();
        String prize = gameCardParseArrayList.get(selectedPosition).getPrize();
        String quantity = gameCardParseArrayList.get(selectedPosition).getQuantity();
        String offer = gameCardParseArrayList.get(selectedPosition).getOfferPercent();
        String bonus = String.valueOf((Integer.parseInt(quantity)*Integer.parseInt(offer))/100);
        GameCardOrder gameCardOrder = new GameCardOrder(id, quantity, bonus, prize, "Pending", orderDate);
        Map<String, Object> orderValues = gameCardOrder.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(orderID, orderValues);
        FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID).child("Game Card History").updateChildren(childUpdates);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin");
        databaseReference.child("Game Card History").updateChildren(childUpdates);
        databaseReference.child("Game Card History").child(orderID).child("UID").setValue(UID);
        Toast.makeText(FreeFireActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
        finish();
    }

    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected()
                    && networkInfo.isConnectedOrConnecting()
                    && networkInfo.isAvailable();
        }
        return false;
    }
}