package bg.znestorov.sofbus24.closest.stations.map;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Handler;

/**
 * Runnable class used to cancel the AsyncTask after a few seconds - defined on
 * object creation (if no location is found)
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class RetrieveCurrentLocationTimeout implements Runnable {

	private AsyncTask<Void, Void, Void> retrieveCurrentLocation;
	private Integer timeout;

	public static final Integer TIMEOUT_CS_MAP_INIT = 6000;
	public static final Integer TIMEOUT_CS_LIST = 6000;
	public static final Integer TIMEOUT_DT_HOME_SCREEN = 1750;
	public static final Integer TIMEOUT_DT = 3000;

	public RetrieveCurrentLocationTimeout(
			AsyncTask<Void, Void, Void> retrieveCurrentLocation, Integer timeout) {

		this.retrieveCurrentLocation = retrieveCurrentLocation;
		this.timeout = timeout;
	}

	@Override
	public void run() {
		mHandler.postDelayed(runnable, timeout);
	}

	Handler mHandler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (retrieveCurrentLocation.getStatus() == Status.RUNNING
					|| retrieveCurrentLocation.getStatus() == Status.PENDING) {
				retrieveCurrentLocation.cancel(true);
			}
		}
	};
}