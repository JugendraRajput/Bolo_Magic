package bolomagic.in.AdaptorAndParse;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

        TextView textView1 = (TextView) listItemView.findViewById(R.id.textView302);
        textView1.setText(lifafaReceivedHistoryParse.getID());

        TextView textView2 = (TextView) listItemView.findViewById(R.id.textView307);
        textView2.setText(lifafaReceivedHistoryParse.getReceiverName());

        TextView textView3 = (TextView) listItemView.findViewById(R.id.textView309);
        textView3.setText(lifafaReceivedHistoryParse.getReceivedTime());

        TextView textView4 = (TextView) listItemView.findViewById(R.id.textView400);
        textView4.setText(lifafaReceivedHistoryParse.getWon());

        TextView textView5 = (TextView) listItemView.findViewById(R.id.textView404);
        textView5.setText(lifafaReceivedHistoryParse.getStatus());

        return listItemView;
    }

}