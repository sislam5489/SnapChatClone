package edu.fordham.snapchatclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.os.Handler;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserInformation userInformationListener = new UserInformation();
        userInformationListener.startFetching();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        getTabs();
    }

   public void getTabs(){
       final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
       new Handler().post(new Runnable() {
           @Override
           public void run() {
               viewPagerAdapter.addFragment(ChatFragment.getInstance(),"Chats");
               viewPagerAdapter.addFragment(StoryFragment.getInstance(),"Stories");
               viewPagerAdapter.addFragment(CameraFragment.getInstance(),"Camera");
               viewPager.setAdapter(viewPagerAdapter);
               tabLayout.setupWithViewPager(viewPager);
           }
       });
   }
}