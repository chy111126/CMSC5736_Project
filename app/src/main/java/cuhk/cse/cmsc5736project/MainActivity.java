package cuhk.cse.cmsc5736project;

import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.util.ArrayList;
import java.util.List;

import cuhk.cse.cmsc5736project.adapters.SmartFragmentStatePagerAdapter;
import cuhk.cse.cmsc5736project.fragment.DummyFragment;
import cuhk.cse.cmsc5736project.fragment.FriendsFragment;
import cuhk.cse.cmsc5736project.fragment.MapFragment;
import cuhk.cse.cmsc5736project.fragment.POIFragment;

public class MainActivity extends AppCompatActivity {

    // Application navigation related
    private NoSwipePager viewPager;
    private AHBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomNavBar();
        setupViewPager();
    }

    private void setupViewPager() {
        viewPager = (NoSwipePager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);

        BottomBarAdapter pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        // Map page
        Bundle bundle = new Bundle();
        bundle.putInt("color", fetchColor(R.color.color_tab_1));

        Fragment fragment = new MapFragment();
        fragment.setArguments(bundle);
        pagerAdapter.addFragments(fragment);

        // POI page
        bundle = new Bundle();
        bundle.putInt("color", fetchColor(R.color.color_tab_2));

        fragment = new POIFragment();
        fragment.setArguments(bundle);
        pagerAdapter.addFragments(fragment);

        // Map page
        bundle = new Bundle();
        bundle.putInt("color", fetchColor(R.color.color_tab_3));

        fragment = new FriendsFragment();
        fragment.setArguments(bundle);
        pagerAdapter.addFragments(fragment);

        // Set viewpager for the fragments
        viewPager.setAdapter(pagerAdapter);

        // Setup bottom navbar on tap behaviour
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (!wasSelected)
                    viewPager.setCurrentItem(position);

                return true;
            }
        });
    }

    private void setupBottomNavBar() {
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_maps_place, R.color.color_tab_1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_maps_local_bar, R.color.color_tab_2);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_maps_local_restaurant, R.color.color_tab_3);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        // Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        // Use colored navigation with circle reveal effect
        bottomNavigation.setColored(true);
        bottomNavigation.setTranslucentNavigationEnabled(true);

        // Set current item programmatically
        bottomNavigation.setCurrentItem(0);
    }

    public class BottomBarAdapter extends SmartFragmentStatePagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();

        public BottomBarAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Our custom method that populates this Adapter with Fragments
        public void addFragments(Fragment fragment) {
            fragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }
}
