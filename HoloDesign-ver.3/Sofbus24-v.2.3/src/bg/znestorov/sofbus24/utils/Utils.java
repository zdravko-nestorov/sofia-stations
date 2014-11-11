package bg.znestorov.sofbus24.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.history.HistoryOfSearches;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.updates.check.CheckForUpdatesAsync;
import bg.znestorov.sofbus24.updates.check.CheckForUpdatesPreferences;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Utility class
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class Utils {

	/**
	 * Check if the input is a number
	 * 
	 * @param input
	 *            the input string
	 * @return if the input is a number
	 */
	public static boolean isNumeric(String input) {
		try {
			Double.parseDouble(input);
		} catch (NumberFormatException nfe) {
			return false;
		}

		return true;
	}

	/**
	 * Function that extracts only digits from a given String. In case of an
	 * empty string - return "".
	 * 
	 * @param value
	 *            the input String
	 * @return the digits from the String
	 */
	public static String getOnlyDigits(String value) {
		if (value != null && !"".equals(value)) {
			value = value.replaceAll("\\D+", "");
		} else {
			value = "";
		}

		return value;
	}

	/**
	 * Function that remove all whitespaces and non visible characters such as
	 * tab, \n from a string text. In case of an empty string - return "".
	 * 
	 * @param value
	 *            the input String
	 * @return the digits from the String
	 */
	public static String removeSpaces(String value) {
		if (value != null && !"".equals(value)) {
			value = value.replaceAll("\\s+", "");
		} else {
			value = "";
		}

		return value;
	}

	/**
	 * Get a value from a string BEFORE some REGEX
	 * 
	 * @param value
	 *            the string value
	 * @param regex
	 *            the regex that is looked for
	 * @return the substring value BEFORE the REGEX, or the value in case of no
	 *         REGEX found
	 */
	public static String getValueBefore(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(0, value.indexOf(regex));
		} else {
			return value;
		}
	}

	/**
	 * Get a value from a string BEFORE some REGEX (LAST)
	 * 
	 * @param value
	 *            the string value
	 * @param regex
	 *            the regex that is looked for
	 * @return the substring value BEFORE the REGEX (LAST), or the value in case
	 *         of no REGEX found
	 */
	public static String getValueBeforeLast(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(0, value.lastIndexOf(regex));
		} else {
			return value;
		}
	}

	/**
	 * Get a value from a string AFTER some REGEX
	 * 
	 * @param value
	 *            the string value
	 * @param regex
	 *            the regex that is looked for
	 * @return the substring value AFTER the REGEX, or the value in case of no
	 *         REGEX found
	 */
	public static String getValueAfter(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(value.indexOf(regex) + regex.length());
		} else {
			return value;
		}
	}

	/**
	 * Get a value from a string AFTER some REGEX (LAST)
	 * 
	 * @param value
	 *            the string value
	 * @param regex
	 *            the regex that is looked for
	 * @return the substring value AFTER the REGEX (LAST), or the value in case
	 *         of no REGEX found
	 */
	public static String getValueAfterLast(String value, String regex) {
		if (value.contains(regex)) {
			return value.substring(value.lastIndexOf(regex) + regex.length());
		} else {
			return value;
		}
	}

	/**
	 * Get a value from a string BETWEEN the REGEX1 and REGEX2
	 * 
	 * @param value
	 *            the string value
	 * @param regex1
	 *            the regex that is looked for (after)
	 * @param regex2
	 *            the regex that is looked for (before)
	 * @return the substring value BETWEEN the REGEX1 and REGEX2, or the value
	 *         in case of no REGEX found
	 */
	public static String getValueBetween(String value, String regex1,
			String regex2) {
		if (value.contains(regex1) && value.contains(regex2)) {
			return value.substring(value.indexOf(regex1) + regex1.length(),
					value.indexOf(regex2));
		} else {
			return value;
		}
	}

	/**
	 * Filling a number with zeroes ("0") if it is lower than "outputLength"
	 * digits
	 * 
	 * @param context
	 *            the current activity context
	 * @param input
	 *            an input string containing a number
	 * @return a number (in string format) which is always at least
	 *         "outputLength" digits
	 */
	public static String formatNumberOfDigits(String input, int outputLength) {
		String formatType = String.format(Locale.getDefault(), "%%0%dd",
				outputLength);

		try {
			input = String.format(formatType, Integer.parseInt(input));
		} catch (Exception e) {
		}

		return input;
	}

	/**
	 * Remove the leading zeros in alphanumeric text with regex
	 * 
	 * @param input
	 *            an input string containing a number
	 * @return a number (in string format) with removed leading zeroes (if
	 *         exist)
	 */
	public static String removeLeadingZeroes(String input) {
		try {
			input = input.replaceFirst("^0+(?!$)", "");
		} catch (Exception e) {
		}

		return input;
	}

	/**
	 * Get the current date time in format DD.MM.YYY, HH:MM
	 * 
	 * @return the current time in format DD.MM.YYY, HH:MM
	 */
	public static String getCurrentDateTime() {
		return DateFormat.format("dd.MM.yyy, kk:mm", new java.util.Date())
				.toString();
	}

	/**
	 * Get the current time in format DD.MM.YYY, HH:MM
	 * 
	 * @return the current time in format DD.MM.YYY, HH:MM
	 */
	public static String getCurrentTime() {
		return DateFormat.format("kk:mm", new java.util.Date()).toString();
	}

	/**
	 * Get the current date in format DD.MM.YYY
	 * 
	 * @return the current date in format DD.MM.YYY
	 */
	public static String getCurrentDate() {
		return DateFormat.format("dd.MM.yyy", new java.util.Date()).toString();
	}

	/**
	 * Get the current day
	 * 
	 * @return the current day
	 */
	public static int getCurrentDay() {
		int day;
		try {
			day = Integer.parseInt(DateFormat
					.format("dd", new java.util.Date()).toString());
		} catch (Exception e) {
			day = 0;
		}

		return day;
	}

	/**
	 * Get the difference between two hours in format HH:MM
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param afterTime
	 *            a time after the current moment in format HH:MM
	 * @param currTime
	 *            the current time in format HH:MM
	 * @return the difference between the times
	 */
	public static String getTimeDifference(Activity context, String afterTime,
			String currTime) {
		String diff = "";
		int afterTimeMilis = 0;
		int currTimeMilis = 0;

		// In cases when it is after midnight
		if (afterTime.startsWith("00:") && !currTime.startsWith("00:")) {
			afterTime = afterTime.replaceAll("00:", "24:");
		}

		try {
			afterTimeMilis = new BigDecimal(afterTime.split(":")[0]).intValue()
					* 60 + new BigDecimal(afterTime.split(":")[1]).intValue();
			currTimeMilis = new BigDecimal(currTime.split(":")[0]).intValue()
					* 60 + new BigDecimal(currTime.split(":")[1]).intValue();

			diff = (afterTimeMilis - currTimeMilis) + "";

			if (!diff.contains("-")) {
				diff = (new BigDecimal(diff).intValue() / 60) + ":"
						+ (new BigDecimal(diff).intValue() % 60);
				diff = formatTime(context, diff);
			} else {
				diff = "---";
			}
		} catch (Exception e) {

		}

		return diff;
	}

	/**
	 * Format the remaining time in format ~1h,20m
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param difference
	 *            the difference between the times (current one and the one
	 *            after)
	 * @return the difference in format ~1h,20m
	 */
	public static String formatTime(Activity context, String difference) {
		String diff = "";
		String[] differenceArr = difference.split(":");

		if (differenceArr.length == 2) {

			if ("".equals(differenceArr[0]) || "0".equals(differenceArr[0])) {
				if (differenceArr[1].length() == 0) {
					differenceArr[1] = "0"
							+ context.getString(R.string.app_remaining_minutes);
				} else {
					differenceArr[1] = differenceArr[1]
							+ context.getString(R.string.app_remaining_minutes);
				}

				diff = "~" + differenceArr[1];
			} else {
				differenceArr[0] = differenceArr[0]
						+ context.getString(R.string.app_remaining_hours);
				if (differenceArr[1].length() == 0) {
					differenceArr[1] = "0"
							+ context.getString(R.string.app_remaining_minutes);
				} else {
					differenceArr[1] = differenceArr[1]
							+ context.getString(R.string.app_remaining_minutes);
				}

				diff = "~" + differenceArr[0] + " " + differenceArr[1];
			}
		}

		return diff;
	}

	/**
	 * Transform the remaining time in minutes
	 * 
	 * @param remainingTime
	 *            the remaining time in string format
	 * @return the rmaining time in minutes
	 */
	public static int getRemainingMinutes(String remainingTime) {
		int remainingMinutes;

		String[] remainingTimeArray = remainingTime.split(" ");
		if (remainingTimeArray.length == 1) {
			remainingMinutes = Integer.parseInt(Utils
					.getOnlyDigits(remainingTimeArray[0]));
		} else {
			remainingMinutes = 60
					* Integer.parseInt(Utils
							.getOnlyDigits(remainingTimeArray[0]))
					+ Integer.parseInt(Utils
							.getOnlyDigits(remainingTimeArray[1]));
		}

		return remainingMinutes;
	}

	/**
	 * Convert the millis in remaining time format (~�� ��)
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param millis
	 *            remaining time in millis
	 * @return the remaining time in format ~�� ��
	 */
	public static String formatMillisInTime(Activity context, Long millis) {
		String remainingTime;

		long minutes = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;

		if (hour > 0) {
			remainingTime = "~" + hour
					+ context.getString(R.string.app_remaining_minutes) + " "
					+ minutes
					+ context.getString(R.string.app_remaining_minutes);
		} else {
			remainingTime = "~" + minutes
					+ context.getString(R.string.app_remaining_minutes);
		}

		return remainingTime;
	}

	/**
	 * Function that format the direction name of the station or vehicle to be
	 * in correct format. In case of an empty string - return "".
	 * 
	 * @param directionName
	 *            the name of the direction
	 * @return the digits from the String
	 */
	public static String formatDirectionName(String directionName) {
		if (directionName != null && !"".equals(directionName)) {
			String[] directionNameParts = directionName.trim().split("-");

			switch (directionNameParts.length) {
			case 1:
				directionName = directionNameParts[0];

				break;
			case 2:
			case 4:
				directionNameParts[0] = directionNameParts[0].trim();
				directionNameParts[0] = directionNameParts[0].replaceAll("\\(",
						" (");

				directionNameParts[1] = directionNameParts[1].trim();
				directionNameParts[1] = getValueBefore(directionNameParts[1],
						"(");
				directionNameParts[1] = getValueBefore(directionNameParts[1],
						"/");

				directionName = directionNameParts[0] + " - "
						+ directionNameParts[1];
				directionName = directionName.replaceAll(" +", " ");

				break;
			case 3:
			case 6:
				boolean isDirectionThreeParts = true;
				if (directionNameParts[0].equals(directionNameParts[2])) {
					isDirectionThreeParts = false;
				}

				directionNameParts[0] = directionNameParts[0].trim();
				directionNameParts[0] = directionNameParts[0].replaceAll("\\(",
						" (");

				directionNameParts[1] = directionNameParts[1].trim();

				directionNameParts[2] = directionNameParts[2].trim();
				directionNameParts[2] = getValueBefore(directionNameParts[2],
						"(");
				directionNameParts[2] = getValueBefore(directionNameParts[2],
						"/");

				if (isDirectionThreeParts) {
					directionName = directionNameParts[0] + " - "
							+ directionNameParts[1] + " - "
							+ directionNameParts[2];
				} else {
					directionName = directionNameParts[0] + " - "
							+ directionNameParts[1];
				}
				directionName = directionName.replaceAll(" +", " ");

				break;
			default:
				break;
			}
		} else {
			directionName = "";
		}

		// Special cases
		directionName = directionName.replaceAll(" - 1 - ", "-1 - ");
		directionName = directionName.replaceAll(" - 1", "-1");
		directionName = directionName.replaceAll(" - 2 - ", "-2 - ");
		directionName = directionName.replaceAll(" - 2", "-2");
		directionName = directionName.replaceAll(" - 3 - ", "-3 - ");
		directionName = directionName.replaceAll(" - 3", "-3");
		directionName = directionName.replaceAll(" - 4 - ", "-4 - ");
		directionName = directionName.replaceAll(" - 4", "-4");
		directionName = directionName.replaceAll(" - 5 - ", "-5 - ");
		directionName = directionName.replaceAll(" - 5", "-5");
		directionName = directionName.replaceAll(" - 8 - ", "-8 - ");
		directionName = directionName.replaceAll(" - 8", "-8");
		directionName = directionName.replaceAll("6 - ", "6-");
		directionName = directionName.replaceAll(" - �", "-�");
		directionName = directionName.replaceAll("� - �", "�-�");
		directionName = directionName.replaceAll("� - ���", "�-���");
		directionName = directionName.replaceAll("� - �", "�-�");
		directionName = directionName.replaceAll("����\\.", "������������ ");
		directionName = directionName.replaceAll("�������\\.", "������������ ");
		directionName = directionName.replaceAll("���\\.", "������� ");
		directionName = directionName.replaceAll("�\\. ����", "��������� ����");
		directionName = directionName.replaceAll("�\\.����", "��������� ����");
		directionName = directionName.replaceAll("�\\. ����", "��������� ����");
		directionName = directionName.replaceAll("�\\.����", "��������� ����");
		directionName = directionName
				.replaceAll("��\\.����", "���������� ����");

		directionName = directionName.trim().replaceAll(" +", " ");

		return directionName;
	}

	/**
	 * Add a search to the history of searches, using the searched text
	 * 
	 * @param context
	 *            the current Activity context
	 * @param historyType
	 *            the type of the search - <b>public transport</b> or
	 *            <b>metro</b>
	 * @param historyValueArr
	 *            the value that has to be added to the history of searches
	 */
	public static void addSearchInHistory(Activity context,
			VehicleTypeEnum historyType, String... historyValueArr) {
		HistoryOfSearches history = HistoryOfSearches.getInstance(context);

		// Get the name of the search
		String historyValue = "";
		if (historyValueArr.length == 1) {
			historyValue = historyValueArr[0];
		} else {
			if (historyValueArr[1] == null || "".equals(historyValueArr[1])) {
				historyValue = historyValueArr[0];
			} else {
				historyValue = String.format(historyValueArr[0] + " (%s)",
						historyValueArr[1]);
			}
		}

		// Get the search type
		if (historyType == VehicleTypeEnum.METRO1
				|| historyType == VehicleTypeEnum.METRO2) {
			historyType = VehicleTypeEnum.METRO;
		} else {
			historyType = VehicleTypeEnum.BTT;
		}

		int nextSearchNumber = history.getNextSearchNumber();
		history.putFiledInPreferences(context,
				Constants.HISTORY_PREFERENCES_SEARCH_VALUE, nextSearchNumber,
				historyValue);
		history.putFiledInPreferences(context,
				Constants.HISTORY_PREFERENCES_SEARCH_DATE, nextSearchNumber,
				DateFormat.format("dd.MM.yyyy, kk:mm", new java.util.Date())
						.toString());
		history.putFiledInPreferences(context,
				Constants.HISTORY_PREFERENCES_SEARCH_TYPE, nextSearchNumber,
				historyType.name());
	}

	/**
	 * Add a search to the history of searches, using the station
	 * 
	 * @param context
	 *            the current Activity context
	 * @param station
	 *            the station that has to be added to the history of searches
	 */
	public static void addStationInHistory(Activity context,
			StationEntity station) {
		addSearchInHistory(context, station.getType(), station.getName(),
				station.getNumber());
	}

	/**
	 * Add a search to the history of searches, using the station
	 * 
	 * @param context
	 *            the current Activity context
	 * @param stationsMap
	 *            the map of stations that has to be added to the history of
	 *            searches
	 */
	public static void addListOfStationsInHistory(Activity context,
			HashMap<String, StationEntity> stationsMap) {
		Iterator<Entry<String, StationEntity>> stationIterator = stationsMap
				.entrySet().iterator();

		while (stationIterator.hasNext()) {
			addStationInHistory(context, stationIterator.next().getValue());
		}
	}

	/**
	 * Check if an application update is deployed in the GooglePlay market (only
	 * on the first of each month)
	 * 
	 * @param context
	 *            current Activity context
	 */
	public static void checkForUpdate(FragmentActivity context) {
		boolean isDayForUpdate = getCurrentDay() == 1 || getCurrentDay() == 15;
		boolean isUpdateAlreadyChecked = CheckForUpdatesPreferences
				.isUpdateAlreadyChecked(context, getCurrentDate());
		boolean haveInternetConnection = ActivityUtils
				.haveNetworkConnection(context);

		if (isDayForUpdate && !isUpdateAlreadyChecked && haveInternetConnection) {
			CheckForUpdatesPreferences.setLastCheckDate(context,
					getCurrentDate());
			new CheckForUpdatesAsync(context).execute();
		}
	}

	/**
	 * Check if the device is in landscape mod
	 * 
	 * @param context
	 *            the current activity context
	 * @return if the device is in landscape mode
	 */
	public static boolean isInLandscapeMode(Activity context) {

		int currentOrientation = context.getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Create a list with all items in the NavigationDrawer (each row of the
	 * menu)
	 * 
	 * @param context
	 *            the current activity context
	 * 
	 * @return an ArrayList with all raws of the menu
	 */
	public static ArrayList<String> initNavigationDrawerItems(Activity context) {

		ArrayList<String> navigationItems = new ArrayList<String>();

		navigationItems.add(context.getString(R.string.navigation_drawer_home));
		navigationItems.add(context
				.getString(R.string.navigation_drawer_home_standard));
		navigationItems.add(context
				.getString(R.string.navigation_drawer_home_map));
		navigationItems.add(context
				.getString(R.string.navigation_drawer_home_cars));
		navigationItems.add(context.getString(R.string.navigation_drawer_cs));
		navigationItems.add(context
				.getString(R.string.navigation_drawer_history));
		navigationItems.add(context
				.getString(R.string.navigation_drawer_options));
		navigationItems.add(context.getString(R.string.navigation_drawer_info));
		navigationItems.add(context.getString(R.string.navigation_drawer_exit));

		return navigationItems;
	}

}