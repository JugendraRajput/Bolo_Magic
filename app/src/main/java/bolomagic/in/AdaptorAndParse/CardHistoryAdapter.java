package bolomagic.in.AdaptorAndParse;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import bolomagic.in.R;

public class CardHistoryAdapter extends ArrayAdapter<CardHistoryParse> {

    public CardHistoryAdapter(Activity context, ArrayList<CardHistoryParse> desserts) {
        super(context, 0, desserts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.card_history_view, parent, false);
        }

        CardHistoryParse cardHistoryParse = getItem(position);

        ImageView imageView = listItemView.findViewById(R.id.giftCardImageView);
        Picasso.get().load(Objects.requireNonNull(cardHistoryParse).getImageURL()).into(imageView);

        TextView textView2 = listItemView.findViewById(R.id.prizeTextView);
        textView2.setText(cardHistoryParse.getPrize());

        TextView textView3 = listItemView.findViewById(R.id.cashbackTextView);
        textView3.setText(cardHistoryParse.getStatus());

        TextView textView5 = listItemView.findViewById(R.id.textView60);
        textView5.setText(cardHistoryParse.getDate());

        Button button = listItemView.findViewById(R.id.button18);
        TextView textView80 = listItemView.findViewById(R.id.textView80);
        textView80.setText(cardHistoryParse.getCode());
        TextView textView81 = listItemView.findViewById(R.id.textView81);
        button.setOnClickListener(v -> {
            if (cardHistoryParse.getCode().equals("Default")) {
                button.setEnabled(false);
                Toast.makeText(getContext(), "Code not available !", Toast.LENGTH_SHORT).show();
            } else {
                if (button.getText().toString().equals("VIEW CODE")) {
                    button.setText("HIDE CODE");
                    textView80.setVisibility(View.VISIBLE);
                    textView81.setVisibility(View.VISIBLE);
                } else {
                    button.setText("VIEW CODE");
                    textView80.setVisibility(View.GONE);
                    textView81.setVisibility(View.GONE);
                }
            }
        });

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