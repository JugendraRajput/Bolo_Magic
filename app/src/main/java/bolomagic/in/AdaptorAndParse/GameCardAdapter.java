package bolomagic.in.AdaptorAndParse;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bolomagic.in.R;

public class GameCardAdapter extends ArrayAdapter<GameCardParse> {

    public GameCardAdapter(Activity context, ArrayList<GameCardParse> desserts) {
        super(context, 0, desserts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.game_card_view, parent, false);
        }

        GameCardParse gameCardParse = getItem(position);

        ImageView imageView = listItemView.findViewById(R.id.imageView);

        Picasso.get().load(gameCardParse.getIcon_url()).into(imageView);

        TextView textView1 = listItemView.findViewById(R.id.textView1);
        textView1.setText(gameCardParse.getUnitType());

        TextView textView2 = listItemView.findViewById(R.id.textView2);
        String quantity = gameCardParse.getQuantity();
        String offer = gameCardParse.getOfferPercent();
        int bonus = (Integer.parseInt(quantity)*Integer.parseInt(offer))/100;
        String string = quantity+"<font color='#e28743'> + Bonus "+bonus+"</font>";
        textView2.setText(Html.fromHtml(string));

        TextView button = listItemView.findViewById(R.id.button);
        button.setText("₹ "+gameCardParse.getPrize());

        return listItemView;
    }

}