package com.example.footsapp_android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footsapp_android.R;
import com.example.footsapp_android.entities.Message;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnMessageClickListener {
        void onMessageClick(int position);
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        //private final de.hdodenhof.circleimageview.CircleImageView profileImage;
        private final TextView messageSentText;
        private final TextView messageSentTime;


        public SentMessageViewHolder(View itemView) {
            super(itemView);
            //profileImage = itemView.findViewById(R.id.profile_image);
            messageSentText = itemView.findViewById(R.id.message_sent_text);
            messageSentTime = itemView.findViewById(R.id.message_sent_time);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        //private final de.hdodenhof.circleimageview.CircleImageView profileImage;
        private final TextView messageSentText;
        private final TextView messageSentTime;


        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            //profileImage = itemView.findViewById(R.id.profile_image);
            messageSentText = itemView.findViewById(R.id.message_received_text);
            messageSentTime = itemView.findViewById(R.id.message_received_time);
        }
    }

    private final LayoutInflater mInflater;
    private List<Message> messages; // ContactsListAdapter needs to be updated

    public static final int SENT_TYPE_VIEW = 1;
    public static final int RECEIVED_TYPE_VIEW = 2;
    // constructor


    public MessageListAdapter(Context context, OnMessageClickListener listener) {
        mInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO may not be contact_layout
        if (viewType == SENT_TYPE_VIEW) {
            View itemView = mInflater.inflate(R.layout.message_sent_layout, parent, false);
            return new SentMessageViewHolder(itemView);
        } else {
            View itemView = mInflater.inflate(R.layout.message_recieved_layout, parent, false);
            return new ReceivedMessageViewHolder(itemView);
        }
    }

    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (messages != null) {
            if (getItemViewType(position) == SENT_TYPE_VIEW) {
                SentMessageViewHolder sentMessageViewHolder = (SentMessageViewHolder) holder;
                sentMessageViewHolder.messageSentText.setText(messages.get(position).getContent());
                sentMessageViewHolder.messageSentTime.setText(messages.get(position).getTime());
            } else {
                ReceivedMessageViewHolder receivedMessageViewHolder = (ReceivedMessageViewHolder) holder;
                receivedMessageViewHolder.messageSentText.setText(messages.get(position).getContent());
                receivedMessageViewHolder.messageSentTime.setText(messages.get(position).getTime());
            }
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

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isSender()) {
            return SENT_TYPE_VIEW;
        } else {
            return RECEIVED_TYPE_VIEW;
        }
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        if (messages != null) {
            notifyDataSetChanged();
        }

    }
}
