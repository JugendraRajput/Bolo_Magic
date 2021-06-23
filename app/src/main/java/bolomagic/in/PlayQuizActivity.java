package bolomagic.in;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayQuizActivity extends AppCompatActivity {

    String quizType;

    //Loading Layout Items
    ConstraintLayout custom_Profiles_Layout;
    ImageView loadingImageView;
    TextView titleTextView, messageTextView;
    Button reloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quiz);
        quizType = getIntent().getStringExtra("Quiz Type");

        custom_Profiles_Layout = findViewById(R.id.custom_Profiles_Layout);
        loadingImageView = findViewById(R.id.imageView1);
        titleTextView = findViewById(R.id.textView1);
        messageTextView = findViewById(R.id.textView2);
        reloadButton = findViewById(R.id.button1);

        if (quizType.equals("1v1")){
            custom_Profiles_Layout.setVisibility(View.VISIBLE);
        }

        reloadButton.setOnClickListener(v -> {
            if (isConnectionAvailable(PlayQuizActivity.this)){
                loadingImageView.setBackgroundResource(R.drawable.loading);
                titleTextView.setText("Connecting...");
                messageTextView.setText("We are trying to connect with our server.");
                reloadButton.setVisibility(View.GONE);
                init();
            }else {
                loadingImageView.setBackgroundResource(R.drawable.pending_img);
                titleTextView.setText("Connection not found");
                messageTextView.setText("It looks like that you are not connected to a active internet connection " +
                        "OR you have disabled internet usage permission for "+R.string.app_name);
                reloadButton.setVisibility(View.VISIBLE);
            }
        });

        if (isConnectionAvailable(this)){
            init();
        }else {
            loadingImageView.setBackgroundResource(R.drawable.pending_img);
            titleTextView.setText("Connection not found");
            messageTextView.setText("It looks like that you are not connected to a active internet connection " +
                    "OR you have disabled internet usage permission for "+R.string.app_name);
            reloadButton.setVisibility(View.VISIBLE);

            /*
            new AlertDialog.Builder(PlayQuizActivity.this)
                    .setIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setTitle("!! Notice !!")
                    .setMessage("Network Issue")
                    .setNegativeButton("Exit", (dialogInterface, i) -> finish())
                    .show();
             */
        }
    }

    private void init(){
        Toast.makeText(this, "Connected for "+quizType, Toast.LENGTH_SHORT).show();
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected()
                    && networkInfo.isConnectedOrConnecting()
                    && networkInfo.isAvailable();
        }
        return false;
    }
}