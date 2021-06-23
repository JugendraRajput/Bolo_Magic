package bolomagic.in.AdaptorAndParse;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import bolomagic.in.R;

public class ReferAdaptor extends ArrayAdapter<ReferParse> {

    ArrayList<ReferParse> arrayList;
    Context context;
    int resource;

    public ReferAdaptor(@NonNull Context context, int resource, @NonNull ArrayList<ReferParse> objects) {
        super(context, resource, objects);
        this.arrayList = objects;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.refer_layout_view,null,true);
        }

        ReferParse referParse = getItem(position);

        TextView friendName = convertView.findViewById(R.id.textView200);
        friendName.setText(referParse.getFriendName());

        TextView referDate = convertView.findViewById(R.id.textView202);
        referDate.setText(referParse.getReferDate());

        TextView friendUID = convertView.findViewById(R.id.textView203);
        friendUID.setText(referParse.getFriendUID());

        return convertView;
    }
}