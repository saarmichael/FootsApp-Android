package com.example.footsapp_android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.footsapp_android.R;
import com.example.footsapp_android.entities.Message;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {

    public interface OnMessageClickListener {
        void onMessageClick(int position);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //private final de.hdodenhof.circleimageview.CircleImageView profileImage;
        private final TextView messageSentText;
        private final TextView messageSentTime;
        private OnMessageClickListener listener;

        public MessageViewHolder(View itemView, OnMessageClickListener listener) {
            super(itemView);
            //profileImage = itemView.findViewById(R.id.profile_image);
            messageSentText = itemView.findViewById(R.id.message_sent_text);
            messageSentTime = itemView.findViewById(R.id.message_sent_time);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onMessageClick(getAdapterPosition());
        }
    }

    private final LayoutInflater mInflater;
    private List<Message> messages; // ContactsListAdapter needs to be updated
    private OnMessageClickListener listener;

    // constructor



    public MessageListAdapter(Context context, OnMessageClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO may not be contact_layout
        View itemView = mInflater.inflate(R.layout.message_sent_layout, parent, false);
        return new MessageViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        if (messages != null) {
            Message current = messages.get(position);
            // TODO update other views of the contact_layout
            holder.messageSentText.setText(current.getContent());
            holder.messageSentTime.setText(current.getTime());
        }
        // Contact data is null, nothing to bind
    }

    @Override
    public int getItemCount() {
        if (messages != null) {
            return messages.size();
        }
        return 0;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        if(messages != null) {
            notifyDataSetChanged();
        }

    }
}
