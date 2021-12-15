package bolomagic.in;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlayQuizActivity extends AppCompatActivity {

    String quizID;

    ConstraintLayout loading_Layout, quiz_Layout, end_Layout;
    //Loading Layout Items
    ConstraintLayout custom_Profiles_Layout;
    ImageView loadingImageView;
    TextView titleTextView, messageTextView;
    Button reloadButton;
    String quizStatus = "Default";

    ArrayList<String> answeredQuestions = new ArrayList<>();
    ArrayList<String> questionsID = new ArrayList<>();
    ArrayList<QuizQuestions> quizQuestionsArrayList = new ArrayList<>();

    TextView questionTextView;
    Button buttonA, buttonB, buttonC, buttonD;
    String currentAnswer = "Default";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quiz);
        quizID = getIntent().getStringExtra("quizID");

        custom_Profiles_Layout = findViewById(R.id.custom_Profiles_Layout);
        loadingImageView = findViewById(R.id.imageView1);
        titleTextView = findViewById(R.id.textView1);
        messageTextView = findViewById(R.id.textView2);
        reloadButton = findViewById(R.id.button1);

        loading_Layout = findViewById(R.id.loading_Layout);
        quiz_Layout = findViewById(R.id.quiz_Layout);
        end_Layout = findViewById(R.id.end_Layout);

        questionTextView = findViewById(R.id.questionTextView);
        buttonA = findViewById(R.id.button11);
        buttonB = findViewById(R.id.button12);
        buttonC = findViewById(R.id.button13);
        buttonD = findViewById(R.id.button14);

        reloadButton.setOnClickListener(v -> {
            if (isConnectionAvailable(PlayQuizActivity.this)) {
                loadingImageView.setImageResource(R.drawable.loading);
                titleTextView.setText("Connecting...");
                messageTextView.setText("We are trying to connect with our server.");
                reloadButton.setVisibility(View.GONE);
                init();
            } else {
                loadingImageView.setImageResource(R.drawable.pending_img);
                titleTextView.setText("Connection not found");
                messageTextView.setText("It looks like that you are not connected to a active internet connection " +
                        "OR you have disabled internet usage permission for " + R.string.app_name);
                reloadButton.setVisibility(View.VISIBLE);
            }
        });

        if (isConnectionAvailable(this)) {
            init();
        } else {
            loadingImageView.setImageResource(R.drawable.pending_img);
            titleTextView.setText("Connection not found");
            messageTextView.setText("It looks like that you are not connected to a active internet connection " +
                    "OR you have disabled internet usage permission for " + R.string.app_name);
            reloadButton.setVisibility(View.VISIBLE);
        }
    }

    private void init() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference.child("SPL").child("Quiz").child(quizID);
        DatabaseReference finalDatabaseReference = databaseReference;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizStatus = snapshot.child("Status").getValue().toString();
                loadingImageView.setImageResource(R.drawable.verified_img);
                titleTextView.setText("Connection Successful");
                messageTextView.setText("You are connected with server.");
                if (snapshot.hasChild("Message")) {
                    messageTextView.setText(snapshot.child("Message").getValue().toString());
                }

                if (snapshot.hasChild("Questions")) {
                    loading_Layout.setVisibility(View.GONE);
                    quiz_Layout.setVisibility(View.VISIBLE);
                    Iterable<DataSnapshot> dataSnapshotIterable = snapshot.child("Questions").getChildren();
                    for (DataSnapshot next : dataSnapshotIterable) {
                        String questionID = next.getKey();
                        if (!questionsID.contains(questionID)) {
                            String question = next.child("Question").getValue().toString();
                            String answer = next.child("Answer").getValue().toString();
                            String optionA = next.child("Option A").getValue().toString();
                            String optionB = next.child("Option B").getValue().toString();
                            String optionC = next.child("Option C").getValue().toString();
                            String optionD = next.child("Option D").getValue().toString();
                            quizQuestionsArrayList.add(new QuizQuestions(questionID, question, optionA, optionB, optionC, optionD, answer));
                            questionsID.add(questionID);
                        }
                    }
                }
                if (quizStatus.equals("Ended")) {
                    loading_Layout.setVisibility(View.GONE);
                    quiz_Layout.setVisibility(View.GONE);
                    TextView textView76 = findViewById(R.id.textView76);
                    int myScore = 20;
                    textView76.setText(myScore + " points");
                    end_Layout.setVisibility(View.VISIBLE);
                    finalDatabaseReference.keepSynced(false);
                    findViewById(R.id.button17).setOnClickListener(v -> finish());
                }
                loadNextQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlayQuizActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadNextQuestion() {
        if (quizQuestionsArrayList.size() > 0) {
            for (int i = 0; i < quizQuestionsArrayList.size(); i++) {
                if (!answeredQuestions.contains(quizQuestionsArrayList.get(i).getQuestionID())) {
                    String question = quizQuestionsArrayList.get(i).getQuestion();
                    String optionA = quizQuestionsArrayList.get(i).getOptionA();
                    String optionB = quizQuestionsArrayList.get(i).getOptionB();
                    String optionC = quizQuestionsArrayList.get(i).getOptionC();
                    String optionD = quizQuestionsArrayList.get(i).getOptionD();
                    questionTextView.setText(question);
                    buttonA.setText(optionA);
                    buttonB.setText(optionB);
                    buttonC.setText(optionC);
                    buttonD.setText(optionD);
                    currentAnswer = quizQuestionsArrayList.get(i).getAnswer();
                    answeredQuestions.add(quizQuestionsArrayList.get(i).getQuestionID());
                    break;
                }
            }
        } else {
            Toast.makeText(this, "We are setting-up server...", Toast.LENGTH_SHORT).show();
        }
    }
}