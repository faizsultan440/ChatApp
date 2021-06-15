package com.example.rbchat.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.rbchat.Fragements.CallsFragment;
import com.example.rbchat.Fragements.ChatFragment;
import com.example.rbchat.Fragements.ContactsFragment;
import com.example.rbchat.Fragements.StatusFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0: return new ChatFragment();
            case 1: return new StatusFragment();
            case 2: return new CallsFragment();
           // case 2: return new ContactsFragment();

            default: return new ChatFragment();
        }
            }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title=null;

        switch(position)
        {
            case 0: title="CHATS";break;
            case 1:title="STATUS";break;
            case 2: title= "CALLS";break;
//            case 3:    title="CONTACTS";break;

        }
return title;
    }
}
