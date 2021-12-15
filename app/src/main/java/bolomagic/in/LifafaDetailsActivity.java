package bolomagic.in;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LifafaDetailsActivity extends AppCompatActivity {

    String lifafaID = "Default";
    String lifafaType = "Default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setTitle("");
        }
        setContentView(R.layout.activity_lifafa_details);
        lifafaType = getIntent().getStringExtra("Lifafa Type");
        lifafaID = getIntent().getStringExtra("Lifafa ID");

        ImageView mainImageView = findViewById(R.id.imageView1);
        ImageView themeImageView = findViewById(R.id.imageView2);

        TextView senderNameTextView = findViewById(R.id.textView1);
        TextView messageTextView = findViewById(R.id.textView2);
        TextView amountTextView = findViewById(R.id.textView3);
        TextView dateTextView = findViewById(R.id.textView5);
        TextView quantityTextView = findViewById(R.id.textView6);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Lifafa").child(lifafaID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Status")) {
                    String status = snapshot.child("Status").getValue().toString();
                    String senderName = snapshot.child("Status").getValue().toString();
                    String message = snapshot.child("Status").getValue().toString();
                    String amountReceived = snapshot.child("Status").getValue().toString();
                    String maxAmount = snapshot.child("Status").getValue().toString();
                    String date = snapshot.child("Status").getValue().toString();
                    String maxReceiver = snapshot.child("Status").getValue().toString();
                    String receivedBy = snapshot.child("Status").getValue().toString();
                    if (lifafaType.equals("Received")) {
                        if (status.equals("Completed")) {
                            quantityTextView.setVisibility(View.VISIBLE);
                            quantityTextView.setClickable(false);
                            quantityTextView.setText("This lifafa has been completed.");
                        }
                    }
                    if (lifafaType.equals("Sent")) {
                        quantityTextView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LifafaDetailsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lifafa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        if (item.getItemId() == R.id.action_share) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Share your lifafa with friends");
            intent.setType("text/plain");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void LoadReceiverUsers(View view) {
        //Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
    }
}