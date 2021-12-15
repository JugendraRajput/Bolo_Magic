package bolomagic.in.AdaptorAndParse;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import bolomagic.in.R;

public class CardsAdapter extends ArrayAdapter<CardParse> {

    public CardsAdapter(Activity context, ArrayList<CardParse> desserts) {
        super(context, 0, desserts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.card_view, parent, false);
        }

        CardParse cardParse = getItem(position);

        TextView textView1 = (TextView) listItemView.findViewById(R.id.cardIDTextView);
        textView1.setText(cardParse.getCardID());

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.giftCardImageView);
        Picasso.get().load(Objects.requireNonNull(cardParse).getImageURL()).into(imageView);

        TextView textView2 = (TextView) listItemView.findViewById(R.id.prizeTextView);
        textView2.setText(cardParse.getPrize());

        TextView textView3 = (TextView) listItemView.findViewById(R.id.cashbackTextView);
        textView3.setText(cardParse.getCashBack());

        TextView textView4 = (TextView) listItemView.findViewById(R.id.effectivePrizeTextView);
        textView4.setText(cardParse.getEffectivePrize());

        TextView textView5 = (TextView) listItemView.findViewById(R.id.validityTextView);
        textView5.setText(cardParse.getValidity());

        Button button = (Button) listItemView.findViewById(R.id.cartButton);
        button.setText(cardParse.getButtonText());

        return listItemView;
    }

}