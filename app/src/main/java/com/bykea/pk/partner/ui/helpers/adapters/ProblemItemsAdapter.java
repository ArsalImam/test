package com.bykea.pk.partner.ui.helpers.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProblemItemsAdapter extends RecyclerView.Adapter<ProblemItemsAdapter.ItemHolder> {

    private ArrayList<String> mProblemList;
    private MyOnItemClickListener myOnItemClickListener;

    public ProblemItemsAdapter(ArrayList<String> list) {
        mProblemList = list;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.problem_items_view,
                parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.tv_problem_item.setText(mProblemList.get(position));
    }


    @Override
    public int getItemCount() {
        return mProblemList.size();
    }


    class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_problem_item)
        FontTextView tv_problem_item;

        ItemHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myOnItemClickListener.onItemClickListener(getAdapterPosition(), v, mProblemList.get(getAdapterPosition()));
                }
            });
        }
    }

    public void setMyOnItemClickListener(MyOnItemClickListener itemClickListener) {
        myOnItemClickListener = itemClickListener;
    }

    public interface MyOnItemClickListener {
        void onItemClickListener(int position, View view, String reason);
    }
}

