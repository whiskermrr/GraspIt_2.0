package com.example.mrr.final_project_mobile_programming.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mrr.final_project_mobile_programming.Activity.Communicator;
import com.example.mrr.final_project_mobile_programming.Adapters.ContactsAdapter;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactModel;
import com.example.mrr.final_project_mobile_programming.R;
import com.example.mrr.final_project_mobile_programming.Utilities.ContactsFetcher;

public class ContactsFragment extends DialogFragment {

    ContactsAdapter adapter;
    ListView contactsList;
    Communicator communicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.contacts_fragment_layout, container, false);

        adapter = new ContactsAdapter(getActivity(), ContactsFetcher.getContactsAsList(getActivity()));

        contactsList = (ListView) view.findViewById(R.id.contactsList);
        contactsList.setAdapter(adapter);
        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ContactModel contact = (ContactModel) adapter.getItem(position);
                communicator.updateAddEventFragment(contact);
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        communicator = (Communicator) context;
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().setCanceledOnTouchOutside(true);

        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int height = (displayMetrics.heightPixels - (displayMetrics.heightPixels / 5));
        int width = (displayMetrics.widthPixels - (displayMetrics.widthPixels / 5));

        getDialog().getWindow().setLayout(width, height);
    }
}
