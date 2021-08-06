package bolomagic.in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import bolomagic.in.AdaptorAndParse.LifafaCreatedHistoryAdapter;
import bolomagic.in.AdaptorAndParse.LifafaCreatedHistoryParse;

public class LifafaCreatedActivity extends AppCompatActivity {

    ListView LifafaCreatedListView;
    String UID = "DEFAULT";
    FirebaseAuth mAuth;
    int totalCreation = 0;
    ArrayList<LifafaCreatedHistoryParse> lifafaCreatedHistoryParseArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            setContentView(R.layout.activity_lifafa_created);
            setTitle("Creation History");
            UID = mAuth.getCurrentUser().getUid();
            LifafaCreatedListView = findViewById(R.id.LifafaCreatedListView);
            Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605114138/loading_bp9ico.png").into((ImageView) findViewById(R.id.LifafaCreatedImageView));
            //LoadLifafaCreatedHistory();
        } else {
            finish();
            startActivity(new Intent(LifafaCreatedActivity.this, AuthActivity.class));
        }
    }
/*
    public void LoadLifafaCreatedHistory(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Lifafa");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                for (DataSnapshot next : snapshotIterator) {
                    if (next.child("Sender ID").getValue().toString().equals(UID)){
                        totalCreation = totalCreation + 1;
                        String ID = next.getKey();
                        String receiverName = next.child("Receiver Name").getValue().toString();
                        String availableAmount = next.child("Available Amount").getValue().toString();
                        String totalBalance = next.child("Max Amount").getValue().toString();
                        String status = next.child("Status").getValue().toString();
                        String link = next.child("Link").getValue().toString();
                        String creationTime = next.child("Created On").child("hh").getValue().toString()+
                                ":"+next.child("Created On").child("mm").getValue().toString()+
                                " || "+next.child("Created On").child("Date").getValue().toString()+
                                "/"+next.child("Created On").child("Month").getValue().toString()+
                                "/"+next.child("Created On").child("Year").getValue().toString();
                        lifafaCreatedHistoryParseArrayList.add(new LifafaCreatedHistoryParse(ID,receiverName,creationTime,totalBalance,availableAmount,status,link));
                    }
                }
                if (totalCreation == 0){
                    Picasso.get().load("https://res.cloudinary.com/dsznqkutd/image/upload/v1605424833/imageedit_9_7876917501_kdsobm.png").into((ImageView) findViewById(R.id.LifafaCreatedImageView));
                }else {
                    LifafaCreatedHistoryAdapter lifafaCreatedHistoryAdapter = new LifafaCreatedHistoryAdapter(LifafaCreatedActivity.this, lifafaCreatedHistoryParseArrayList);
                    LifafaCreatedListView.setAdapter(lifafaCreatedHistoryAdapter);
                    findViewById(R.id.LifafaCreatedListView).setVisibility(View.VISIBLE);
                    findViewById(R.id.LifafaCreatedImageView).setVisibility(View.GONE);
                    LifafaCreatedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,  lifafaCreatedHistoryParseArrayList.get(position).getLink());
                            intent.setType("text/plain");
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LifafaCreatedActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

*/
}