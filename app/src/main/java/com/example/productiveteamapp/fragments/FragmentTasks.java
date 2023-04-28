package com.example.productiveteamapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.productiveteamapp.adapters.FragmentTasksAdapter;
import com.example.productiveteamapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FragmentTasks extends Fragment {

    ViewPager2 view_tasks;
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
    }

    private void Init(View view){
        view_tasks=view.findViewById(R.id.viewpager_tasks);
        tabLayout=view.findViewById(R.id.tab_layout);

        view_tasks.setAdapter(new FragmentTasksAdapter(requireActivity()));
        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(tabLayout, view_tasks, (tab, position) -> {
                    switch (position){
                        case 0:
                            tab.setText(R.string.today);
                            break;
                        case 1:
                            tab.setText(R.string.tomorrow);
                            break;
                        case 2:
                            tab.setText(R.string.month);
                            break;
                        case 3:
                            tab.setText(R.string.chronology);
                            break;
                    }
                });
        tabLayoutMediator.attach();
    }
}
