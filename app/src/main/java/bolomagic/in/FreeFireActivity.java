package bolomagic.in;

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

import java.util.Objects;

public class FreeFireActivity extends AppCompatActivity {

    ImageView iconImage;
    Button changeButton;
    TextView titleText, subtitleText, offerMessageText;
    String iconURL, title, subtitle;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_fire);

        iconImage = findViewById(R.id.iconImageView);
        titleText = findViewById(R.id.titleText);
        subtitleText = findViewById(R.id.subtitleText);
        changeButton = findViewById(R.id.changeButton);
        offerMessageText = findViewById(R.id.offerText);

        listView = findViewById(R.id.listView);

        iconURL = getIntent().getStringExtra("icon_url");
        title = getIntent().getStringExtra("game_name");
        subtitle = getIntent().getStringExtra("game_developer");

        Picasso.get().load(iconURL).placeholder(R.drawable.loading).into(iconImage);
        titleText.setText(title);
        subtitleText.setText(subtitle);

        loadUserData();
    }

    public void loadUserData() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
        String UID = "0";
        if (mFirebaseUser != null) {
            UID = mFirebaseUser.getUid();
        }
        if (UID.equals("0")) {
            Toast.makeText(FreeFireActivity.this, "Environment is not cool !", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    loadPrizeList(title);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

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
                    if (snapshot.hasChild("Cards")) {
                        Iterable<DataSnapshot> iterable = snapshot.child("Cards").getChildren();
                        for (DataSnapshot next : iterable) {
                            String id = next.getKey();
                            String imgURL = Objects.requireNonNull(next.child("Image URL").getValue()).toString();
                            String title = Objects.requireNonNull(next.child("Title").getValue()).toString();
                            String quantity = Objects.requireNonNull(next.child("quantity").getValue()).toString();
                            String prize = Objects.requireNonNull(next.child("Prize").getValue()).toString();

                        }
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

            }
        });
    }
}