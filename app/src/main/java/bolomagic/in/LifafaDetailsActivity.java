package bolomagic.in;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class LifafaDetailsActivity extends AppCompatActivity {

    String lifafaID = "Default";
    String lifafaType = "Default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        ActionBar actionBar = getSupportActionBar();
        lifafaType = getIntent().getStringExtra("Lifafa Type");
        lifafaID = getIntent().getStringExtra("Lifafa ID");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setTitle("");
        }
        setContentView(R.layout.activity_lifafa_details);

        if (lifafaType.equals("Received")){
            //Lifafa Received
        }
        if (lifafaType.equals("Sent")){
            //Lifafa Sent
        }
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
            intent.putExtra(Intent.EXTRA_TEXT,  "Share your lifafa with friends");
            intent.setType("text/plain");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void LoadReceiverUsers(View view){
        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
    }
}