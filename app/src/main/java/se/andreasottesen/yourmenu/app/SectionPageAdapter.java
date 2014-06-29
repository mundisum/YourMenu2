package se.andreasottesen.yourmenu.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Andreas on 2014-06-18.
 */
public class SectionPageAdapter extends FragmentPagerAdapter {

    public SectionPageAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return ItemListFragment.newInstance(0);
            case 1: return VendorListFragment.newInstance(1);
            case 2: return MapViewFragment.newInstance(2);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Items";
            case 1: return "Restaurants";
            case 2: return "Map";
        }
        return null;
    }
}
