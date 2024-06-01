package com.example.xiaoshu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.*;
import androidx.annotation.*;
import androidx.fragment.app.*;
import androidx.viewpager.widget.ViewPager;

import com.example.xiaoshu.ui.fragments.NoteMainFragment;
import com.example.xiaoshu.ui.fragments.UserFragment;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private final String[] tabs = {"笔记", "我的"};
    private final List<Fragment> tabFragmentList = new ArrayList<>();
    private Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TabLayout tabLayout = findViewById(R.id.bar_bottom);
        ViewPager viewPager = findViewById(R.id.view_pager);


        //添加tab
        tabLayout.addTab(tabLayout.newTab().setText(tabs[0]));
        tabFragmentList.add(new NoteMainFragment());
        tabLayout.addTab(tabLayout.newTab().setText(tabs[1]));
        tabFragmentList.add(new UserFragment());

        viewPager.setAdapter(
                new FragmentPagerAdapter(getSupportFragmentManager(),
                        FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                    @NonNull
                    @Override
                    public Fragment getItem(int position) {
                        return tabFragmentList.get(position);
                    }

                    @Override
                    public int getCount() {
                        return tabFragmentList.size();
                    }

                    @Nullable
                    @Override
                    public CharSequence getPageTitle(int position) {
                        return tabs[position];
                    }
                });

        //设置TabLayout和ViewPager联动
        tabLayout.setupWithViewPager(viewPager,false);

        // 给tab设置图标
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_note);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_user);

    }
}