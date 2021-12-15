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

public class withdrawalMethodsAdapter extends ArrayAdapter<withdrawalMethodsParse> {

    public withdrawalMethodsAdapter(Activity context, ArrayList<withdrawalMethodsParse> desserts) {
        super(context, 0, desserts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.withdrawal_methods_view, parent, false);
        }

        withdrawalMethodsParse withdrawalMethodsParse = getItem(position);

        TextView textView0 = listItemView.findViewById(R.id.methodIDTextView);
        textView0.setText(withdrawalMethodsParse.getMethodID());

        ImageView imageView = listItemView.findViewById(R.id.methodImageView);
        Picasso.get().load(withdrawalMethodsParse.getMethodImageURL()).into(imageView);

        TextView textView1 = listItemView.findViewById(R.id.methodNameTextView);
        textView1.setText(withdrawalMethodsParse.getMethodName());

        TextView textView2 = listItemView.findViewById(R.id.methodValueTextView);
        textView2.setText(withdrawalMethodsParse.getMethodValue());

        if (withdrawalMethodsParse.getMethodValue().equals("DEFAULT")) {
            textView2.setVisibility(View.GONE);
        }


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