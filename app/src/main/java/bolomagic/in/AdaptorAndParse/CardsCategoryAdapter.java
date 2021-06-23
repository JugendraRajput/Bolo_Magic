package bolomagic.in.AdaptorAndParse;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import bolomagic.in.R;

public class CardsCategoryAdapter extends ArrayAdapter<CardCategoryParse> {

    public CardsCategoryAdapter(Activity context, ArrayList<CardCategoryParse> desserts) {
        super(context, 0, desserts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.card_category_view, parent, false);
        }

        CardCategoryParse cardCategoryParse = getItem(position);

        TextView textView1 = (TextView) listItemView.findViewById(R.id.textView16);
        textView1.setText(cardCategoryParse.getCategoryName());

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.imageView12);
        Picasso.get().load(Objects.requireNonNull(cardCategoryParse).getImageURL()).into(imageView);

        TextView textView2 = (TextView) listItemView.findViewById(R.id.textView25);
        textView2.setText(cardCategoryParse.getMaxCashBack());

        TextView textView3 = (TextView) listItemView.findViewById(R.id.textView30);
        textView3.setText(cardCategoryParse.getGiftsCount());

        return listItemView;
    }

}