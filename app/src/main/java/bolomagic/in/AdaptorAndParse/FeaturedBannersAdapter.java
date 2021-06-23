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

public class FeaturedBannersAdapter extends RecyclerView.Adapter<FeaturedBannersAdapter.MyViewHolder> {
    private final List<FeaturedBannersParse> featuredBannersParses;
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView productID;
        ImageView imageURL;
        MyViewHolder(View view) {
            super(view);
            productID = view.findViewById(R.id.productIDTextView);
            imageURL = view.findViewById(R.id.featuredImageView);
        }
    }
    public FeaturedBannersAdapter(List<FeaturedBannersParse> featuredBannersParses) {
        this.featuredBannersParses = featuredBannersParses;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.featured_banners_view, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FeaturedBannersParse featuredBannersParse = featuredBannersParses.get(position);
        holder.productID.setText(featuredBannersParse.getProductID());
        ImageView imageView = holder.imageURL.findViewById(R.id.featuredImageView);
        Picasso.get().load(featuredBannersParse.getImageURL()).into(imageView);
    }
    @Override
    public int getItemCount() {
        return featuredBannersParses.size();
    }
}