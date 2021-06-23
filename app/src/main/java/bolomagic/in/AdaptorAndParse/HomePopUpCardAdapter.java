package bolomagic.in.AdaptorAndParse;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolomagic.in.HashMap.CreateEventCardOrder;
import bolomagic.in.PlayerID;
import bolomagic.in.R;

import static bolomagic.in.MainActivity.DepositAmount;
import static bolomagic.in.MainActivity.UID;
import static bolomagic.in.MainActivity.currentEventPlayerID;
import static bolomagic.in.MainActivity.currentPosition;
import static bolomagic.in.MainActivity.homeListAdapter;
import static bolomagic.in.MainActivity.homeListParseArrayList;

public class HomePopUpCardAdapter extends RecyclerView.Adapter<HomePopUpCardAdapter.MyViewHolder> {
    private final List<HomePopUpCardParse> homePopUpCardParseList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        TextView textView5;
        ImageView imageView;
        Button increase_button;
        Button decrease_button;
        Button buy_button;
        MyViewHolder(View view) {
            super(view);
            textView1 = view.findViewById(R.id.textView70);
            textView2 = view.findViewById(R.id.textView69);
            textView3 = view.findViewById(R.id.textView72);
            textView4 = view.findViewById(R.id.textView73);
            textView5 = view.findViewById(R.id.textView74);
            imageView = view.findViewById(R.id.imageView23);
            increase_button = view.findViewById(R.id.button10);
            decrease_button = view.findViewById(R.id.button11);
            buy_button = view.findViewById(R.id.button9);
        }
    }

    public HomePopUpCardAdapter(List<HomePopUpCardParse> homePopUpCardParse) {
        this.homePopUpCardParseList = homePopUpCardParse;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pop_up_recycle_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HomePopUpCardParse homePopUpCardParse = homePopUpCardParseList.get(position);
        holder.textView1.setText(homePopUpCardParse.getName());
        holder.textView2.setText(homePopUpCardParse.getAvailability());
        
        int originalPrizeX = Integer.parseInt(homePopUpCardParse.getPrize())*Integer.parseInt(homePopUpCardParse.getCartCount());
        
        int discount = Integer.parseInt(homePopUpCardParse.getDiscount());
        int discountValue = (originalPrizeX*discount)/100;
        String prize = String.valueOf(originalPrizeX - discountValue);
        holder.textView5.setPaintFlags(holder.textView5.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        holder.textView5.setText(String.format("₹ %s", originalPrizeX));
        if (Integer.parseInt(prize) <= 0){
            holder.textView3.setText("FREE");
        }else {
            holder.textView3.setText(String.format("₹ %s", prize));
        }
        
        holder.textView4.setText(homePopUpCardParse.getCartCount());
        holder.buy_button.setTag(homePopUpCardParse.getIndex());
        Picasso.get().load(homePopUpCardParse.getImageURL()).into(holder.imageView);

        String currentCount = holder.textView4.getText().toString();
        String currentMaxCount = homePopUpCardParse.getMaxCount();
        holder.increase_button.setEnabled(Integer.parseInt(currentCount) != Integer.parseInt(currentMaxCount));
        holder.decrease_button.setEnabled(Integer.parseInt(currentCount) != 1);

        holder.buy_button.setOnClickListener(v -> {
            if (Integer.parseInt(homePopUpCardParse.getAvailability()) > 0){
                holder.buy_button.setEnabled(false);
                String eventID = homeListParseArrayList.get(currentPosition).getEventID();
                int currentCartCount = Integer.parseInt(holder.textView4.getText().toString());
                String cardID = homePopUpCardParse.getCardID();
                String cardName = homePopUpCardParse.getName();
                int currentPrize = Integer.parseInt(homePopUpCardParse.getPrize());
                int currentDiscount = Integer.parseInt(homePopUpCardParse.getDiscount());
                String message = "Item Name: "+cardName+"\nTotal Item(s): "+currentCartCount+"\nOn Prize: "+currentPrize+"\nWith Discount: "+currentDiscount+"\nThis amount will be charged from your deposit wallet.";
                new AlertDialog.Builder(homeListAdapter.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("!! Notice !!")
                        .setMessage("Do you want to buy below product(s)...\n"+message)
                        .setOnCancelListener(dialog -> holder.buy_button.setEnabled(true))
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            holder.buy_button.setEnabled(true);
                            boolean isValidBalance = false;
                            try{
                                if (Integer.parseInt(DepositAmount) >= currentPrize*currentCartCount){
                                    isValidBalance = true;
                                }
                            }catch (Exception e){
                                if (Double.parseDouble(DepositAmount) >= Double.parseDouble(String.valueOf(currentPrize*currentCartCount))){
                                    isValidBalance = true;
                                }
                            }
                            if (isValidBalance){
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(UID);
                                databaseReference.child("Event Player IDs").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("Player ID")){
                                            currentEventPlayerID = snapshot.child("Player ID").getValue().toString();
                                            /////////
                                            SimpleDateFormat orderIDFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                                            SimpleDateFormat orderDateFormat = new SimpleDateFormat("yyy-MM-dd hh:mm:ss a");

                                            String orderID = orderIDFormat.format(new Date());
                                            String orderDate = orderDateFormat.format(new Date());
                                            CreateEventCardOrder createEventCardOrder = new CreateEventCardOrder(eventID,cardID,String.valueOf(currentPrize),String.valueOf(currentDiscount),String.valueOf(currentCartCount),"Pending",orderDate,"Default");
                                            Map<String, Object> orderValues = createEventCardOrder.toMap();
                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put(orderID, orderValues);
                                            databaseReference.child("Event Order History").updateChildren(childUpdates);
                                            databaseReference.child("Personal Information").child("Wallets").child("Deposit Amount").setValue(currentPrize*currentCartCount - Integer.parseInt(DepositAmount));

                                            databaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(orderID).child("Name").setValue("Event Card brought.\nCard Name: "+cardName);
                                            databaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(orderID).child("Amount").setValue(currentPrize*currentCartCount);
                                            databaseReference.child("Personal Information").child("Wallets").child("Wallet History").child(orderID).child("Time").setValue(ServerValue.TIMESTAMP);

                                            DatabaseReference databaseReferenceAdmin = FirebaseDatabase.getInstance().getReference().child("Admin");
                                            databaseReferenceAdmin.child("Event Order History").updateChildren(childUpdates);
                                            databaseReferenceAdmin.child("Event Order History").child(orderID).child("UID").setValue(UID);
                                            databaseReferenceAdmin.child("Event Order History").child(orderID).child("Player ID").setValue(currentEventPlayerID);
                                            /////////
                                        }else {
                                            Toast.makeText(homeListAdapter.getContext(), "Please enter Player id", Toast.LENGTH_SHORT).show();
                                            homeListAdapter.getContext().startActivity(new Intent(homeListAdapter.getContext(), PlayerID.class));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(homeListAdapter.getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                Toast.makeText(homeListAdapter.getContext(), "You don't have enough balance!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", (dialog, which) -> holder.buy_button.setEnabled(true))
                        .show();
            }else {
                Toast.makeText(homeListAdapter.getContext(), "This item currently out of stock", Toast.LENGTH_SHORT).show();
            }
        });

        holder.increase_button.setOnClickListener(v -> {
            String currentCartCount = holder.textView4.getText().toString();
            String maxCount = homePopUpCardParse.getMaxCount();
            if (Integer.parseInt(currentCartCount) < Integer.parseInt(maxCount)){
                currentCartCount = String.valueOf(Integer.parseInt(currentCartCount)+1);
                holder.textView4.setText(currentCartCount);
                
                int originalPrize = Integer.parseInt(homePopUpCardParse.getPrize())*Integer.parseInt(currentCartCount);
                holder.textView5.setText(String.format("₹ %s", originalPrize));
                int discountValueX = (originalPrize*discount)/100;
                String prizeX = String.valueOf(originalPrize - discountValueX);
                holder.textView3.setText(String.format("₹ %s", prizeX));

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("SPL").child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                databaseReference.child("Card Cart List").child(homePopUpCardParse.getCardID()).child("Count").setValue(currentCartCount);
                if (Integer.parseInt(currentCartCount) == Integer.parseInt(maxCount)){
                    holder.increase_button.setEnabled(false);
                }
            }
            if (!holder.decrease_button.isEnabled()){
                holder.decrease_button.setEnabled(true);
            }
        });

        holder.decrease_button.setOnClickListener(v -> {
            String currentCartCount = holder.textView4.getText().toString();
            if (Integer.parseInt(currentCartCount) > 1){
                currentCartCount = String.valueOf(Integer.parseInt(currentCartCount)-1);
                holder.textView4.setText(currentCartCount);

                int originalPrize = Integer.parseInt(homePopUpCardParse.getPrize())*Integer.parseInt(currentCartCount);
                holder.textView5.setText(String.format("₹ %s", originalPrize));
                int discountValueX = (originalPrize*discount)/100;
                String prizeX = String.valueOf(originalPrize - discountValueX);
                holder.textView3.setText(String.format("₹ %s", prizeX));
                
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("SPL").child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                databaseReference.child("Card Cart List").child(homePopUpCardParse.getCardID()).child("Count").setValue(currentCartCount);
                if (Integer.parseInt(currentCartCount) == 1) {
                    holder.decrease_button.setEnabled(false);
                }
            }
            if (!holder.increase_button.isEnabled()){
                holder.increase_button.setEnabled(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return homePopUpCardParseList.size();
    }
}