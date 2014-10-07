package bg.znestorov.sofbus24.entity;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import bg.znestorov.sofbus24.utils.Constants;

/**
 * Station object used to transfer data for the VirtualBoards part
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class VirtualBoardsStationEntity extends StationEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String skgtTime;
	private String systemTime;
	private ArrayList<VehicleEntity> vehiclesList;

	public VirtualBoardsStationEntity() {
		super();

		this.systemTime = android.text.format.DateFormat.format(
				"dd.MM.yyy, kk:mm", new java.util.Date()).toString();
		this.vehiclesList = new ArrayList<VehicleEntity>();
	}

	public VirtualBoardsStationEntity(StationEntity station) {
		super(station.getNumber(), station.getName(), station.getLat(), station
				.getLon(), station.getType(), station.getCustomField());

		this.systemTime = android.text.format.DateFormat.format(
				"dd.MM.yyy, kk:mm", new java.util.Date()).toString();
		this.vehiclesList = new ArrayList<VehicleEntity>();
	}

	public VirtualBoardsStationEntity(StationEntity station, String skgtTime,
			ArrayList<VehicleEntity> vehiclesList) {
		super(station.getNumber(), station.getName(), station.getLat(), station
				.getLon(), station.getType(), station.getCustomField());

		this.skgtTime = skgtTime;
		this.systemTime = android.text.format.DateFormat.format(
				"dd.MM.yyy, kk:mm", new java.util.Date()).toString();
		this.vehiclesList = vehiclesList;
	}

	public String getSkgtTime() {
		return skgtTime;
	}

	public void setSkgtTime(String skgtTime) {
		this.skgtTime = skgtTime;
	}

	public String getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(String systemTime) {
		this.systemTime = systemTime;
	}

	/**
	 * Reset the station with the fields from a new station object
	 * 
	 * @param vbStation
	 *            the new viertual boards station object
	 */
	public void setVirtualBoardsTimeStation(VirtualBoardsStationEntity vbStation) {
		if (vbStation != null) {
			this.setNumber(vbStation.getNumber());
			this.setName(vbStation.getName());
			this.setLat(vbStation.getLat());
			this.setLon(vbStation.getLon());
			this.setType(vbStation.getType());
			this.setCustomField(vbStation.getCustomField());

			this.skgtTime = vbStation.getSkgtTime();
			this.systemTime = vbStation.getSystemTime();

			this.vehiclesList.clear();
			this.vehiclesList.addAll(vbStation.getVehiclesList());
		} else {
			this.vehiclesList.clear();
		}
	}

	/**
	 * Depends on the preferences (user settings) decide which time to use - the
	 * system time or the SKGT time
	 * 
	 * @param context
	 *            the current activity context
	 * @return the time to use according to user's choice
	 */
	public String getTime(Activity context) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		String timeSource = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_TIME_SOURCE,
				Constants.PREFERENCE_DEFAULT_VALUE_TIME_SOURCE);

		if (timeSource.equals(Constants.PREFERENCE_DEFAULT_VALUE_TIME_SOURCE)) {
			return skgtTime;
		} else {
			return systemTime;
		}
	}

	public ArrayList<VehicleEntity> getVehiclesList() {
		return vehiclesList;
	}

	public void setVehiclesList(ArrayList<VehicleEntity> vehiclesList) {
		this.vehiclesList = vehiclesList;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\tskgtTime: " + skgtTime
				+ "\n\tsystemTime: " + systemTime + "\n\tvehiclesList: "
				+ vehiclesList + "\n}";
	}

}
