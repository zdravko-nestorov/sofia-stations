package bg.znestorov.sofbus24.main;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.publictransport.PublicTransportFragment;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;

public class PublicTransport extends FragmentActivity implements
		ActionBar.TabListener {

	private Activity context;
	private ActionBar actionBar;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	private int activeDirection;
	private DirectionsEntity ptDirectionsEntity;
	private ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LanguageChange.selectLocale(this);
		setContentView(R.layout.activity_public_transport);

		// Get the current activity context
		context = PublicTransport.this;

		initBundleInfo();
		initLayoutFields();
		setActiveFragment();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.activity_public_transport_actions,
				menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_pt_route_map:
			ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog
					.setMessage(getString(R.string.pt_menu_map_route_loading));
			RetrievePTRoute retrievePTRoute = new RetrievePTRoute(context,
					progressDialog);
			retrievePTRoute.execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	/**
	 * Get the current location coordinates from the Bundle object
	 */
	private void initBundleInfo() {
		Bundle extras = getIntent().getExtras();
		ptDirectionsEntity = (DirectionsEntity) extras
				.get(Constants.BUNDLE_PUBLIC_TRANSPORT_SCHEDULE);
		activeDirection = ptDirectionsEntity.getActiveDirection();
	}

	/**
	 * Initialize the layout fields (ActionBar, ViewPager and
	 * SectionsPagerAdapter)
	 */
	private void initLayoutFields() {
		// Set up the ActionBar
		actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.pt_title));
		actionBar.setSubtitle(getSubtitle());
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Create the fragments list
		createFragmentsList();

		// Create the adapter that will return a fragment for each of the
		// primary sections of the application
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter and load all tabs at
		// once
		mViewPager = (ViewPager) findViewById(R.id.pt_pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getTabName(i))
					.setTabListener(this));
		}
	}

	/**
	 * Create the ActionBar subtitle using the vehicle type and number
	 * 
	 * @return the actionBar subtitle in format - [VehicleType] �[VehicleNumber]
	 */
	private String getSubtitle() {
		Vehicle vehicle = ptDirectionsEntity.getVehicle();
		String subtitle;

		switch (vehicle.getType()) {
		case BUS:
			subtitle = String.format(getString(R.string.pt_bus),
					vehicle.getNumber());
			break;
		case TROLLEY:
			subtitle = String.format(getString(R.string.pt_trolley),
					vehicle.getNumber());
			break;
		case TRAM:
			subtitle = String.format(getString(R.string.pt_tram),
					vehicle.getNumber());
			break;
		default:
			subtitle = String.format(getString(R.string.pt_bus),
					vehicle.getNumber());
			break;
		}

		return subtitle;
	}

	/**
	 * Create the FragmentsList, where each element contains a separate
	 * direction
	 */
	private void createFragmentsList() {
		DirectionsEntity ptDirectionsEntity1 = new DirectionsEntity(
				ptDirectionsEntity, 0);
		fragmentsList.add(PublicTransportFragment
				.newInstance(ptDirectionsEntity1));

		DirectionsEntity ptDirectionsEntity2 = new DirectionsEntity(
				ptDirectionsEntity, 1);
		fragmentsList.add(PublicTransportFragment
				.newInstance(ptDirectionsEntity2));
	}

	/**
	 * Set the active fragment to be firstly visible (the chosen from the
	 * AlertDialog)
	 */
	private void setActiveFragment() {
		mViewPager.setCurrentItem(activeDirection);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentsList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentsList.size();
		}

		public String getTabName(int i) {
			String tabName;

			switch (i) {
			case 0:
				tabName = ptDirectionsEntity.getDirectionsNames().get(0);
				break;
			case 1:
				tabName = ptDirectionsEntity.getDirectionsNames().get(1);
				break;
			default:
				tabName = ptDirectionsEntity.getDirectionsNames().get(0);
				break;
			}

			return tabName;
		}
	}

	/**
	 * Asynchronous class used for retrieving the Public Transport route
	 * 
	 * @author Zdravko Nestorov
	 */
	public class RetrievePTRoute extends AsyncTask<Void, Void, Intent> {

		private Activity context;
		private ProgressDialog progressDialog;

		public RetrievePTRoute(Activity context, ProgressDialog progressDialog) {
			this.context = context;
			this.progressDialog = progressDialog;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						public void onCancel(DialogInterface dialog) {
							cancel(true);
						}
					});
			progressDialog.show();
		}

		@Override
		protected Intent doInBackground(Void... params) {
			Intent ptMapRouteIntent = new Intent(context, StationRouteMap.class);
			DirectionsEntity ptDirectionsEntityTransfer = new DirectionsEntity(
					ptDirectionsEntity, mViewPager.getCurrentItem());
			ptMapRouteIntent.putExtra(Constants.BUNDLE_STATION_ROUTE_MAP,
					ptDirectionsEntityTransfer);

			return ptMapRouteIntent;
		}

		@Override
		protected void onPostExecute(Intent ptMapRouteIntent) {
			super.onPostExecute(ptMapRouteIntent);

			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				// Workaround used just in case the orientation is changed once
				// retrieving info
			}

			context.startActivity(ptMapRouteIntent);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				// Workaround used just in case when this activity is destroyed
				// before the dialog
			}
		}
	}
}