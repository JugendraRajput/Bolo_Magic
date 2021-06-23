package bolomagic.in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import bolomagic.in.AdaptorAndParse.LifafaReceivedHistoryAdapter;
import bolomagic.in.AdaptorAndParse.LifafaReceivedHistoryParse;

public class LifafaReceivedActivity extends AppCompatActivity {

    ListView LifafaReceivedListView;
    String UID = "DEFAULT";
    FirebaseAuth mAuth;
    int totalReceive = 0;
    ArrayList<LifafaReceivedHistoryParse> lifafaReceivedHistoryParseArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            setContentView(R.layout.activity_lifafa_received);
            setTitle("Receive History");
            UID = mAuth.getCurrentUser().getUid();
            LifafaReceivedListView = findViewById(R.id.LifafaReceivedListView);
            Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605114138/loading_bp9ico.png").into((ImageView) findViewById(R.id.LifafaReceivedImageView));
            LoadLifafaReceivedHistory();
        }else {
            finish();
            startActivity(new Intent(LifafaReceivedActivity.this, AuthActivity.class));
        }
    }

    public void LoadLifafaReceivedHistory(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Lifafa");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                for (DataSnapshot next : snapshotIterator) {
                    if (next.hasChild("Received By")){
                        if (next.child("Received By").hasChild(UID)){
                            totalReceive = totalReceive + 1;
                            String ID = next.getKey();
                            String receiverName = next.child("Receiver Name").getValue().toString();
                            String receivedOn = next.child("Received By").child(UID).child("Received On").getValue().toString();
                            String won = next.child("Received By").child(UID).child("Amount Received").getValue().toString();
                            String status = next.child("Status").getValue().toString();
                            lifafaReceivedHistoryParseArrayList.add(new LifafaReceivedHistoryParse(ID,receiverName,receivedOn,won,status));
                        }
                    }
                }
                if (totalReceive == 0){
                    Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605424833/imageedit_9_7876917501_kdsobm.png").into((ImageView) findViewById(R.id.LifafaReceivedImageView));
                }else {
                    LifafaReceivedHistoryAdapter lifafaReceivedHistoryAdapter = new LifafaReceivedHistoryAdapter(LifafaReceivedActivity.this, lifafaReceivedHistoryParseArrayList);
                    LifafaReceivedListView.setAdapter(lifafaReceivedHistoryAdapter);
                    findViewById(R.id.LifafaReceivedListView).setVisibility(View.VISIBLE);
                    findViewById(R.id.LifafaReceivedImageView).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LifafaReceivedActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}