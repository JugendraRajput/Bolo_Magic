package bolomagic.in;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import bolomagic.in.AdaptorAndParse.HomeGridAdapter;
import bolomagic.in.AdaptorAndParse.HomeGridParse;
import bolomagic.in.AdaptorAndParse.HomeListAdapter;
import bolomagic.in.AdaptorAndParse.HomeListParse;
import bolomagic.in.AdaptorAndParse.QuizListAdaptor;
import bolomagic.in.AdaptorAndParse.QuizListParse;
import bolomagic.in.CustomDialog.CustomDialogHome;
import bolomagic.in.CustomDialog.CustomDialogJoin;

public class MainActivity extends AppCompatActivity {

    public static String DepositAmount;
    public static String UID;
    public static ArrayList<HomeListParse> homeListParseArrayList = new ArrayList<>();
    public static HomeListAdapter homeListAdapter;
    public static ArrayList<QuizListParse> quizListParseArrayList = new ArrayList<>();
    public static QuizListAdaptor quizListAdaptor;
    public static boolean isNewUser = false;
    public static DataSnapshot userDataSnapshot;
    public static int currentPosition = 0;
    public static String currentEventPlayerID = "Default";
    public static int currentQuizPosition = 0;
    ConstraintLayout HomeScreen, ProfileScreen;
    ImageView profilePicImageView;
    TextView userNameTextView, userEmailTextView, userWithdrawTextView, userDepositTextView, userReferTextView;
    Button withdrawButton, depositButton, referButton;
    FirebaseAuth mAuth;
    String Name, Email, WiningAmount, BonusAmount, ProfilePicture;
    ShimmerFrameLayout homeShimmerViewContainer;
    ListView listView;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            UID = mFirebaseUser.getUid();
        }

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

        homeShimmerViewContainer = findViewById(R.id.homeShimmerViewContainer);
        homeShimmerViewContainer.setAutoStart(true);

        loadBottomNavigation();
//        loadQuizEvents();
        loadGridView();

        FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID)
                .child("Personal Information").child("Last Active").setValue(ServerValue.TIMESTAMP);

        RecyclerView FeaturedBannersRecyclerView = findViewById(R.id.recyclerView);

        List<FeaturedBannersParse> featuredBannersParses = new ArrayList<>();
        featuredBannersParses.add(new FeaturedBannersParse("https://tuitionpad.com/wp-content/uploads/2020/08/refer11.png", "REFER"));
        featuredBannersParses.add(new FeaturedBannersParse("https://akm-img-a-in.tosshub.com/indiatoday/envelope647_121717051808_2.jpg", "LIFAFA"));
        featuredBannersParses.add(new FeaturedBannersParse("https://d2j6dbq0eux0bg.cloudfront.net/default-store/giftcards/gift_card_003_1500px.jpg", "Gift Card"));

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
                        startActivity(new Intent(MainActivity.this, ReferActivity.class));
                    }

                    if (productID.equals("Gift Card")) {
                        startActivity(new Intent(MainActivity.this, CardCategoryActivity.class));
                    }
                    if (productID.equals("LIFAFA")) {
                        Intent intent = new Intent(MainActivity.this, LifafaActivity.class);
                        intent.putExtra("Lifafa ID", "DEFAULT");
                        startActivity(intent);
                    }
                });

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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userDataSnapshot = snapshot;
                if (Objects.requireNonNull(snapshot.child("Security Information").child("Account Status").getValue()).toString().equals("GOOD")) {
                    Name = Objects.requireNonNull(snapshot.child("Personal Information").child("Name").getValue()).toString();
                    Email = Objects.requireNonNull(snapshot.child("Personal Information").child("Email").getValue()).toString();
                    ProfilePicture = Objects.requireNonNull(snapshot.child("Personal Information").child("Profile Picture").getValue()).toString();
                    WiningAmount = Objects.requireNonNull(snapshot.child("Personal Information").child("Wallets").child("Wining Amount").getValue()).toString();
                    DepositAmount = Objects.requireNonNull(snapshot.child("Personal Information").child("Wallets").child("Deposit Amount").getValue()).toString();
                    BonusAmount = Objects.requireNonNull(snapshot.child("Personal Information").child("Wallets").child("Bonus Amount").getValue()).toString();

                    Picasso.get().load(ProfilePicture).into(profilePicImageView);
                    userNameTextView.setText(Name);
                    userEmailTextView.setText(Email);
                    userWithdrawTextView.setText(String.format("₹ %s", WiningAmount));
                    userDepositTextView.setText(String.format("₹ %s", DepositAmount));
                    userReferTextView.setText(String.format("₹ %s", BonusAmount));

                    isNewUser = !snapshot.hasChild("Event Order History");

                } else {
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
            intent.putExtra("Withdraw Type", "Wallet");
            startActivity(intent);
        });

        depositButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
            intent.putExtra("Wallet Type", "Wallet");
            startActivity(intent);
        });

        referButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ReferActivity.class));
        });
    }

    public void loadBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                setTitle("Shop");
                ProfileScreen.animate().translationX(100).alpha(0).setDuration(100).withEndAction(() -> {
                    ProfileScreen.setVisibility(View.GONE);
                    HomeScreen.setVisibility(View.VISIBLE);
                    HomeScreen.animate().translationX(0).alpha(1).setDuration(100);
                });
            }
            if (item.getItemId() == R.id.nav_profile) {
                setTitle("PLay");
                HomeScreen.animate().translationX(-100).alpha(0).setDuration(100).withEndAction(() -> {
                    HomeScreen.setVisibility(View.GONE);
                    ProfileScreen.setVisibility(View.VISIBLE);
                    ProfileScreen.animate().translationX(0).alpha(1).setDuration(100);
                });
            }
            return true;
        });
    }

    public void loadQuizEvents() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Quiz"));
        tabLayout.addTab(tabLayout.newTab().setText("Events"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                listView.animate().translationY(100).alpha(0).setDuration(100).withEndAction(() -> {
                    listView.animate().translationY(0).alpha(1).setDuration(100);
                    if (tab.getPosition() == 0) {
                        listView.setAdapter(quizListAdaptor);
                    }
                    if (tab.getPosition() == 1) {
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

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (listView.getAdapter().equals(homeListAdapter)) {
                currentPosition = position;
                CustomDialogHome customDialogHome = new CustomDialogHome(this);
                customDialogHome.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(customDialogHome.getWindow().getAttributes());
                layoutParams.gravity = Gravity.BOTTOM;
                customDialogHome.getWindow().setAttributes(layoutParams);
                customDialogHome.show();
            } else {
                currentQuizPosition = position;
                CustomDialogJoin customDialogJoin = new CustomDialogJoin(this);
                customDialogJoin.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(customDialogJoin.getWindow().getAttributes());
                layoutParams.gravity = Gravity.BOTTOM;
                customDialogJoin.getWindow().setAttributes(layoutParams);
                customDialogJoin.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                customDialogJoin.show();
            }
        });

        DatabaseReference quizDatabaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Quiz");
        quizDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    Iterable<DataSnapshot> dataSnapshotIterable = snapshot.getChildren();
                    for (DataSnapshot next : dataSnapshotIterable) {
                        String quizID = next.getKey();
                        String backgroundURL = "Default";
                        if (next.hasChild("Background URL")) {
                            backgroundURL = next.child("Background URL").getValue().toString();
                        }

                        String isJoined = "false";
                        String totalJoined = "0";
                        if (next.hasChild("Joined Users")) {
                            if (next.child("Joined Users").hasChild(UID)) {
                                isJoined = "true";
                            }
                            totalJoined = String.valueOf(next.child("Joined Users").getChildrenCount());
                        }

                        String quizName = next.child("Quiz Name").getValue().toString();
                        String prizePool = next.child("Prize Pool").getValue().toString();
                        String startTime = next.child("Start Time").getValue().toString();
                        String endTime = next.child("End Time").getValue().toString();

                        String maxJoined = next.child("Max Joined").getValue().toString();
                        String entryFee = next.child("Entry").getValue().toString();
                        String minimumJoined = next.child("minimum Joined").getValue().toString();
                        String status = next.child("Status").getValue().toString();
                        quizListParseArrayList.add(new QuizListParse(quizID, backgroundURL, quizName, prizePool, startTime, endTime, totalJoined, maxJoined, isJoined, entryFee, minimumJoined, status));
                    }
                    quizListAdaptor = new QuizListAdaptor(MainActivity.this, quizListParseArrayList);
                    listView.setAdapter(quizListAdaptor);
                } else {
                    Toast.makeText(MainActivity.this, "No active quiz !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference databaseReferenceBeta = FirebaseDatabase.getInstance().getReference().child("SPL").child("Events");
        databaseReferenceBeta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                homeShimmerViewContainer.stopShimmerAnimation();
                homeShimmerViewContainer.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);

                Iterable<DataSnapshot> dataSnapshotIterable = snapshot.getChildren();
                homeListParseArrayList.clear();
                for (DataSnapshot next : dataSnapshotIterable) {
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
                        if (!status.equals("hide")) {
                            homeListParseArrayList.add(new HomeListParse(eventID, image1URL, image2URL, title,
                                    message, status, appIcon, appName, appID, appRating, appDiscount, appDiscountNewUser));
                        }
                    } catch (NullPointerException e) {
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                homeListAdapter = new HomeListAdapter(MainActivity.this, homeListParseArrayList);

                if (homeListParseArrayList.size() < 1) {
                    listView.setVisibility(View.GONE);
                    findViewById(R.id.emptyLayout).setVisibility(View.VISIBLE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadGridView(){
        GridView gridView = findViewById(R.id.gridView);
        ArrayList<HomeGridParse> homeGridParseArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Gift Cards").child("Games");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()){
                    Iterable<DataSnapshot> iterable = snapshot.getChildren();
                    for (DataSnapshot next : iterable){
                        String id = next.getKey();
                        String icon_url = next.child("Icon URL").getValue().toString();
                        String game_name = next.child("Name").getValue().toString();
                        String game_developer = next.child("Developer").getValue().toString();
                        homeGridParseArrayList.add(new HomeGridParse(id,icon_url,game_name,game_developer));
                    }
                    HomeGridAdapter homeGridAdapter = new HomeGridAdapter(MainActivity.this, R.layout.home_grid_view, homeGridParseArrayList);
                    gridView.setAdapter(homeGridAdapter);
                    gridView.setOnItemClickListener((parent, view, i, id) -> {
                        String icon_url = homeGridParseArrayList.get(i).getIcon_url();
                        String game_name = homeGridParseArrayList.get(i).getGame_name();
                        String game_developer = homeGridParseArrayList.get(i).getGame_developer();
                        Intent intent = new Intent(MainActivity.this, FreeFireActivity.class);
                        intent.putExtra("icon_url",icon_url);
                        intent.putExtra("game_name",game_name);
                        intent.putExtra("game_developer",game_developer);
                        startActivity(intent);
                    });
                    gridView.setVisibility(View.VISIBLE);
                    homeShimmerViewContainer.stopShimmerAnimation();
                    homeShimmerViewContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() == R.id.nav_profile) {
            ProfileScreen.animate().translationX(100).alpha(0).setDuration(100).withEndAction(() -> {
                ProfileScreen.setVisibility(View.GONE);
                HomeScreen.setVisibility(View.VISIBLE);
                HomeScreen.animate().translationX(0).alpha(1).setDuration(100);
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            });
        } else {
            quizListParseArrayList.clear();
            super.onBackPressed();
        }
    }
}
