package bolomagic.in.CustomDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bolomagic.in.AdaptorAndParse.HomePopUpCardAdapter;
import bolomagic.in.AdaptorAndParse.HomePopUpCardParse;
import bolomagic.in.MainActivity;
import bolomagic.in.R;

import static bolomagic.in.MainActivity.isNewUser;
import static bolomagic.in.MainActivity.userDataSnapshot;

public class CustomDialogHome extends Dialog implements View.OnClickListener {

    public Activity activity;
    public Button openButton, info;
    String currentAppID;
    List<HomePopUpCardParse> homePopUpCardParses;

    public CustomDialogHome(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.activity.setTheme(R.style.Theme_Dialog);
        setContentView(R.layout.custom_dialog_home);
        openButton = findViewById(R.id.button8);
        openButton.setOnClickListener(this);
        info = findViewById(R.id.button4);
        info.setOnClickListener(this);
        init();
    }

    public void init(){
        int position = MainActivity.currentPosition;
        String status = MainActivity.homeListParseArrayList.get(position).getStatus();
        if (status.equals("Active")){
            String eventID = MainActivity.homeListParseArrayList.get(position).getEventID();
            String appIcon = MainActivity.homeListParseArrayList.get(position).getAppIcon();
            String appName = MainActivity.homeListParseArrayList.get(position).getAppName();
            currentAppID = MainActivity.homeListParseArrayList.get(position).getAppID();
            String appRating = MainActivity.homeListParseArrayList.get(position).getAppRating();
            String appDiscount = MainActivity.homeListParseArrayList.get(position).getAppDiscount();
            String appDiscountNewUser = MainActivity.homeListParseArrayList.get(position).getAppDiscountNewUser();
            ImageView imageView1 = findViewById(R.id.imageView20);
            Picasso.get().load(appIcon).into(imageView1);
            TextView textView1 = findViewById(R.id.textView62);
            TextView textView2 = findViewById(R.id.textView64);
            TextView textView3 = findViewById(R.id.textView66);
            TextView textView4 = findViewById(R.id.textView52);
            textView1.setText(appName);
            textView2.setText(appRating);
            int finalDiscount = 0;
            if (appDiscount.equals("hide")){
                textView3.setText("No Discount\nAvailable");
                if (isNewUser){
                    textView4.setText(appDiscountNewUser+"% discount applicable only for you.");
                    finalDiscount = Integer.parseInt(appDiscountNewUser);
                }else {
                    textView4.setText("Instant Creation in your account.");
                }
            }else {
                textView3.setText("Discounted Prize\nupto "+appDiscount+"%");
                if (isNewUser){
                    textView4.setText(appDiscountNewUser+"% discount applicable only for you, "+appDiscount+"% extra discount when you buy this event card.");
                    finalDiscount = Integer.parseInt(appDiscountNewUser) + Integer.parseInt(appDiscount);
                }else {
                    textView4.setText(appDiscount+"% extra discount when you buy this event card.");
                    finalDiscount = Integer.parseInt(appDiscount);
                }

            }
            RecyclerView carRecyclerView = findViewById(R.id.recyclerView2);
            homePopUpCardParses = new ArrayList<>();
            HomePopUpCardAdapter homePopUpCardAdapter = new HomePopUpCardAdapter(homePopUpCardParses);
            LinearLayoutManager cardLinearLayoutManager = new LinearLayoutManager(activity);
            cardLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            carRecyclerView.setLayoutManager(cardLinearLayoutManager);
            carRecyclerView.setItemAnimator(new DefaultItemAnimator());
            carRecyclerView.setAdapter(homePopUpCardAdapter);
            LoadEventCards(finalDiscount, eventID, homePopUpCardAdapter, homePopUpCardParses);
        }else {
            Toast.makeText(activity, "Event has been ended !", Toast.LENGTH_SHORT).show();
        }
    }

    public void LoadEventCards(int discount, String eventID, HomePopUpCardAdapter homePopUpCardAdapter, List<HomePopUpCardParse> homePopUpCardParses){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference.child("SPL").child("Events").child(eventID).child("Cards List");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                homePopUpCardParses.clear();
                Iterable<DataSnapshot> snapshotIterable = snapshot.getChildren();
                int i = 0;
                for (DataSnapshot next : snapshotIterable){
                    String cardID = next.getKey();
                    String name = next.child("Name").getValue().toString();
                    String imageURL = next.child("Image URL").getValue().toString();
                    String prize = next.child("Prize").getValue().toString();
                    String availability = next.child("Availability").getValue().toString();
                    String maxCount = next.child("Max Count").getValue().toString();
                    String cartCount = "1";
                    if (userDataSnapshot.child("Card Cart List").hasChild(cardID)){
                        cartCount = userDataSnapshot.child("Card Cart List").child(cardID).child("Count").getValue().toString();
                    }
                    if (Integer.parseInt(availability) > 0){
                        homePopUpCardParses.add(new HomePopUpCardParse(cardID,imageURL,name,prize,String.valueOf(discount),availability,maxCount, cartCount,i));
                        i++;
                    }
                }
                homePopUpCardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button8) {
            try{
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(currentAppID,"MainActivity.class"));
                activity.startActivity(intent);
                dismiss();
                activity.finish();
            }catch (Exception e){
                Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        if (v.getId() == R.id.button4){
            new AlertDialog.Builder(activity)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("!! Notice !!")
                    .setMessage("There is all cards available for this event.")
                    .show();
        }
    }
}
