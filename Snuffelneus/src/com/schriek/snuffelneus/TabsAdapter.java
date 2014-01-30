package com.schriek.snuffelneus;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Mooie adapter van StackOverflow, preciese werking weet ik er niet van (ja toch)
 * @author Schriek
 *
 */
public class TabsAdapter extends FragmentPagerAdapter implements
		ActionBar.TabListener, ViewPager.OnPageChangeListener {
	private final Context mContext;
	private final ActionBar mActionBar;
	private final ViewPager mViewPager;
	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

	private final List<Fragment> fragments = new ArrayList<Fragment>();

	private MetingPage meting;
	private MapActivity map;
	private ListLogger logger;
	private InfoPage info;
	
	static final class TabInfo {
		private final Class<?> clss;
		private final Bundle args;

		TabInfo(Class<?> _class, Bundle _args) {
			clss = _class;
			args = _args;
		}
	}

	public TabsAdapter(FragmentActivity activity, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mContext = activity;
		mActionBar = activity.getActionBar();
		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
		
		meting = new MetingPage();
		map = new MapActivity();
		logger = new ListLogger(mContext);
		info = new InfoPage();
	}

	public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args,
			int position, boolean setSelected) {
		TabInfo info = new TabInfo(clss, args);
		tab.setTag(info);
		tab.setTabListener(this);
		mTabs.add(info);
		mActionBar.addTab(tab, position, setSelected);
		// mActionBar.addTab(tab);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;

		switch (position) {
		case MainActivityPager.LOGGER:
			return logger;
		case MainActivityPager.STARTPAGINA:
			return meting;
		case MainActivityPager.MAP:
			return map;
		case MainActivityPager.INFO:
			return info;
		}

		return fragment;
		
//		TabInfo info = mTabs.get(position);
//		Fragment fr = Fragment.instantiate(
//		// addFragment (fr, position);
//		return fr;

	}

	public void addFragment(Fragment f, int location) {

		if (fragments.size() == 0)
			fragments.add(f);
		else
			fragments.add(location, f);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		mActionBar.setSelectedNavigationItem(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		Object tag = tab.getTag();
		for (int i = 0; i < mTabs.size(); i++) {
			if (mTabs.get(i) == tag) {
				// updateDatasetMovies (i);
				mViewPager.setCurrentItem(i);
			}
		}
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}
}