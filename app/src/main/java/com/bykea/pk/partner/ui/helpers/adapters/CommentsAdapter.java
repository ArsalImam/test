package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PredefineRatingToShow;
import com.bykea.pk.partner.models.data.Predefine_rating;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private ArrayList<Predefine_rating> commentsList;
    private ItemClickListener itemClickListener;
    private Context mContext;

    public CommentsAdapter(Context context, ArrayList<Predefine_rating> ratingList) {
        this.commentsList = ratingList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_comments,
                parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        if ("true".equalsIgnoreCase(commentsList.get(position).getSelected())) {
//            holder.tvComment.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_selected_feedback_comment));
//        } else {
//            holder.tvComment.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_unselected_feedback_comment));
//        }
        holder.tvComment.setText(commentsList.get(position).getMessage());
    }

   /* public void updateData(ArrayList<PredefineRatingToShow> newComments) {
        commentsList.clear();
        commentsList.addAll(newComments);
        notifyDataSetChanged();
    }*/

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvComment)
        FontTextView tvComment;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    itemClickListener.onClick(commentsList.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public interface ItemClickListener {
        void onClick(PredefineRatingToShow item, int position);
    }
}
