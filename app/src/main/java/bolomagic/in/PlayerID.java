package bolomagic.in;

import static bolomagic.in.MainActivity.UID;
import static bolomagic.in.MainActivity.currentPosition;
import static bolomagic.in.MainActivity.homeListParseArrayList;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlayerID extends AppCompatActivity {

    String gameName = "Default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_id);
        setTitle("One Time Process");

        EditText editText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button1);
        try {
            gameName = getIntent().getStringExtra("game_name");
        }catch (Exception e){
            Log.d("Normal",e.toString());
        }

        button.setOnClickListener(v -> {
            String playerID = editText.getText().toString();
            if (!playerID.equals("")) {
                if (gameName.equals("Default")){
                    String eventID = homeListParseArrayList.get(currentPosition).getEventID();
                    FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID).child("Event Player IDs").child(eventID).child("Player ID").setValue(playerID).addOnCompleteListener(task -> {
                        Toast.makeText(PlayerID.this, "Player id has been successfully saved.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SPL/Gift Cards/Game Cards/" + gameName+"/Player ID");
                    databaseReference.child(UID).child("Player ID").setValue(playerID).addOnCompleteListener(task -> {
                        Toast.makeText(PlayerID.this, "Player id has been successfully saved.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
//                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            boolean saveID = true;
//                            if (snapshot.hasChildren()){
//                                Iterable<DataSnapshot> iterable = snapshot.getChildren();
//                                for (DataSnapshot next : iterable){
//                                    String s_playerID = next.child("Player ID").getValue().toString();
//                                    if (s_playerID.equals(playerID)){
//                                        saveID = false;
//                                    }
//                                }
//                            }
//
//                            if (saveID){
//                                databaseReference.child(UID).child("Player ID").setValue(playerID).addOnCompleteListener(task -> {
//                                    Toast.makeText(PlayerID.this, "Player id has been successfully saved.", Toast.LENGTH_SHORT).show();
//                                    finish();
//                                });
//                            }else {
//                                Toast.makeText(PlayerID.this, "This ID already saved with us.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Toast.makeText(PlayerID.this, error.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    });

                }
            } else {
                Toast.makeText(PlayerID.this, "Please enter valid player id !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}