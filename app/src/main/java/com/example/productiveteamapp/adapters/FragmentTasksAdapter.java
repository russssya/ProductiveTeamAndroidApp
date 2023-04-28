package com.example.productiveteamapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.productiveteamapp.fragments.FragmentTasksMonth;
import com.example.productiveteamapp.fragments.FragmentTasksChronology;
import com.example.productiveteamapp.fragments.FragmentTasksToday;
import com.example.productiveteamapp.fragments.FragmentTasksTomorrow;

public class FragmentTasksAdapter extends FragmentStateAdapter {

    public FragmentTasksAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FragmentTasksToday();
            case 1:
                return new FragmentTasksTomorrow();
            case 2:
                return new FragmentTasksMonth();
            case 3:
                return new FragmentTasksChronology();
            default:
                return new FragmentTasksToday();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
