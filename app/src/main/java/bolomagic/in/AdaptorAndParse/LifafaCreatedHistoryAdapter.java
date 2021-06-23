package bolomagic.in.AdaptorAndParse;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
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

        TextView textView1 = (TextView) listItemView.findViewById(R.id.textView32);
        textView1.setText(lifafaCreatedHistoryParse.getID());

        TextView textView2 = (TextView) listItemView.findViewById(R.id.textView37);
        textView2.setText(lifafaCreatedHistoryParse.getReceiverName());

        TextView textView3 = (TextView) listItemView.findViewById(R.id.textView39);
        textView3.setText(lifafaCreatedHistoryParse.getCreationTime());

        TextView textView4 = (TextView) listItemView.findViewById(R.id.textView40);
        textView4.setText(lifafaCreatedHistoryParse.getAvailableAmount());

        TextView textView5 = (TextView) listItemView.findViewById(R.id.textView42);
        textView5.setText(lifafaCreatedHistoryParse.getTotalBalance());

        TextView textView6 = (TextView) listItemView.findViewById(R.id.textView44);
        textView6.setText(lifafaCreatedHistoryParse.getStatus());

        TextView textView7  = (TextView) listItemView.findViewById(R.id.textView49);
        textView7.setText(lifafaCreatedHistoryParse.getLink());

        return listItemView;
    }

}