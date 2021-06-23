package bolomagic.in;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window mWindow = getWindow();
        mWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (isConnectionAvailable(this)){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Application Details");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot snapshot) {
                    if (snapshot.hasChild("Banned Applications")) {
                        ArrayList<String> bannedApps = new ArrayList<>();
                        Iterable<DataSnapshot> dataSnapshotIterable = snapshot.child("Banned Applications").getChildren();
                        for (DataSnapshot next : dataSnapshotIterable) {
                            bannedApps.add(Objects.requireNonNull(next.child("Package").getValue()).toString());
                        }
                        if (!installedApps(bannedApps)){
                            if (Integer.parseInt(Objects.requireNonNull(snapshot.child("Version Code").getValue()).toString()) == BuildConfig.VERSION_CODE){
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                if (firebaseAuth.getCurrentUser() != null){
                                    Continue();
                                }else {
                                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                                    finish();
                                }
                            }else {
                                new AlertDialog.Builder(SplashActivity.this)
                                        .setIcon(android.R.drawable.ic_lock_idle_alarm)
                                        .setTitle("Update Available")
                                        .setMessage(Objects.requireNonNull(snapshot.child("Update Message").getValue()).toString())
                                        .setPositiveButton("Update", (dialog, which) -> {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Objects.requireNonNull(snapshot.child("Website URL").getValue()).toString()));
                                            startActivity(intent);
                                            finish();
                                        })
                                        .setNegativeButton("Exit", (dialogInterface, i) -> finish())
                                        .show();
                            }
                        }else {
                            ShowToast("Security Manager does not allow you to open the app...!");
                            finish();
                        }
                    }else {
                        ShowToast("Security Manager unable to fetch data from server...!");
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ShowToast(error.toString());
                    finish();
                }
            });
        }else {
            ShowToast("Network not available...!");
            finish();
        }
    }

    public void Continue(){
        String s = "https://www.bolomagic.in/lifafa/id=";
        int i = s.length();
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Uri deepLink;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                        String LifafaID = String.valueOf(deepLink);
                        LifafaID = LifafaID.substring(i);
                        Intent intent = new Intent(SplashActivity.this, LifafaActivity.class);
                        intent.putExtra("Lifafa ID",LifafaID);
                        startActivity(intent);
                    }else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                    finish();
                })
                .addOnFailureListener(this, e -> ShowToast(e.toString()));
    }

    public void ShowToast(String errorMessage){
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private boolean installedApps(ArrayList<String> bannedAppsList) {
        boolean returnStatus = false;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R){
            List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packageInfoList.size(); i++) {
                PackageInfo packageInfo = packageInfoList.get(i);
                String packageName = packageInfo.applicationInfo.packageName;
                if (bannedAppsList.contains(packageName)){
                    returnStatus = true;
                    break;
                }
            }
        }

        return returnStatus;
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