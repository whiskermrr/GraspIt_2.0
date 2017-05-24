package com.example.mrr.final_project_mobile_programming.Utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.example.mrr.final_project_mobile_programming.Contacts.ContactModel;

import java.util.ArrayList;

public class ContactsFetcher {

    public static ArrayList<ContactModel> getContactsAsList(Context context) {

        Uri CONTACT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PHONE_CONTACT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EMAIL_CONTACT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        String PHOTO_URI = ContactsContract.CommonDataKinds.Phone.PHOTO_URI;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CONTACT_URI, null, null, null, NAME + " ASC");

        ArrayList<ContactModel> contactModels = new ArrayList<>();

        if(cursor.getCount() > 0) {

            while(cursor.moveToNext()) {

                ContactModel contactModel = new ContactModel();

                contactModel.setContactId(cursor.getString(cursor.getColumnIndex(_ID)));
                contactModel.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                contactModel.setPhoto(cursor.getString(cursor.getColumnIndex(PHOTO_URI)));


                int hasNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if(hasNumber > 0) {

                    Cursor phoneCursor = contentResolver
                            .query(PHONE_CONTACT_URI, null, PHONE_CONTACT_ID + " = ?", new String[] {contactModel.getContactId()}, null);


                    if(phoneCursor != null) {

                        phoneCursor.moveToFirst();
                        contactModel.setPhoneNumber(phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)));
                        phoneCursor.close();
                    }

                }

                Cursor emailCursor = contentResolver
                        .query(EMAIL_CONTACT_URI, null, EMAIL_CONTACT_ID + " = ?", new String[] {contactModel.getContactId()}, null);

                if(emailCursor.getCount() > 0) {

                    emailCursor.moveToFirst();
                    contactModel.setEmail(emailCursor.getString(emailCursor.getColumnIndex(DATA)));
                    emailCursor.close();
                }

                contactModels.add(contactModel);
            }

            cursor.close();
        }

        return contactModels;
    }
}

