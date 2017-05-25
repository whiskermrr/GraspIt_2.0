package com.example.mrr.final_project_mobile_programming.Contacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrr.final_project_mobile_programming.Calendar.Event;
import com.example.mrr.final_project_mobile_programming.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ContactsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ContactModel> mContacts;

    public ContactsAdapter(Context context, ArrayList<ContactModel> contacts) {

        mContext = context;
        this.mContacts = contacts;
    }

    private class ContactsViewHolder {

        ImageView contactImage;
        TextView contactName;
        TextView contactEmail;
        TextView contactPhone;

        private ContactsViewHolder(View view) {

            contactImage = (ImageView) view.findViewById(R.id.contactImage);
            contactName = (TextView) view.findViewById(R.id.contactName);
            contactPhone = (TextView) view.findViewById(R.id.contactPhone);
            contactEmail = (TextView) view.findViewById(R.id.contactEmail);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ContactsViewHolder holder = null;

        if(view == null) {

            LayoutInflater inflater =  LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.contacts_row_layout, parent, false);
            holder = new ContactsViewHolder(view);
            view.setTag(holder);
        }

        else
            holder = (ContactsViewHolder) view.getTag();

        holder.contactName.setText(mContacts.get(position).getName());

        if(mContacts.get(position).getPhoneNumber() != null)
            holder.contactPhone.setText(mContacts.get(position).getPhoneNumber());

        if(mContacts.get(position).getEmail() != null)
            holder.contactEmail.setText(mContacts.get(position).getEmail());

        String picture = mContacts.get(position).getPhoto();

        if(picture != null) {

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(picture));
                holder.contactImage.setImageBitmap(bitmap);
            }

            catch (FileNotFoundException e) {

                e.printStackTrace();
            }

            catch (IOException e) {

                e.printStackTrace();
            }
        }

        else
            holder.contactImage.setImageResource(R.mipmap.ic_launcher);

        return view;
    }

    public String getNumber(int position) {

        return mContacts.get(position).getPhoneNumber();
    }

    public ArrayList<ContactModel> getAllContacts() {

        return mContacts;
    }

    public void addContactToList(ContactModel contact) {

        mContacts.add(contact);
        this.notifyDataSetChanged();
    }

    public void removeContactFromList(int position) {

        mContacts.remove(position);
        this.notifyDataSetChanged();
    }

    public void clearContactList() {

        mContacts = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return mContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}

