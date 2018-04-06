package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecentPlacesAdapter extends RecyclerView.Adapter<RecentPlacesAdapter.ViewHolder> {

    private ArrayList<PlacesResult> mRecentPlaces;
    private OnItemClickListener onItemClickListener;
    private Drawable mSelectedStar, mUnSelectedStar;

    public RecentPlacesAdapter(Context context, ArrayList<PlacesResult> objects, OnItemClickListener onItemClickListener) {
        mRecentPlaces = objects;
        this.onItemClickListener = onItemClickListener;
        mSelectedStar = Utils.changeDrawableColor(context, R.drawable.ic_star_grey, R.color.yellowStar);
        mUnSelectedStar = Utils.changeDrawableColor(context, R.drawable.ic_star_grey, R.color.secondaryColorLight);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_recent_places, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlacesResult placesResult = mRecentPlaces.get(position);
        if (position == mRecentPlaces.size() - 1) {
            holder.lineViewPlaces.setVisibility(View.GONE);
        } else {
            holder.lineViewPlaces.setVisibility(View.VISIBLE);
        }
        holder.tvName.setText(placesResult.name);

        int lastIndex = placesResult.address.lastIndexOf(',');
        holder.tvAddress.setText(placesResult.address.substring(lastIndex + 1).trim());
        String dist = "" + Math.round(((Utils.calculateDistance(placesResult.latitude,
                placesResult.longitude, AppPreferences.getLatitude(),
                AppPreferences.getLongitude())) / 1000) * 10.0) / 10.0;
        holder.tvDist.setText((StringUtils.isNotBlank(dist) ? dist + " km" : "N/A"));
        holder.ivStar.setImageDrawable(placesResult.isSaved ? mSelectedStar : mUnSelectedStar);
    }

    @Override
    public int getItemCount() {
        if (mRecentPlaces != null) {
            return mRecentPlaces.size();
        } else {
            return 0;
        }
    }

    public PlacesResult getItem(int position) {
        return mRecentPlaces.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvPlaceName)
        FontTextView tvName;

        @BindView(R.id.tvPlaceAddress)
        FontTextView tvAddress;

        @BindView(R.id.tvDistance)
        FontTextView tvDist;

        @BindView(R.id.ivStar)
        ImageView ivStar;

        @BindView(R.id.lineViewPlaces)
        View lineViewPlaces;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            ivStar.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                if (v.getId() == R.id.ivStar) {
                    onItemClickListener.onStarClickListener(getLayoutPosition());
                } else {
                    onItemClickListener.onItemClickListener(getLayoutPosition());
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);

        void onStarClickListener(int position);
    }
}
