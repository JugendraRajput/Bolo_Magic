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

public class HomeListAdapter extends ArrayAdapter<HomeListParse> {

    public HomeListAdapter(Activity context, ArrayList<HomeListParse> desserts) {
        super(context, 0, desserts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.home_list_view, parent, false);
        }

        HomeListParse homeListParse = getItem(position);

        ImageView imageView1 = listItemView.findViewById(R.id.imageView1);
        Picasso.get().load(homeListParse.getImage1URL()).into(imageView1);

        ImageView imageView2 = listItemView.findViewById(R.id.imageView2);
        Picasso.get().load(homeListParse.getImage2URL()).into(imageView2);

        TextView textView1 = listItemView.findViewById(R.id.textView1);
        textView1.setText(homeListParse.getTitle());

        TextView textView2 = listItemView.findViewById(R.id.textView2);
        textView2.setText(homeListParse.getMessage());

        return listItemView;
    }

}