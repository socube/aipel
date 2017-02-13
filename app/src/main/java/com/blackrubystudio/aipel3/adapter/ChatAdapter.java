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

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextView;

        public MessageViewHolder(View itemView){
            super(itemView);

            mTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        }

    }

    private ArrayList<String> messageSet = new ArrayList<>();

    public ChatAdapter(){}

    // inflating
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_message, parent, false);

        //Return a new holder instance
        MessageViewHolder messageViewHolder = new MessageViewHolder(contactView);
        return messageViewHolder;
    }

    // populating
    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        String message = messageSet.get(position);

        TextView textView = holder.mTextView;
        textView.setText(message);
    }

    @Override
    public int getItemCount() {
        return messageSet.size();
    }

    public void AddMessage(String message){
        messageSet.add(message);
    }
}
