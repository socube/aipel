package com.blackrubystudio.aipel3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackrubystudio.aipel3.R;

import java.util.ArrayList;

/**
 * Created by jaewoo on 2017. 1. 23..
 */

public class ChatAdapter extends
        RecyclerView.Adapter<ChatAdapter.MessageViewHolder>{

    public static final int VIEW_TYPE_AIPEL = 0;
    public static final int VIEW_TYPE_USER = 1;

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextView;

        public MessageViewHolder(View itemView){
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        }
    }

    private class MessageWithViewType{
        String item_message;
        int view_type;

        public MessageWithViewType(String item_message, int view_type) {
            this.item_message = item_message;
            this.view_type = view_type;
        }

        public String getItem_message() {
            return item_message;
        }

        public int getView_type() {
            return view_type;
        }
    }

    private ArrayList<MessageWithViewType> messageSet = new ArrayList<>();
    public ChatAdapter(){}

    // inflating
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView;
        if(viewType == VIEW_TYPE_AIPEL) {
            contactView = inflater.inflate(R.layout.item_message_aipel, parent, false);
        }else{
            contactView = inflater.inflate(R.layout.item_message_user, parent, false);
        }

        return new MessageViewHolder(contactView);
    }

    // populating
    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        String message = messageSet.get(position).getItem_message();

        TextView textView = holder.mTextView;
        textView.setText(message);
    }

    @Override
    public int getItemCount() {
        return messageSet.size();
    }

    public void AddMessage(String message, int num){
        MessageWithViewType messageWithViewType = new MessageWithViewType(message, num);
        messageSet.add(messageWithViewType);
    }

    @Override
    public int getItemViewType(int position) {
        return messageSet.get(position).getView_type();
    }
}
