package bolomagic.in.CustomDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import bolomagic.in.MainActivity;
import bolomagic.in.PlayQuizActivity;
import bolomagic.in.R;

import static bolomagic.in.MainActivity.DepositAmount;
import static bolomagic.in.MainActivity.UID;
import static bolomagic.in.MainActivity.homeListAdapter;

public class CustomDialogJoin extends Dialog implements View.OnClickListener {

    public Activity activity;
    public Button button;

    int position = MainActivity.currentQuizPosition;
    String quizID = MainActivity.quizListParseArrayList.get(position).getQuizID();
    String quizName = MainActivity.quizListParseArrayList.get(position).getQuizName();
    String prizePool = MainActivity.quizListParseArrayList.get(position).getPrizePool();
    String startTime = MainActivity.quizListParseArrayList.get(position).getQuizStartTime();
    String endTime = MainActivity.quizListParseArrayList.get(position).getQuizEndTime();
    String join = MainActivity.quizListParseArrayList.get(position).getTotalJoined();
    String entryFee = MainActivity.quizListParseArrayList.get(position).getEntryFee();
    String maxJoin = MainActivity.quizListParseArrayList.get(position).getMaxJoined();

    DatabaseReference quizDatabaseReference;

    public CustomDialogJoin(Activity activity) {
        super(activity);
        //TODO Auto-generated constructor stub
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.activity.setTheme(R.style.Theme_Dialog);
        setContentView(R.layout.custom_dialog_join);
        button = findViewById(R.id.button1);
        button.setOnClickListener(this);
        init();
    }

    public void init(){
        TextView textView1 = findViewById(R.id.textView1);
        TextView textView2 = findViewById(R.id.textView2);
        TextView textView3 = findViewById(R.id.textView3);
        TextView textView4 = findViewById(R.id.textView4);
        textView1.setText(quizName);
        textView2.setText("₹ "+prizePool);
        textView3.setText(startTime+" - "+endTime);
        if (endTime.equals("Default")){
            textView3.setText(startTime);
        }

        int joined = Integer.parseInt(join);
        int maxJoined = Integer.parseInt(maxJoin);
        textView4.setText(joined+"/"+maxJoined);
        if (joined == maxJoined){
            textView4.setTextColor(Color.RED);
        }

        button.setText("₹ "+entryFee);
        if (MainActivity.quizListParseArrayList.get(position).getIsJoined().equals("true")){
            button.setText("Joined");
        }
        quizDatabaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Quiz").child(quizID);
        LoadRewardList();
    }

    public void LoadRewardList(){
        quizDatabaseReference.child("Reward List").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<String> rewardArrayList = new ArrayList<>();
                Iterable<DataSnapshot> snapshotIterable = snapshot.getChildren();
                for (DataSnapshot next : snapshotIterable){
                    String reward = "Rank "+next.getKey()+" - ₹ "+next.child("Reward").getValue().toString();
                    rewardArrayList.add(reward);
                }
                ListView listView = findViewById(R.id.listView);
                ArrayAdapter arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, rewardArrayList);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {
            button.setEnabled(false);
            if (!button.getText().toString().equals("Joined")){
                if (!MainActivity.quizListParseArrayList.get(position).getIsJoined().equals("true")){
                    new android.app.AlertDialog.Builder(activity)
                            .setIcon(android.R.drawable.ic_lock_idle_alarm)
                            .setOnCancelListener(dialog -> button.setEnabled(true))
                            .setTitle("!! Notice !!")
                            .setMessage("This is a live quiz, if you lost your chance due to connection lost or late entry then we(App:"+R.string.app_name+") will not responsible for this.\nIn this case we will not refund your entry fees.")
                            .setNegativeButton("Cancel", (dialog, which) -> button.setEnabled(true))
                            .setPositiveButton("Agree & Join", (dialog, which) -> {
                                boolean isValidBalance = false;
                                String walletAmountType = "Default";
                                try{
                                    if (Integer.parseInt(DepositAmount) >= Integer.parseInt(MainActivity.quizListParseArrayList.get(position).getEntryFee())){
                                        isValidBalance = true;
                                    }
                                    walletAmountType = "Integer";
                                }catch (Exception e){
                                    if (Double.parseDouble(DepositAmount) >= Double.parseDouble(MainActivity.quizListParseArrayList.get(position).getEntryFee())){
                                        isValidBalance = true;
                                    }
                                    walletAmountType = "Double";
                                }
                                if (isValidBalance){
                                    int joined = Integer.parseInt(join);
                                    int maxJoined = Integer.parseInt(maxJoin);
                                    if (joined < maxJoined){
                                        if (!walletAmountType.equals("Default")){
                                            SimpleDateFormat orderIDFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                                            String orderID = orderIDFormat.format(new Date());
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);
                                            if (walletAmountType.equals("Double")){
                                                databaseReference.child("Personal Information").child("Wallets").child("Deposit Amount").setValue(Double.parseDouble(DepositAmount) - Double.parseDouble(MainActivity.quizListParseArrayList.get(position).getEntryFee()));
                                                databaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(orderID).child("Name").setValue("You have join quiz. QuizID: "+quizID);
                                                databaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(orderID).child("Amount").setValue(Double.parseDouble(DepositAmount) - Double.parseDouble(MainActivity.quizListParseArrayList.get(position).getEntryFee()));
                                                databaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(orderID).child("Time").setValue(ServerValue.TIMESTAMP);}
                                            if (walletAmountType.equals("Integer")){
                                                databaseReference.child("Personal Information").child("Wallets").child("Deposit Amount").setValue(Integer.parseInt(DepositAmount) - Integer.parseInt(MainActivity.quizListParseArrayList.get(position).getEntryFee()));
                                                databaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(orderID).child("Name").setValue("You have join quiz. QuizID: "+quizID);
                                                databaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(orderID).child("Amount").setValue(Integer.parseInt(DepositAmount) - Integer.parseInt(MainActivity.quizListParseArrayList.get(position).getEntryFee()));
                                                databaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(orderID).child("Time").setValue(ServerValue.TIMESTAMP);
                                            }
                                            quizDatabaseReference.child("Joined Users").child(UID).child("Date").setValue(ServerValue.TIMESTAMP);
                                            MainActivity.quizListParseArrayList.get(position).setIsJoined("true");
                                            MainActivity.quizListParseArrayList.get(position).setTotalJoined(String.valueOf(joined+1));
                                            MainActivity.quizListAdaptor.notifyDataSetChanged();

                                            button.setEnabled(true);
                                            button.setText("Joined");
                                            Toast.makeText(activity, "Successfully joined.", Toast.LENGTH_SHORT).show();
                                        }else {
                                            button.setEnabled(true);
                                            Toast.makeText(activity, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        button.setEnabled(true);
                                        Toast.makeText(activity, "Slots are full!", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    button.setEnabled(true);
                                    Toast.makeText(activity, "You don't have enough balance!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }else {
                    button.setEnabled(true);
                    button.setText("Joined");
                }
            }else {
                String quizStatus = MainActivity.quizListParseArrayList.get(position).getStatus();
                if (quizStatus.equals("Active")){
                    Intent intent = new Intent(activity, PlayQuizActivity.class);
                    intent.putExtra("quizID", MainActivity.quizListParseArrayList.get(position).getQuizID());
                    activity.startActivity(intent);
                    dismiss();
                }
                if (quizStatus.equals("Waiting")){
                    button.setEnabled(true);
                    Toast.makeText(activity, "Quiz About to start...", Toast.LENGTH_SHORT).show();
                }
                if (quizStatus.equals("Ended")){
                    button.setEnabled(true);
                    Toast.makeText(activity, "Quiz ended !", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
