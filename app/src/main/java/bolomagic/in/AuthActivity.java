package bolomagic.in;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;

public class AuthActivity extends AppCompatActivity {

    //a constant for detecting the login intent result
    private static final int RC_SIGN_IN = 234;
    ConstraintLayout checkingUpdatesLayout;
    Button loginButton;
    FirebaseAuth mAuth;
    String UID;
    //creating a GoogleSignInClient object
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window mWindow = getWindow();
        mWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();

        checkingUpdatesLayout = findViewById(R.id.checkingUpdatesLayout);
        loginButton = findViewById(R.id.loginButton);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(AuthActivity.this, gso);

        loginButton.setTranslationY(300);
        loginButton.setVisibility(View.VISIBLE);
        loginButton.animate().translationY(0).setDuration(750).start();
        loginButton.setOnClickListener(view -> signIn());
    }

    //this method is called on click
    private void signIn() {
        //getting the google SignIn intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Toast.makeText(AuthActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loginButton.animate().translationY(300).setDuration(200).start();
                            checkingUpdatesLayout.setTranslationY(300);
                            checkingUpdatesLayout.setVisibility(View.VISIBLE);
                            checkingUpdatesLayout.animate().translationY(0).setDuration(200).start();
                            UserData();
                        } else {
                            Toast.makeText(AuthActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void UserData() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            UID = mFirebaseUser.getUid();
        } else {
            System.exit(0);
        }
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(AuthActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (dataSnapshot.hasChild("Personal Information")) {
                    if (Objects.requireNonNull(dataSnapshot.child("Security Information").child("Account Status").getValue()).toString().equals("GOOD")) {
                        databaseReference.child("Security Information").child("Android ID").setValue(android_id);
                        databaseReference.child("Personal Information").child("Application Status").setValue("Installed");
                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(AuthActivity.this, "Your Account has been blocked by Admin :)", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Random r = new Random();
                    int d1 = r.nextInt(9);
                    int d2 = r.nextInt(9);
                    int d3 = r.nextInt(9);
                    int d4 = r.nextInt(9);
                    while (d1 == d2 || d1 == d3 || d1 == d4 || d2 == d3 || d2 == d4 || d3 == d4) {
                        if (d1 == d2 || d2 == d3 || d2 == d4) {
                            d2 = r.nextInt(9);
                        }
                        if (d1 == d3 || d2 == d3 || d3 == d4) {
                            d3 = r.nextInt(9);
                        }
                        if (d1 == d4 || d2 == d4 || d3 == d4) {
                            d4 = r.nextInt(9);
                        }
                    }
                    String halfName = Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName()).substring(0, 2);
                    String referCode = halfName.toUpperCase() + d1 + d2 + d3 + d4;
                    int tempInt = r.nextInt(9);
                    String d5 = String.valueOf(tempInt);
                    referCode = referCode.replace(" ", d5);
                    databaseReference.child("Personal Information").child("Name").setValue(mAuth.getCurrentUser().getDisplayName());
                    databaseReference.child("Personal Information").child("Email").setValue(mAuth.getCurrentUser().getEmail());
                    databaseReference.child("Personal Information").child("Profile Picture").setValue(Objects.requireNonNull(mAuth.getCurrentUser().getPhotoUrl()).toString());
                    databaseReference.child("Personal Information").child("Wallets").child("Wining Amount").setValue(0);
                    databaseReference.child("Personal Information").child("Wallets").child("Deposit Amount").setValue(0);
                    databaseReference.child("Personal Information").child("Wallets").child("Bonus Amount").setValue(0);
                    databaseReference.child("Personal Information").child("Refer Details").child("Friend Refer Code").setValue("NEVER_USE");
                    databaseReference.child("Personal Information").child("Refer Details").child("Refer Code").setValue(referCode);
                    databaseReference.child("Personal Information").child("Registered On").setValue(ServerValue.TIMESTAMP);
                    databaseReference.child("Personal Information").child("Application Status").setValue("Installed");
                    databaseReference.child("Security Information").child("Android ID").setValue(android_id);
                    databaseReference.child("Security Information").child("Account Status").setValue("GOOD");
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AuthActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
