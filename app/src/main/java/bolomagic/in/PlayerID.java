package bolomagic.in;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import static bolomagic.in.MainActivity.UID;
import static bolomagic.in.MainActivity.currentPosition;
import static bolomagic.in.MainActivity.homeListParseArrayList;

public class PlayerID extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_id);

        EditText editText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button1);

        button.setOnClickListener(v -> {
            String playerID = editText.getText().toString();
            if (!playerID.equals("")){
                String eventID = homeListParseArrayList.get(currentPosition).getEventID();
                FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID).child("Event Player IDs").child(eventID).child("Player ID").setValue(playerID).addOnCompleteListener(task -> {
                    Toast.makeText(PlayerID.this, "Player id has been successfully saved.", Toast.LENGTH_SHORT).show();
                    finish();
                });
                }else {
                Toast.makeText(PlayerID.this, "Please enter valid player id !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}