package bolomagic.in.AdaptorAndParse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bolomagic.in.R;

public class HomeGridAdapter extends ArrayAdapter<HomeGridParse> {

    ArrayList<HomeGridParse> arrayList;
    Context context;
    int resource;

    public HomeGridAdapter(@NonNull Context context, int resource, @NonNull ArrayList<HomeGridParse> objects) {
        super(context, resource, objects);
        this.arrayList = objects;
        this.context = context;
        this.resource = resource;
    }


    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.home_grid_view, null, true);
        }

        HomeGridParse homeGridParse = getItem(position);

        ImageView imageView = convertView.findViewById(R.id.imageView);
        Picasso.get().load(homeGridParse.getIcon_url()).placeholder(R.drawable.loading).into(imageView);

        TextView nameTextView = convertView.findViewById(R.id.textView);
        nameTextView.setText(homeGridParse.getGame_name());

        return convertView;
    }
}
