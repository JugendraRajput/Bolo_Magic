package bolomagic.in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import bolomagic.in.AdaptorAndParse.FeaturedBannersAdapter;
import bolomagic.in.AdaptorAndParse.FeaturedBannersParse;
import bolomagic.in.AdaptorAndParse.HomeListAdapter;
import bolomagic.in.AdaptorAndParse.HomeListParse;
import bolomagic.in.AdaptorAndParse.QuizListAdaptor;
import bolomagic.in.AdaptorAndParse.QuizListParse;
import bolomagic.in.CustomDialog.CustomDialogHome;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout HomeScreen,ProfileScreen;
    ImageView profilePicImageView;
    TextView userNameTextView,userEmailTextView,userWithdrawTextView,userDepositTextView,userReferTextView;
    Button withdrawButton,depositButton,referButton;

    FirebaseAuth mAuth;
    String Name,Email,WiningAmount,BonusAmount,ProfilePicture;
    public static String DepositAmount;
    public static String UID;

    ShimmerFrameLayout homeShimmerViewContainer;

    public static ArrayList<HomeListParse> homeListParseArrayList = new ArrayList<>();
    public static HomeListAdapter homeListAdapter;

    public static ArrayList<QuizListParse> quizListParseArrayList = new ArrayList<>();
    public static QuizListAdaptor quizListAdaptor;

    ListView listView;

    public static boolean isNewUser = false;

    public static DataSnapshot userDataSnapshot;

    public static int currentPosition = 0;

    public static String currentEventPlayerID = "Default";

    BottomNavigationView bottomNavigationView;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        HomeScreen = findViewById(R.id.HomeScreen);
        ProfileScreen = findViewById(R.id.ProfileScreen);

        profilePicImageView = findViewById(R.id.profilePicImageView);
        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);

        userWithdrawTextView = findViewById(R.id.textView5);
        userDepositTextView = findViewById(R.id.textView7);
        userReferTextView = findViewById(R.id.textView9);

        withdrawButton = findViewById(R.id.button1);
        depositButton = findViewById(R.id.button2);
        referButton = findViewById(R.id.button3);

        listView = findViewById(R.id.listView);
        tabLayout = findViewById(R.id.tabLayout);
        loadTabLayout();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            UID = mFirebaseUser.getUid();
        }

        homeShimmerViewContainer = findViewById(R.id.homeShimmerViewContainer);
        homeShimmerViewContainer.setAutoStart(true);

        FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID)
                .child("Personal Information").child("Last Active").setValue(ServerValue.TIMESTAMP);

        final RecyclerView FeaturedBannersRecyclerView = findViewById(R.id.recyclerView);
        List<FeaturedBannersParse> featuredBannersParses = new ArrayList<>();
        FeaturedBannersAdapter featuredBannersAdapter = new FeaturedBannersAdapter(featuredBannersParses);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        FeaturedBannersRecyclerView.setLayoutManager(linearLayoutManager);
        FeaturedBannersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        FeaturedBannersRecyclerView.setAdapter(featuredBannersAdapter);
        ItemClickSupport.addTo(FeaturedBannersRecyclerView)
                .setOnItemClickListener((recyclerView, i, v) -> {
                    String productID = featuredBannersParses.get(i).getProductID();
                    if (productID.equals("REFER")) {
                        startActivity(new Intent(MainActivity.this,ReferActivity.class));
                    }
                    if (productID.equals("Gift Card")) {
                        startActivity(new Intent(MainActivity.this,CardCategoryActivity.class));
                    }
                    if (productID.equals("LIFAFA")) {
                        Intent intent = new Intent(MainActivity.this, LifafaActivity.class);
                        intent.putExtra("Lifafa ID","DEFAULT");
                        startActivity(intent);
                    }
                });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (listView.getAdapter().equals(homeListAdapter)){
                currentPosition = position;
                CustomDialogHome customDialogHome = new CustomDialogHome(this);
                customDialogHome.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(customDialogHome.getWindow().getAttributes());
                layoutParams.gravity = Gravity.BOTTOM;
                customDialogHome.getWindow().setAttributes(layoutParams);
                customDialogHome.show();
            }else {
                Toast.makeText(this, quizListParseArrayList.get(position).getQuizName(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference databaseReferenceBeta = FirebaseDatabase.getInstance().getReference().child("SPL").child("Events");
        databaseReferenceBeta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FeaturedBannersParse featuredBannersParse1 = new FeaturedBannersParse("https://gos3.ibcdn.com/img-1587107388.jpg","REFER");
                featuredBannersParses.add(featuredBannersParse1);
                FeaturedBannersParse featuredBannersParse2 = new FeaturedBannersParse("https://res.cloudinary.com/dsznqkutd/image/upload/v1612112929/order_history_empty_y21mqu.png","Gift Card");
                featuredBannersParses.add(featuredBannersParse2);
                FeaturedBannersParse featuredBannersParse3 = new FeaturedBannersParse("https://image.winudf.com/v2/image/Y29tLkVudmVsb3BlRGVzaWduLmdvbGRlbnN0dWRpb19zY3JlZW5fMV8xNTEyMTA5Mzk2XzAyOQ/screen-1.jpg?fakeurl=1&type=.jpg","LIFAFA");
                featuredBannersParses.add(featuredBannersParse3);
                featuredBannersAdapter.notifyDataSetChanged();
                FeaturedBannersRecyclerView.setVisibility(View.VISIBLE);
                homeShimmerViewContainer.stopShimmerAnimation();
                homeShimmerViewContainer.setVisibility(View.GONE);

                Iterable<DataSnapshot> dataSnapshotIterable = snapshot.getChildren();
                homeListParseArrayList.clear();
                for (DataSnapshot next : dataSnapshotIterable){
                    try {
                        String eventID = next.getKey();
                        String image1URL = Objects.requireNonNull(next.child("Image1 URL").getValue()).toString();
                        String image2URL = Objects.requireNonNull(next.child("Image2 URL").getValue()).toString();
                        String title = Objects.requireNonNull(next.child("Title").getValue()).toString();
                        String message = Objects.requireNonNull(next.child("Message").getValue()).toString();
                        String status = Objects.requireNonNull(next.child("Status").getValue()).toString();
                        String appIcon = Objects.requireNonNull(next.child("App Icon").getValue()).toString();
                        String appName = Objects.requireNonNull(next.child("App Name").getValue()).toString();
                        String appID = Objects.requireNonNull(next.child("App ID").getValue()).toString();
                        String appRating = Objects.requireNonNull(next.child("App Rating").getValue()).toString();
                        String appDiscount = Objects.requireNonNull(next.child("App Discount").getValue()).toString();
                        String appDiscountNewUser = Objects.requireNonNull(next.child("App Discount For New User").getValue()).toString();
                        if (!status.equals("hide")){
                            homeListParseArrayList.add(new HomeListParse(eventID,image1URL,image2URL,title,
                                    message,status,appIcon,appName,appID,appRating,appDiscount,appDiscountNewUser));
                        }
                    }catch (NullPointerException e){
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                String isJoined = "false";
                for (int i = 1; i<11; i++){
                    if (i == 7){
                        isJoined = "true";
                    }
                    if (i == 8){
                        isJoined = "false";
                    }
                    if (i == 9){
                        isJoined = "true";
                    }

                    quizListParseArrayList.add(new QuizListParse(String.valueOf(i),"Default","First "+i+"v"+i,"20","05:00 PM","06:00 PM","5","10",isJoined,"3"));
                }
                quizListAdaptor = new QuizListAdaptor(MainActivity.this, quizListParseArrayList);
                homeListAdapter = new HomeListAdapter(MainActivity.this, homeListParseArrayList);

                listView.setAdapter(quizListAdaptor);

                tabLayout.setVisibility(View.VISIBLE);
                if (homeListParseArrayList.size() < 1){
                    listView.setVisibility(View.GONE);
                    findViewById(R.id.emptyLayout).setVisibility(View.VISIBLE);
                }else {
                    listView.setVisibility(View.VISIBLE);
                }

                final int[] x = {0};
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        final Random random = new Random();
                        x[0] = random.nextInt(featuredBannersParses.size());
                        FeaturedBannersRecyclerView.smoothScrollToPosition(x[0]);
                        handler.postDelayed(this, 5000);
                    }
                }, 5000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userDataSnapshot = snapshot;
                if (Objects.requireNonNull(snapshot.child("Security Information").child("Account Status").getValue()).toString().equals("GOOD")){
                    Name = Objects.requireNonNull(snapshot.child("Personal Information").child("Name").getValue()).toString();
                    Email= Objects.requireNonNull(snapshot.child("Personal Information").child("Email").getValue()).toString();
                    ProfilePicture = Objects.requireNonNull(snapshot.child("Personal Information").child("Profile Picture").getValue()).toString();
                    WiningAmount = Objects.requireNonNull(snapshot.child("Personal Information").child("Wallets").child("Wining Amount").getValue()).toString();
                    DepositAmount = Objects.requireNonNull(snapshot.child("Personal Information").child("Wallets").child("Deposit Amount").getValue()).toString();
                    BonusAmount = Objects.requireNonNull(snapshot.child("Personal Information").child("Wallets").child("Bonus Amount").getValue()).toString();
                    Loader();

                    isNewUser = !snapshot.hasChild("Event Order History");

                }else {
                    Toast.makeText(MainActivity.this, "Your account has been blocked...!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        withdrawButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WithdrawActivity.class);
            intent.putExtra("Withdraw Type","Wallet");
            startActivity(intent);
        });

        depositButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
            intent.putExtra("Wallet Type","Wallet");
            startActivity(intent);
        });

        referButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,ReferActivity.class)));

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadBottomNavigation();
    }

    public void Loader(){
        Picasso.get().load(ProfilePicture).into(profilePicImageView);
        userNameTextView.setText(Name);
        userEmailTextView.setText(Email);
        userWithdrawTextView.setText(String.format("₹ %s", WiningAmount));
        userDepositTextView.setText(String.format("₹ %s", DepositAmount));
        userReferTextView.setText(String.format("₹ %s", BonusAmount));
    }

    public void loadBottomNavigation(){
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home){
                setTitle(R.string.app_name);
                ProfileScreen.animate().translationX(100).alpha(0).setDuration(100).withEndAction(() ->{
                    ProfileScreen.setVisibility(View.GONE);
                    HomeScreen.setVisibility(View.VISIBLE);
                    HomeScreen.animate().translationX(0).alpha(1).setDuration(100);
                });
            }
            if (item.getItemId() == R.id.nav_profile){
                setTitle("My Profile");
                HomeScreen.animate().translationX(-100).alpha(0).setDuration(100).withEndAction(() ->{
                    HomeScreen.setVisibility(View.GONE);
                    ProfileScreen.setVisibility(View.VISIBLE);
                    ProfileScreen.animate().translationX(0).alpha(1).setDuration(100);
                });
            }
            return true;
        });
    }

    public void loadTabLayout(){
        tabLayout.addTab(tabLayout.newTab().setText("Quiz"));
        tabLayout.addTab(tabLayout.newTab().setText("Events"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                listView.animate().translationY(100).alpha(0).setDuration(100).withEndAction(() ->{
                    listView.animate().translationY(0).alpha(1).setDuration(100);
                    if (tab.getPosition() == 0){
                        listView.setAdapter(quizListAdaptor);
                    }
                    if (tab.getPosition() == 1){
                        listView.setAdapter(homeListAdapter);
                    }
                });

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(bottomNavigationView.getSelectedItemId() == R.id.nav_profile){
            ProfileScreen.animate().translationX(100).alpha(0).setDuration(100).withEndAction(() ->{
                ProfileScreen.setVisibility(View.GONE);
                HomeScreen.setVisibility(View.VISIBLE);
                HomeScreen.animate().translationX(0).alpha(1).setDuration(100);
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            });
        }else {
            super.onBackPressed();
        }
    }
}
