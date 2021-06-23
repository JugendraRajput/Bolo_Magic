package bolomagic.in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import bolomagic.in.AdaptorAndParse.CardParse;
import bolomagic.in.AdaptorAndParse.CardsAdapter;

public class CardListActivity extends AppCompatActivity {

    ArrayList<CardParse> cardParses = new ArrayList<>();
    DatabaseReference databaseReference;

    FirebaseAuth mAuth;
    String UID = "";
    String LocalCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            UID = mFirebaseUser.getUid();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);

        LocalCategory = getIntent().getStringExtra("Local Category");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Gift Cards");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Products")){
                    Iterable<DataSnapshot> dataSnapshotIterable = snapshot.child("Products").getChildren();
                    for (DataSnapshot next : dataSnapshotIterable) {
                        if (next.child("Category").getValue().toString().equals(LocalCategory)){
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
                            String buttonText = "ADD";
                            cardParses.add(new CardParse(cardID,imageURL,prize,cashBack,effectivePrize,validity,buttonText));
                        }
                      }
                    CardsAdapter cardsAdapter = new CardsAdapter(CardListActivity.this, cardParses);
                    ListView listView = findViewById(R.id.listView);
                    listView.setAdapter(cardsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CardListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Info(View view) {
        String string = "How To Redeem"+"\n\t1. Download & Login into PayTm App." + "\n\t2. Go to Add Money.\n\t3. Goo to 'Have a PromoCode' and Enter the Code.\n\n" + "Where To Redeem"+"\n\tDownload & Login into PayTm App.\n\n" + "Terms"+"\n\t1. Download & Login into PayTm App.\n\t2. Go to Add Money." + "\n\t3. Go to 'Have a PromoCode' and Enter the Code.\n\t4. Valid for 7 from the date of issue of voucher.";
        new AlertDialog.Builder(CardListActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(string)
                .setCancelable(true)
                .show();
    }
    public void Cart(View view) {
        ConstraintLayout constraintLayout = (ConstraintLayout) view.getParent();
        TextView textView = (TextView) constraintLayout.findViewById(R.id.cardIDTextView);
        String id = textView.getText().toString();
        Button button = (Button) constraintLayout.findViewById(R.id.cartButton);
        String buttonText = button.getText().toString();
        if (buttonText.equals("ADD")){
            button.setText("REMOVE");
            button.setBackgroundResource(R.drawable.red_background);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");
            String time = simpleDateFormat.format(new Date());
            databaseReference.child("Personal Information").child("Cart").child(id).child("Time").setValue(time);
        }else {
            button.setText("ADD");
            button.setBackgroundResource(R.drawable.blue_background);
            databaseReference.child("Personal Information").child("Cart").child(id).removeValue();
        }
    }
}