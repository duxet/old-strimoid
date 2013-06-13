package com.duxet.strimoid.ui;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;

public class TabsAdapter extends FragmentPagerAdapter implements TabListener, ViewPager.OnPageChangeListener {
    
    private final SherlockFragmentActivity mActivity;
    private final ActionBar mActionBar;
    private final ViewPager mPager;
    
    private ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>(Arrays.asList(new Fragment[5]));

    public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
        super(activity.getSupportFragmentManager());
        this.mActivity = activity;
        this.mActionBar = activity.getSupportActionBar();
        this.mPager = pager;
        mActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
    }

    private static class TabInfo {
        public final Class<?> fragmentClass;
        public final Bundle args;
        public TabInfo(Class<?> fragmentClass,
                Bundle args) {
            this.fragmentClass = fragmentClass;
            this.args = args;
        }
    }

    public void addTab( CharSequence title, Class<?> fragmentClass, Bundle args ) {
        final TabInfo tabInfo = new TabInfo( fragmentClass, args );

        Tab tab = mActionBar.newTab();
        tab.setText( title );
        tab.setTabListener( this );
        tab.setTag( tabInfo );

        mTabs.add( tabInfo );

        mActionBar.addTab( tab );
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments.get(position) == null) {
            final TabInfo tabInfo = (TabInfo) mTabs.get( position );
            mFragments.set(position, Fragment.instantiate( mActivity, tabInfo.fragmentClass.getName(), tabInfo.args ));
        }
        
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }
    
    public ArrayList<Fragment> getFragments() {
        return mFragments;
    }

    public Fragment getCurrentFragment() {
        return this.getItem(mActionBar.getSelectedNavigationIndex());
    }
    
    public void onPageScrollStateChanged(int arg0) {
    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    public void onPageSelected(int position) {
        mActionBar.setSelectedNavigationItem( position );
    }

    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        TabInfo tabInfo = (TabInfo) tab.getTag();
        for ( int i = 0; i < mTabs.size(); i++ ) {
            if ( mTabs.get( i ) == tabInfo ) {
                mPager.setCurrentItem( i );
            }
        }
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
}
