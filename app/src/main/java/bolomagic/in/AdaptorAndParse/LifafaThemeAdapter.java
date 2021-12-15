package bolomagic.in.AdaptorAndParse;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import bolomagic.in.R;

public class LifafaThemeAdapter extends RecyclerView.Adapter<LifafaThemeAdapter.MyViewHolder> {
    private final List<LifafaThemeParse> LifafaThemeParses;

    public LifafaThemeAdapter(List<LifafaThemeParse> LifafaThemeParses) {
        this.LifafaThemeParses = LifafaThemeParses;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LifafaThemeParse LifafaThemeParse = LifafaThemeParses.get(position);
        ImageView imageView = holder.imageURL.findViewById(R.id.imageViewTheme);
        Picasso.get().load(LifafaThemeParse.getShortImageURL()).into(imageView);
        TextView textView = holder.textView.findViewById(R.id.textView);
        textView.setText(LifafaThemeParse.getTitle());
        if (LifafaThemeParse.getIsSelected().equals("true")) {
            textView.setTextColor(Color.parseColor("#000000"));
        } else {
            textView.setTextColor(Color.parseColor("#979797"));
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lifafa_theme_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return LifafaThemeParses.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageURL;
        TextView textView;

        MyViewHolder(View view) {
            super(view);
            imageURL = view.findViewById(R.id.imageViewTheme);
            textView = view.findViewById(R.id.textView);
        }
    }
}