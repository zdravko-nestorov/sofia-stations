package bg.znestorov.sofbus24.home.screen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import bg.znestorov.sofbus24.entity.AppThemeEnum;
import bg.znestorov.sofbus24.entity.ConfigEntity;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.HomeTabEntity;
import bg.znestorov.sofbus24.favorites.FavouritesStationFragment;
import bg.znestorov.sofbus24.main.EditTabs;
import bg.znestorov.sofbus24.main.EditTabsDialog;
import bg.znestorov.sofbus24.main.HomeScreenSelect;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.metro.MetroFragment;
import bg.znestorov.sofbus24.schedule.ScheduleFragment;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.ThemeChange;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;
import bg.znestorov.sofbus24.virtualboards.VirtualBoardsFragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.astuetz.PagerSlidingTabStrip;
import com.astuetz.PagerSlidingTabStrip.IconTabProvider;

public class Sofbus24Fragment extends SherlockFragment implements
		ActionBar.TabListener {

	private SherlockFragmentActivity context;
	private GlobalEntity globalContext;
	private ActionBar actionBar;

	private ViewPager mViewPager;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private PagerSlidingTabStrip mPagerSlidingTabs;

	private List<Fragment> fragmentsList = new ArrayList<Fragment>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View fragmentView = inflater.inflate(
				R.layout.activity_sofbus24_fragment, container, false);

		// Get the application and curren context;
		context = getSherlockActivity();
		globalContext = (GlobalEntity) context.getApplicationContext();
		LanguageChange.selectLocale(context);

		// Activate the option menu
		setHasOptionsMenu(true);

		return fragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initLayoutFields(getView());
	}

	@Override
	public void onResume() {
		super.onResume();
		actionsOverHomeScreen(-1);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (mViewPager != null) {
			int currentTab = mViewPager.getCurrentItem();
			Fragment currentFragment = fragmentsList.get(currentTab);

			MenuItem favouritesSort = menu
					.findItem(R.id.action_favourites_sort);
			MenuItem favouritesRemoveAll = menu
					.findItem(R.id.action_favourites_remove_all);
			MenuItem metroMapRoute = menu.findItem(R.id.action_metro_map_route);
			MenuItem metroScheduleSite = menu
					.findItem(R.id.action_metro_schedule_site);

			if (currentFragment instanceof FavouritesStationFragment) {
				favouritesSort.setVisible(true);
				favouritesRemoveAll.setVisible(true);
				metroMapRoute.setVisible(false);
				metroScheduleSite.setVisible(false);
			} else if (currentFragment instanceof MetroFragment) {
				favouritesSort.setVisible(false);
				favouritesRemoveAll.setVisible(false);
				metroMapRoute.setVisible(true);
				metroScheduleSite.setVisible(true);
			} else {
				favouritesSort.setVisible(false);
				favouritesRemoveAll.setVisible(false);
				metroMapRoute.setVisible(false);
				metroScheduleSite.setVisible(false);
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present
		inflater.inflate(R.menu.activity_sofbus24_actions, menu);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		actionsOverHomeScreen(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_droidtrans:
			ActivityUtils.startDroidTrans(context, getChildFragmentManager(),
					false);

			return true;
		case R.id.action_closest_stations_map:
			ActivityUtils.startClosestStationsMap(context,
					getChildFragmentManager(), false);

			return true;
		case R.id.action_edit_tabs:
			Intent editTabsIntent;
			if (globalContext.isPhoneDevice()) {
				editTabsIntent = new Intent(context, EditTabs.class);
			} else {
				editTabsIntent = new Intent(context, EditTabsDialog.class);
			}

			startActivity(editTabsIntent);

			return true;
		default:
			if (mViewPager != null) {
				Integer tabPosition = mViewPager.getCurrentItem();
				List<Fragment> sofbus24FragmentsList = getChildFragmentManager()
						.getFragments();

				if (sofbus24FragmentsList != null && tabPosition != null
						&& sofbus24FragmentsList.size() > tabPosition) {

					Fragment fakeFragment = fragmentsList.get(tabPosition);
					Fragment currentFragment = null;

					// Check which fragment from the child manager fragment list
					// is the current one. In case of FavouritesStationFragment
					// or MetroStationFragment, proceed with options menu
					if (fakeFragment instanceof FavouritesStationFragment) {
						for (Fragment fragment : sofbus24FragmentsList) {
							if (fragment != null
									&& fragment instanceof FavouritesStationFragment) {
								currentFragment = fragment;
							}
						}
					}

					if (fakeFragment instanceof MetroFragment) {
						for (Fragment fragment : sofbus24FragmentsList) {
							if (fragment != null
									&& fragment instanceof MetroFragment) {
								currentFragment = fragment;
							}
						}
					}

					// Check the type of the fragment
					if (currentFragment != null) {
						if (currentFragment instanceof SherlockFragment) {
							((SherlockFragment) currentFragment)
									.onOptionsItemSelected(item);
						}

						if (currentFragment instanceof SherlockListFragment) {
							((SherlockListFragment) currentFragment)
									.onOptionsItemSelected(item);
						}
					}
				}
			}

			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * This seems to be a bug in the newly added support for nested fragments.
	 * Basically, the child FragmentManager ends up with a broken internal state
	 * when it is detached from the activity. A short-term workaround that fixed
	 * it for me is to add the following to onDetach() of every Fragment which
	 * you call getChildFragmentManager() on:
	 * www.stackoverflow.com/questions/18977923/viewpager-with-nested-fragments
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Initialize the layout fields (ActionBar, ViewPager and
	 * SectionsPagerAdapter)
	 */
	private void initLayoutFields(View fragmentView) {

		// Set the tabs to the ActionBar
		actionBar = context.getSupportActionBar();
		actionBar.setTitle(getString(R.string.app_sofbus24));

		// Create the fragments list
		createFragmentsList();

		// Create the adapter that will return a fragment for each of the
		// primary sections of the application
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager());

		// Set up the ViewPager with the sections adapter and load all tabs at
		// once
		mPagerSlidingTabs = (PagerSlidingTabStrip) fragmentView
				.findViewById(R.id.sofbus24_tabs);
		if (ThemeChange.getAppTheme(context) == AppThemeEnum.DARK) {
			mPagerSlidingTabs
					.setTabBackground(R.color.app_dark_theme_tab_background);
		}

		mViewPager = (ViewPager) fragmentView.findViewById(R.id.sofbus24_pager);
		mViewPager
				.setOffscreenPageLimit(Constants.GLOBAL_PARAM_HOME_TABS_COUNT - 1);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// Assign the view pager to the SlidingTab pager
		mPagerSlidingTabs.setViewPager(mViewPager);

		// When swiping between the sections, select the corresponding tab
		if (!Utils.isInLandscapeMode(context)) {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			mPagerSlidingTabs.setVisibility(View.VISIBLE);

			mPagerSlidingTabs
					.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
						@Override
						public void onPageSelected(int position) {
							actionsOverHomeScreen(position);
						}
					});
		} else {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			mPagerSlidingTabs.setVisibility(View.GONE);

			mViewPager
					.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
						@Override
						public void onPageSelected(int position) {
							actionBar.setSelectedNavigationItem(position);
						}
					});
		}

		// For each of the sections in the app, add a tab to the action bar
		initTabs();
	}

	/**
	 * For each of the sections in the app, add a tab to the action bar
	 */
	private void initTabs() {
		if (actionBar.getTabCount() > 0) {
			actionBar.removeAllTabs();
		}

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setIcon(mSectionsPagerAdapter.getPageIconResId(i))
					.setTabListener(this));
		}
	}

	/**
	 * Create or rearrange (if already created) the FragmentsList, using the
	 * current application config file
	 */
	private void createFragmentsList() {
		// Get the application cofig file
		ConfigEntity config = new ConfigEntity(context);

		// Emtpy the fragmentsList if contains any elements
		if (!fragmentsList.isEmpty()) {
			fragmentsList.clear();
		}

		// Create a new ordered list with fragments (according to the
		// configuration file)
		for (int i = 0; i < Constants.GLOBAL_PARAM_HOME_TABS_COUNT; i++) {
			HomeTabEntity homeTab = config.getTabByPosition(context, i);
			if (homeTab.isTabVisible()) {
				fragmentsList.add(getFragmentByTagName(homeTab));
			}
		}
	}

	/**
	 * Get the fragment according to the given HomeTab
	 * 
	 * @param homeTab
	 *            HomeTab object pointing which fragment to be choosen
	 * @return the fragment associated to the given HomeTab
	 */
	private Fragment getFragmentByTagName(HomeTabEntity homeTab) {
		Fragment fragment;

		String homeTabName = homeTab.getTabName();
		if (homeTabName.equals(getString(R.string.edit_tabs_favourites))) {
			fragment = FavouritesStationFragment.getInstance(true);
		} else if (homeTabName.equals(getString(R.string.edit_tabs_search))) {
			fragment = new VirtualBoardsFragment();
		} else if (homeTabName.equals(getString(R.string.edit_tabs_schedule))) {
			fragment = new ScheduleFragment();
		} else {
			fragment = new MetroFragment();
		}

		return fragment;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter
			implements IconTabProvider {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentsList.get(position);
		}

		/**
		 * Purpose of this method is to check whether an item in the adapter
		 * still exists in the dataset and where it should show. For each entry
		 * in dataset, request its Fragment.
		 * 
		 * If the Fragment is found, return its (new) position. There's no need
		 * to return POSITION_UNCHANGED; ViewPager handles it.
		 * 
		 * If the Fragment passed to this method is not found, remove all
		 * references and let the ViewPager remove it from display by by
		 * returning POSITION_NONE;
		 */
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return fragmentsList.size();
		}

		@Override
		public int getPageIconResId(int position) {
			return getPageIconByTagName(fragmentsList.get(position));
		}

		/**
		 * Get the current item page icon according to the fragment type
		 * 
		 * @param fragment
		 *            the fragment set on this tab
		 * @return the icon associated to the given fragment
		 */
		private int getPageIconByTagName(Fragment fragment) {
			int pageIcon;

			if (fragment instanceof FavouritesStationFragment) {
				pageIcon = R.drawable.ic_tab_favorites;
			} else if (fragment instanceof VirtualBoardsFragment) {
				pageIcon = R.drawable.ic_tab_real_time;
			} else if (fragment instanceof ScheduleFragment) {
				pageIcon = R.drawable.ic_tab_schedule;
			} else {
				pageIcon = R.drawable.ic_tab_metro;
			}

			return pageIcon;
		}
	}

	/**
	 * Proceed with updating the HomeScreen if needed
	 * 
	 * @param tabPosition
	 *            the tabPosition that is pressed or "-1" in case of onResume
	 */
	private void actionsOverHomeScreen(int tabPosition) {

		// Check if this is called from "onResume(...)", so take the current
		// active tab or from "onTabSelected(...)", so set the according menu
		// items
		if (tabPosition == -1) {
			// Check if the activity has to be restarted
			if (globalContext.isHasToRestart()) {
				context.setResult(HomeScreenSelect.RESULT_CODE_ACTIVITY_RESTART);
				context.finish();

				return;
			}

			// Check if the ordering and visibility of the tabs should be
			// changed
			if (globalContext.isHomeScreenChanged()) {
				// Rearrange the fragmentsList
				createFragmentsList();

				// Notify the adapters for the changes in the
				// fragmentsList
				mSectionsPagerAdapter.notifyDataSetChanged();
				mPagerSlidingTabs.notifyDataSetChanged();

				// For each of the sections in the application, add a
				// tab to the ActionBar
				initTabs();

				// Show a message that the home screen is changed
				Toast.makeText(context, getString(R.string.edit_tabs_toast),
						Toast.LENGTH_SHORT).show();

				// Reset to default
				globalContext.setHomeScreenChanged(false);

				return;
			}

			// Check if the view pager is already created (if the application
			// has just started)
			if (mViewPager != null) {
				tabPosition = mViewPager.getCurrentItem();
			}
		} else {
			// Declare that the options menu has changed, so should be recreated
			// (make the system calls the method onPrepareOptionsMenu)
			context.supportInvalidateOptionsMenu();

			// When the given tab is selected, switch to the corresponding page
			// in the ViewPager.
			mViewPager.setCurrentItem(tabPosition);
		}

		// Get the Fragment from the fragmentsList (used to check what type is
		// the current fragment. It doesn't store the real fragment - it will be
		// taken from the FragmentManager)
		if (mViewPager != null) {
			Fragment fakeFragment = fragmentsList.get(tabPosition);
			if (fakeFragment instanceof FavouritesStationFragment) {
				ActivityUtils.setHomeScreenActionBarSubtitle(context,
						actionBar, getString(R.string.edit_tabs_favourites),
						getString(R.string.edit_tabs_favourites_pre_honeycomb));
			} else if (fakeFragment instanceof VirtualBoardsFragment) {
				ActivityUtils.setHomeScreenActionBarSubtitle(context,
						actionBar, getString(R.string.edit_tabs_search),
						getString(R.string.edit_tabs_search_pre_honeycomb));
			} else if (fakeFragment instanceof ScheduleFragment) {
				ActivityUtils.setHomeScreenActionBarSubtitle(context,
						actionBar, getString(R.string.edit_tabs_schedule),
						getString(R.string.edit_tabs_schedule_pre_honeycomb));
			} else {
				ActivityUtils.setHomeScreenActionBarSubtitle(context,
						actionBar, getString(R.string.edit_tabs_metro),
						getString(R.string.edit_tabs_metro_pre_honeycomb));
			}

			// Check if the FragmentManager is created and proceed with actions
			// for each fragment (updates)
			if (getChildFragmentManager().getFragments() != null) {
				List<Fragment> fmFragmentsList = getChildFragmentManager()
						.getFragments();

				if (fakeFragment instanceof FavouritesStationFragment
						&& globalContext.isFavouritesChanged()) {

					// Match the fake fragment from the fragmentsList with the
					// one from the FragmentManager
					for (Fragment fragment : fmFragmentsList) {
						if (fragment instanceof FavouritesStationFragment) {
							((FavouritesStationFragment) fragment)
									.onResumeFragment(context);
							globalContext.setFavouritesChanged(false);
						}
					}
				}

				if (fakeFragment instanceof VirtualBoardsFragment
						&& globalContext.isVbChanged()) {

					// Match the fake fragment from the fragmentsList with the
					// one from the FragmentManager
					for (Fragment fragment : fmFragmentsList) {
						if (fragment instanceof VirtualBoardsFragment) {
							((VirtualBoardsFragment) fragment)
									.onResumeFragment(context);
							globalContext.setVbChanged(false);
						}
					}
				}
			}
		}
	}
}
