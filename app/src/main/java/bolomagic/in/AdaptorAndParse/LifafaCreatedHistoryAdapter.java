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

import bolomagic.in.R;

public class LifafaCreatedHistoryAdapter extends ArrayAdapter<LifafaCreatedHistoryParse> {

    public LifafaCreatedHistoryAdapter(Activity context, ArrayList<LifafaCreatedHistoryParse> desserts) {
        super(context, 0, desserts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.lifafa_created_history_view, parent, false);
        }

        LifafaCreatedHistoryParse lifafaCreatedHistoryParse = getItem(position);

        ImageView imageView = listItemView.findViewById(R.id.imageView);
        Picasso.get().load(lifafaCreatedHistoryParse.getImageURL()).into(imageView);

        TextView textView1 = listItemView.findViewById(R.id.textView1);
        textView1.setText(lifafaCreatedHistoryParse.getCount()+" Lucky Lifafa");

        TextView textView2 = listItemView.findViewById(R.id.textView2);
        String message = lifafaCreatedHistoryParse.getMessage();
        if (message.length() > 29){
            message = message.substring(0,29)+"...";
        }
        textView2.setText(message);

        TextView textView3 = listItemView.findViewById(R.id.textView3);
        textView3.setText("₹ "+lifafaCreatedHistoryParse.getTotalAmount());

        return listItemView;
    }

}