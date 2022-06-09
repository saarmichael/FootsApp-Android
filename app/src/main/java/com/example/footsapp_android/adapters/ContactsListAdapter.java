package com.example.footsapp_android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.footsapp_android.R;
import com.example.footsapp_android.entities.Contact;

import java.util.List;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactViewHolder> {

    class ContactViewHolder extends RecyclerView.ViewHolder {
        //private final de.hdodenhof.circleimageview.CircleImageView profileImage;
        private final TextView contactName;
        private final TextView contactLastMsg;
        private final TextView contactLastSent;

        public ContactViewHolder(View itemView) {
            super(itemView);
            //profileImage = itemView.findViewById(R.id.profile_image);
            contactName = itemView.findViewById(R.id.contact_name);
            contactLastMsg = itemView.findViewById(R.id.contact_last_msg);
            contactLastSent = itemView.findViewById(R.id.contact_last_sent);
        }
    }

    private final LayoutInflater mInflater;
    private List<Contact> Contacts; // ContactsListAdapter needs to be updated


    public ContactsListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO may not be contact_layout
        View itemView = mInflater.inflate(R.layout.contact_layout, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        if (Contacts != null) {
            Contact current = Contacts.get(position);
            // TODO update other views of the contact_layout
            holder.contactName.setText(current.getNickname());
            holder.contactLastMsg.setText(current.getLastMessage());
            holder.contactLastSent.setText(current.getTime());
        }
        // Contact data is null, nothing to bind
    }

    @Override
    public int getItemCount() {
        if (Contacts != null) {
            return Contacts.size();
        }
        return 0;
    }

    public List<Contact> getContacts() {
        return Contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.Contacts = contacts;
        if(Contacts != null) {
            notifyDataSetChanged();
        }

    }
}


