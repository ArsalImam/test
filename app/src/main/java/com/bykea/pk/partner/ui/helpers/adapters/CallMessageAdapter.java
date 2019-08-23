package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.ChatMessagesTranslated;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

public class CallMessageAdapter extends RecyclerView.Adapter<CallMessageAdapter.ViewHolder> {

    ArrayList<ChatMessagesTranslated> chatMessagesTranslatedArrayList;
    private ItemClickListener itemClickListener;

    public CallMessageAdapter(ArrayList<ChatMessagesTranslated> chatMessagesTranslatedArrayList) {
        this.chatMessagesTranslatedArrayList = chatMessagesTranslatedArrayList;
    }

    @NonNull
    @Override
    public CallMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent, false);
        return new CallMessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallMessageAdapter.ViewHolder holder, int position) {
        ChatMessagesTranslated chatMessagesTranslated = chatMessagesTranslatedArrayList.get(position);
        holder.tVChatMessageUrdu.setText(chatMessagesTranslated.getChatMessageInUrdu());
    }

    @Override
    public int getItemCount() {
        return chatMessagesTranslatedArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        FontTextView tVChatMessageUrdu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tVChatMessageUrdu = itemView.findViewById(R.id.tVChatMessageUrdu);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(getItem(getAdapterPosition()));
        }
    }

    public interface ItemClickListener {
        void onItemClick(ChatMessagesTranslated chatMessagesTranslated);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private ChatMessagesTranslated getItem(int id) {
        return chatMessagesTranslatedArrayList.get(id);
    }
}
