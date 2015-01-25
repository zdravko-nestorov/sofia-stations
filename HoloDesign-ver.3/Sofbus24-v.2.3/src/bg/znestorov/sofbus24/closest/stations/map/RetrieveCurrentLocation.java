package bg.znestorov.sofbus24.closest.stations.map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.RetrieveCurrentLocationTypeEnum;
import bg.znestorov.sofbus24.main.ClosestStationsList;
import bg.znestorov.sofbus24.main.ClosestStationsListDialog;
import bg.znestorov.sofbus24.main.ClosestStationsMap;
import bg.znestorov.sofbus24.main.DroidTrans;
import bg.znestorov.sofbus24.main.DroidTransDialog;
import bg.znestorov.sofbus24.main.HomeScreenSelect;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class responsible for AsyncLoad of the current location
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class RetrieveCurrentLocation extends AsyncTask<Void, Void, Void> {

	private FragmentActivity context;
	private GlobalEntity globalContext;

	private ProgressDialog progressDialog;
	private RetrieveCurrentLocationTypeEnum retrieveCurrentLocationType;

	// Default latitude and longitude
	private double latitude = 0.0;
	private double longitude = 0.0;

	// Location Managers responsible for the current location
	private LocationManager locationManager;
	private MyLocationListener myNetworkLocationListener;
	private MyLocationListener myGPSLocationListener;

	// Available Location providers
	private boolean isLocationServicesAvailable;
	private boolean isAnyProviderEabled;

	// Different location providers
	private static final String GPS_PROVIDER = LocationManager.GPS_PROVIDER;
	private static final String NETWORK_PROVIDER = LocationManager.NETWORK_PROVIDER;

	// The minimum distance and time to for the location updates
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 2;

	public RetrieveCurrentLocation(FragmentActivity context,
			ProgressDialog progressDialog,
			RetrieveCurrentLocationTypeEnum retrieveCurrentLocationType) {

		this.context = context;
		this.globalContext = (GlobalEntity) context.getApplicationContext();

		this.progressDialog = progressDialog;
		this.retrieveCurrentLocationType = retrieveCurrentLocationType;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		createLoadingView();

		String locationProviders = Settings.Secure.getString(
				context.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		isLocationServicesAvailable = locationProviders
				.contains(LocationManager.NETWORK_PROVIDER)
				|| locationProviders.contains(LocationManager.GPS_PROVIDER);

		try {
			if (isLocationServicesAvailable) {
				locationManager = (LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE);
				registerForLocationUpdates();
			}
		} catch (Exception e) {
			isAnyProviderEabled = false;
		}
	}

	@Override
	protected Void doInBackground(Void... params) {

		while (latitude == 0.0 && longitude == 0.0) {

			// In case of all providers are disabled - dissmiss the progress
			// dialog (if any) and cancel the async task
			if (!isAnyProviderEabled) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				cancel(true);
			}

			// In case the progress dialog is dissmissed - cancel the async task
			if (progressDialog != null && !progressDialog.isShowing()) {
				cancel(true);
			}

			// If the async task is cancelled stop the loop (this is needed
			// because of the RetrieveCurrentLocationTimeout async)
			if (isCancelled()) {
				break;
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		actionsOnLocationFound();
		dismissLoadingView();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		actionsOnCancelled();
		dismissLoadingView();
	}

	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			registerForLocationUpdates();
		}

		@Override
		public void onProviderEnabled(String provider) {
			registerForLocationUpdates();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			registerForLocationUpdates();
		}
	}

	/**
	 * Create the loading view and lock the screen
	 */
	private void createLoadingView() {
		ActivityUtils.lockScreenOrientation(context);

		if (progressDialog != null) {
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
	}

	/**
	 * Dismiss the loading view and unlock the screen
	 */
	private void dismissLoadingView() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}

		if (locationManager != null) {
			if (myNetworkLocationListener != null) {
				locationManager.removeUpdates(myNetworkLocationListener);
			}

			if (myGPSLocationListener != null) {
				locationManager.removeUpdates(myGPSLocationListener);
			}
		}

		ActivityUtils.unlockScreenOrientation(context);
	}

	/**
	 * Check if any of the providers is enabled
	 * 
	 * @return if any provider is ebanled
	 */
	private void registerForLocationUpdates() {

		if (locationManager != null) {
			// Getting the GPS status
			boolean isGPSEnabled = locationManager
					.isProviderEnabled(GPS_PROVIDER);

			// Getting the network status
			boolean isNetworkEnabled = locationManager
					.isProviderEnabled(NETWORK_PROVIDER);

			// Check if any of the providers is enabled
			isAnyProviderEabled = isGPSEnabled || isNetworkEnabled;

			if (isAnyProviderEabled) {
				if (isNetworkEnabled) {
					if (myNetworkLocationListener == null) {
						myNetworkLocationListener = new MyLocationListener();

						locationManager.requestLocationUpdates(
								NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES,
								myNetworkLocationListener);
					}
				} else {
					if (myNetworkLocationListener != null) {
						locationManager
								.removeUpdates(myNetworkLocationListener);
						myNetworkLocationListener = null;
					}
				}

				if (isGPSEnabled) {
					if (myGPSLocationListener == null) {
						myGPSLocationListener = new MyLocationListener();

						locationManager.requestLocationUpdates(GPS_PROVIDER,
								MIN_TIME_BETWEEN_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES,
								myGPSLocationListener);
					}
				} else {
					if (myGPSLocationListener != null) {
						locationManager.removeUpdates(myGPSLocationListener);
						myGPSLocationListener = null;
					}
				}
			}
		}
	}

	/**
	 * Show a toast for a long period of time
	 * 
	 * @param message
	 *            the message of the toast
	 */
	private void showLongToast(String message) {
		ActivityUtils.showLongToast(context, message, 3000, 1000);
	}

	/**
	 * Actions when any location is found
	 */
	private void actionsOnLocationFound() {

		// Check if the location is available
		if (isLocationServicesAvailable) {

			// Check which activity called the async task
			switch (retrieveCurrentLocationType) {
			case CS_MAP_INIT:
				startClosestStationsMapActivity();
				break;
			case CS_LIST_INIT:
				startClosestStationsListActivity();
				break;
			case CS_LIST_REFRESH:
				refreshClosestStationsListActivity();
				break;
			case DT_HOME_SCREEN:
			case DT_INIT:
				startDroidTransActivity();
				break;
			default:
				refreshDroidTransActivity();
				break;
			}
		} else {
			try {
				DialogFragment dialogFragment = new LocationSourceDialog();
				dialogFragment.show(context.getSupportFragmentManager(),
						"dialogFragment");
			} catch (Exception e) {
				/**
				 * Fixing a strange error that is happening sometimes when the
				 * dialog is created. I guess sometimes the activity gets
				 * destroyed before the dialog successfully be shown.
				 * 
				 * java.lang.IllegalStateException: Activity has been destroyed
				 */
			}
		}
	}

	/**
	 * Start the ClosestStationsMap activity with all needed information
	 */
	private void startClosestStationsMapActivity() {
		Intent closestStationsMapIntent = new Intent(context,
				ClosestStationsMap.class);
		context.startActivity(closestStationsMapIntent);
	}

	/**
	 * Start the ClosestStationsList activity with all needed information
	 */
	private void startClosestStationsListActivity() {

		LatLng currentLocation = new LatLng(this.latitude, this.longitude);

		Intent closestStationsListIntent;
		if (globalContext.isPhoneDevice()) {
			closestStationsListIntent = new Intent(context,
					ClosestStationsList.class);
		} else {
			closestStationsListIntent = new Intent(context,
					ClosestStationsListDialog.class);
		}

		closestStationsListIntent.putExtra(
				Constants.BUNDLE_CLOSEST_STATIONS_LIST, currentLocation);
		context.startActivity(closestStationsListIntent);
	}

	/**
	 * Refresh the ClosestStationsList activity
	 */
	private void refreshClosestStationsListActivity() {
		((ClosestStationsList) context).refreshClosestStationsListFragment();
	}

	/**
	 * Start the DroidTrans activity with all needed information
	 */
	private void startDroidTransActivity() {

		LatLng currentLocation = new LatLng(this.latitude, this.longitude);

		Intent droidTransIntent;
		if (globalContext.isPhoneDevice()) {
			droidTransIntent = new Intent(context, DroidTrans.class);
		} else {
			droidTransIntent = new Intent(context, DroidTransDialog.class);
		}

		switch (retrieveCurrentLocationType) {
		case DT_HOME_SCREEN:
			droidTransIntent.putExtra(
					DroidTrans.BUNDLE_IS_DROID_TRANS_HOME_SCREEN, true);
			droidTransIntent.putExtra(Constants.BUNDLE_DROID_TRANS,
					currentLocation);

			context.startActivityForResult(droidTransIntent,
					HomeScreenSelect.REQUEST_CODE_HOME_SCREEN_SELECT);
			break;
		default:
			droidTransIntent.putExtra(Constants.BUNDLE_DROID_TRANS,
					currentLocation);

			context.startActivity(droidTransIntent);
			break;
		}
	}

	/**
	 * Refresh the DroidTrans activity
	 */
	private void refreshDroidTransActivity() {

		Location currentLocation;
		if (latitude != 0.0 && longitude != 0.0) {
			currentLocation = new Location("");
			currentLocation.setLatitude(latitude);
			currentLocation.setLongitude(longitude);
		} else {
			currentLocation = null;
		}

		((DroidTrans) context).refreshDroidTransActivity(currentLocation);
	}

	/**
	 * Actions on cancelled the async task
	 */
	private void actionsOnCancelled() {

		Location lastKnownLocation = null;
		if (locationManager != null) {
			lastKnownLocation = locationManager
					.getLastKnownLocation(GPS_PROVIDER) == null ? locationManager
					.getLastKnownLocation(NETWORK_PROVIDER) == null ? null
					: locationManager.getLastKnownLocation(NETWORK_PROVIDER)
					: locationManager.getLastKnownLocation(GPS_PROVIDER);
		}

		// Check if there is any last known location
		if (lastKnownLocation == null) {

			switch (retrieveCurrentLocationType) {
			case CS_MAP_INIT:
				if (!isAnyProviderEabled) {
					showLongToast(context
							.getString(R.string.app_location_modules_error));
				} else if (progressDialog.isShowing()) {
					showLongToast(context
							.getString(R.string.app_location_timeout_map_error));
				}

				// Timeout has interrupted the ClosestStationMap startup, so
				// just inform the user and start the map fragment (it will take
				// care for the rest)
				if (progressDialog.isShowing()) {
					Intent closestStationsMapIntent = new Intent(context,
							ClosestStationsMap.class);
					context.startActivity(closestStationsMapIntent);
				}
				break;
			case CS_LIST_INIT:
				if (!isAnyProviderEabled) {
					showLongToast(context
							.getString(R.string.app_location_modules_error));
				} else if (progressDialog.isShowing()) {
					showLongToast(context
							.getString(R.string.app_location_timeout_error));
				}

				break;
			case CS_LIST_REFRESH:
				((ClosestStationsList) context)
						.refreshClosestStationsListFragmentFailed();
				showLongToast(context
						.getString(R.string.app_location_modules_timeout_error));

				break;
			case DT_HOME_SCREEN:
				startDroidTransActivity();
				break;
			case DT_INIT:
				if (!isAnyProviderEabled) {
					showLongToast(context
							.getString(R.string.app_location_modules_error));
				}

				// Timeout has interrupted the DroidTrans startup, so just
				// inform the user and start the DroidTrans activity
				if (progressDialog.isShowing()) {
					startDroidTransActivity();
				}

				break;
			default:
				if (progressDialog.isShowing()) {
					showLongToast(context
							.getString(R.string.app_location_modules_timeout_error));
					refreshDroidTransActivity();
				}

				break;
			}
		} else {
			actionsOnLocationFound();
		}
	}

}