package bg.znestorov.sofbus24.utils.activity;

import android.app.Activity;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.GlobalEntity.TrackerName;
import bg.znestorov.sofbus24.entity.HtmlRequestCodesEnum;
import bg.znestorov.sofbus24.navigation.NavDrawerHomeScreenPreferences;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

/**
 * Class used to interact with GoogleAnalytivs
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ActivityTracker {

	/**
	 * Send an event to GoogleAnalytics on HomeScreen selection
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void selectedHomeScreen(Activity context) {

		// Get tracker
		Tracker tracker = ((GlobalEntity) context.getApplicationContext())
				.getTracker(TrackerName.APP_TRACKER);

		// Build and send an Event
		tracker.send(new HitBuilders.EventBuilder()
				.setCategory("Home Screen")
				.setAction("homeScreenSelect")
				.setLabel(
						"homeScreenSelect: "
								+ NavDrawerHomeScreenPreferences
										.getUserHomeScreenChoice(context))
				.build());
	}

	/**
	 * Send a screen view to GoogleAnalytics on which home screen is started on
	 * app startup
	 * 
	 * @param context
	 *            the current activity context
	 * @param screenName
	 *            the screen name
	 */
	public static void homeScreenUsed(Activity context, String screenName) {

		// Get tracker
		Tracker t = ((GlobalEntity) context.getApplicationContext())
				.getTracker(TrackerName.APP_TRACKER);

		// Set screen name
		t.setScreenName(screenName);

		// Send a screen view
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	/**
	 * Send a caught exception to GoogleAnalytics
	 * 
	 * @param context
	 *            the current activity context
	 * @param methodName
	 *            the name of the method
	 * @param msg
	 *            the user message
	 * @param e
	 *            the exception
	 */
	public static void sendCaughtException(Activity context, String methodName,
			String msg, Exception e) {

		// Get tracker
		Tracker tracker = ((GlobalEntity) context.getApplicationContext())
				.getTracker(TrackerName.APP_TRACKER);

		// Build and send exception
		tracker.send(new HitBuilders.ExceptionBuilder()
				.setDescription(
						"["
								+ methodName
								+ "] "
								+ msg
								+ ":\n"
								+ new StandardExceptionParser(context, null)
										.getDescription(Thread.currentThread()
												.getName(), e)).setFatal(false)
				.build());
	}

	/**
	 * Send an event to GoogleAnalytics on VirtualBoards search
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void queriedVirtualBoardsInformation(Activity context) {

		// Get tracker
		Tracker tracker = ((GlobalEntity) context.getApplicationContext())
				.getTracker(TrackerName.APP_TRACKER);

		// Build and send an Event
		tracker.send(new HitBuilders.EventBuilder()
				.setCategory("Virtual Boards Info")
				.setAction("queryVirtualBoards").setLabel("queryVirtualBoards")
				.build());
	}

	/**
	 * Send an event to GoogleAnalytics on VirtualBoards search
	 * 
	 * @param context
	 *            the current activity context
	 * @param htmlRequestCode
	 *            the request code of the user call
	 */
	public static void queriedVirtualBoardsInformationType(Activity context,
			HtmlRequestCodesEnum htmlRequestCode) {

		// Get tracker
		Tracker tracker = ((GlobalEntity) context.getApplicationContext())
				.getTracker(TrackerName.APP_TRACKER);

		// Build and send an Event
		String label = "queryVirtualBoards" + htmlRequestCode;
		tracker.send(new HitBuilders.EventBuilder()
				.setCategory("Virtual Boards Info [" + htmlRequestCode + "]")
				.setAction(label).setLabel(label).build());
	}

	/**
	 * Send an event to GoogleAnalytics on schedule search
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void queriedScheduleInformation(Activity context) {

		// Get tracker
		Tracker tracker = ((GlobalEntity) context.getApplicationContext())
				.getTracker(TrackerName.APP_TRACKER);

		// Build and send an Event
		tracker.send(new HitBuilders.EventBuilder()
				.setCategory("Schedule Info").setAction("querySchedule")
				.setLabel("querySchedule").build());
	}

	/**
	 * Send an event to GoogleAnalytics on metro schedule search
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void queriedMetroInformation(Activity context) {

		// Get tracker
		Tracker tracker = ((GlobalEntity) context.getApplicationContext())
				.getTracker(TrackerName.APP_TRACKER);

		// Build and send an Event
		tracker.send(new HitBuilders.EventBuilder().setCategory("Metro Info")
				.setAction("queryMetro").setLabel("queryMetro").build());
	}

}