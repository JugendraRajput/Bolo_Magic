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

public class LifafaReceivedHistoryAdapter extends ArrayAdapter<LifafaReceivedHistoryParse> {

    public LifafaReceivedHistoryAdapter(Activity context, ArrayList<LifafaReceivedHistoryParse> desserts) {
        super(context, 0, desserts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.lifafa_received_history_view, parent, false);
        }

        LifafaReceivedHistoryParse lifafaReceivedHistoryParse = getItem(position);

        ImageView imageView = listItemView.findViewById(R.id.imageView);
        Picasso.get().load(lifafaReceivedHistoryParse.getSenderProfilePic()).into(imageView);

        TextView textView1 = listItemView.findViewById(R.id.textView1);
        textView1.setText(lifafaReceivedHistoryParse.getSenderName());

        TextView textView2 = listItemView.findViewById(R.id.textView2);
        String message = lifafaReceivedHistoryParse.getMessage();
        if (message.length() > 29){
            message = message.substring(0,29)+"...";
        }
        textView2.setText(message);

        TextView textView3 = listItemView.findViewById(R.id.textView3);
        textView3.setText(lifafaReceivedHistoryParse.getDate());

        return listItemView;
    }

}