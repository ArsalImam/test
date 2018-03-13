package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.SavedPlaces;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SavedPlacesAdapter extends RecyclerView.Adapter<SavedPlacesAdapter.ViewHolder> {

    private ArrayList<SavedPlaces> mRecentPlaces;
    private OnItemClickListener onItemClickListener;
    private Drawable mSelectedStar, mUnSelectedStar;

    public SavedPlacesAdapter(Context context, ArrayList<SavedPlaces> objects, OnItemClickListener onItemClickListener) {
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
        SavedPlaces placesResult = mRecentPlaces.get(position);
        if (position == mRecentPlaces.size() - 1) {
            holder.lineViewPlaces.setVisibility(View.GONE);
        } else {
            holder.lineViewPlaces.setVisibility(View.VISIBLE);
        }
        if (placesResult.getAddress().contains(",")) {
            int lastIndex = placesResult.getAddress().lastIndexOf(',');
            holder.tvName.setText(placesResult.getAddress().substring(0, lastIndex));
            holder.tvAddress.setText(placesResult.getAddress().substring(lastIndex + 1).trim());
        } else {
            holder.tvName.setText(placesResult.getAddress());
            holder.tvAddress.setText(placesResult.getAddress());
        }
        String dist = "" + Math.round(((Utils.calculateDistance(placesResult.getLat(),
                placesResult.getLng(), AppPreferences.getLatitude(),
                AppPreferences.getLongitude())) / 1000) * 10.0) / 10.0;
        holder.tvDist.setText((StringUtils.isNotBlank(dist) ? dist + " km" : "N/A"));
        holder.ivStar.setImageDrawable(mSelectedStar);
    }

    @Override
    public int getItemCount() {
        if (mRecentPlaces != null) {
            return mRecentPlaces.size();
        } else {
            return 0;
        }
    }

    public void update(ArrayList<SavedPlaces> savedPlaces) {
        mRecentPlaces.clear();
        mRecentPlaces.addAll(savedPlaces);
        notifyDataSetChanged();
    }

    public void clear() {
        mRecentPlaces.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        getItems().remove(position);
        notifyItemRemoved(position);
        AppPreferences.updateSavedPlace(getItems());
    }

    public SavedPlaces getItem(int position) {
        return mRecentPlaces.get(position);
    }

    public ArrayList<SavedPlaces> getItems() {
        return mRecentPlaces;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.tvPlaceName)
        FontTextView tvName;

        @Bind(R.id.tvPlaceAddress)
        FontTextView tvAddress;

        @Bind(R.id.tvDistance)
        FontTextView tvDist;

        @Bind(R.id.ivStar)
        ImageView ivStar;

        @Bind(R.id.lineViewPlaces)
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
