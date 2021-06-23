package bolomagic.in.AdaptorAndParse;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import bolomagic.in.R;

public class cartAdapter extends ArrayAdapter<cartParse> {

    public cartAdapter(Activity context, ArrayList<cartParse> desserts) {
        super(context, 0, desserts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.cart_view, parent, false);
        }

        cartParse cartParse = getItem(position);

        TextView textView1 = (TextView) listItemView.findViewById(R.id.cartIDTextView);
        textView1.setText(cartParse.getCardID());

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.giftcartImageView);
        Picasso.get().load(Objects.requireNonNull(cartParse).getImageURL()).into(imageView);

        TextView textView2 = (TextView) listItemView.findViewById(R.id.prizeTextView);
        textView2.setText(cartParse.getPrize());

        TextView textView3 = (TextView) listItemView.findViewById(R.id.cashbackTextView);
        textView3.setText(cartParse.getCashBack());

        TextView textView4 = (TextView) listItemView.findViewById(R.id.effectivePrizeTextView);
        textView4.setText(cartParse.getEffectivePrize());

        TextView textView5 = (TextView) listItemView.findViewById(R.id.validityTextView);
        textView5.setText(cartParse.getValidity());

        Button button = (Button) listItemView.findViewById(R.id.button);
        button.setOnClickListener(v -> {
            String string = "How To Redeem"+"\n\t1. Download & Login into PayTm App." +
                    "\n\t2. Go to Add Money.\n\t3. Goo to 'Have a PromoCode' and Enter the Code.\n\n" +
                    "Where To Redeem"+"\n\tDownload & Login into PayTm App.\n\n" + "Terms"+
                    "\n\t1. Download & Login into PayTm App.\n\t2. Go to Add Money." +
                    "\n\t3. Go to 'Have a PromoCode' and Enter the Code.\n\t4. Valid for 7 from the date of issue of voucher.";
            new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(string)
                    .setCancelable(true)
                    .show();
        });

        Button removeButton = (Button) listItemView.findViewById(R.id.cartButton);
        removeButton.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference().child("SPL").child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Personal Information").child("Cart").child(cartParse.getCardID()).child("Time").removeValue());

        return listItemView;
    }
    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}