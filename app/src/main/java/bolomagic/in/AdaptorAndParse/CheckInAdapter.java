package bolomagic.in.AdaptorAndParse;

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

public class CheckInAdapter extends RecyclerView.Adapter<CheckInAdapter.MyViewHolder> {
    private final List<CheckInParse> checkInParseList;

    public CheckInAdapter(List<CheckInParse> checkInParses) {
        this.checkInParseList = checkInParses;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.check_in_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CheckInParse checkInParse = checkInParseList.get(position);
        //ImageView imageView = holder.imageView.findViewById(R.id.featuredImageView);
        holder.day.setText(checkInParse.getDay());
        Picasso.get().load(checkInParse.getImageURL()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return checkInParseList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView day;
        ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            day = view.findViewById(R.id.textView);
            imageView = view.findViewById(R.id.imageView);
        }
    }
}