package bolomagic.in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bolomagic.in.AdaptorAndParse.CardHistoryAdapter;
import bolomagic.in.AdaptorAndParse.CardHistoryParse;

public class CardHistoryActivity extends AppCompatActivity {

    String UID = "DEFAULT";
    ListView listView;
    ArrayList<CardHistoryParse> cardHistoryParseArrayList = new ArrayList<>();
    String loadingImageURL = "https://res.cloudinary.com/dsznqkutd/image/upload/v1605114138/loading_bp9ico.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            setContentView(R.layout.activity_card_history);
            setTitle("Gift Card History");
            UID = mAuth.getCurrentUser().getUid();
            listView = findViewById(R.id.listView);
            Picasso.get().load(loadingImageURL).into((ImageView) findViewById(R.id.imageView16));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    view.animate().rotationX(90).setDuration(400).alpha(0).withEndAction(() ->{
                        view.animate().rotationX(0).setDuration(400).alpha(1).start();
                    });
                }
            });
            LoadHistory();
        }else {
            finish();
            startActivity(new Intent(CardHistoryActivity.this, AuthActivity.class));
        }
    }

    public void LoadHistory(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Gift Card History")){
                    Iterable<DataSnapshot> snapshotIterator = snapshot.child("Gift Card History").getChildren();
                    for (DataSnapshot next : snapshotIterator) {
                        String imageURL = loadingImageURL;
                        int prize = 0;
                        Iterable<DataSnapshot> snapshotIterable = next.child("Cards").getChildren();
                        for (DataSnapshot innerNext : snapshotIterable){
                            prize = prize + Integer.parseInt(innerNext.child("Prize").getValue().toString());
                            if (next.child("Cards").getChildrenCount() == 1){
                                imageURL = innerNext.child("Type").getValue().toString();
                            }
                        }
                        String status = next.child("Status").getValue().toString();
                        String date = next.child("Time").getValue().toString();
                        String code = "Default";
                        if (next.hasChild("Code")){
                            code = next.child("Code").getValue().toString();
                        }
                        cardHistoryParseArrayList.add(new CardHistoryParse(imageURL,String.valueOf(prize),status,date,code));
                    }
                    CardHistoryAdapter cardHistoryAdapter = new CardHistoryAdapter(CardHistoryActivity.this, cardHistoryParseArrayList);
                    listView.setAdapter(cardHistoryAdapter);
                    listView.setVisibility(View.VISIBLE);
                    findViewById(R.id.imageView16).setVisibility(View.GONE);
                }else {
                    Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605424833/imageedit_9_7876917501_kdsobm.png").into((ImageView) findViewById(R.id.imageView16));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CardHistoryActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}