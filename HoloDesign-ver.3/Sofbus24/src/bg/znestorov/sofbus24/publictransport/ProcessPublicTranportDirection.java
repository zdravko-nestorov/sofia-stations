package bg.znestorov.sofbus24.publictransport;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import bg.znestorov.sofbus24.databases.StationsDataSource;
import bg.znestorov.sofbus24.entity.DirectionsEntity;
import bg.znestorov.sofbus24.entity.PublicTransportStation;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VehicleType;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;

public class ProcessPublicTranportDirection {

	private Activity context;
	private Vehicle vehicle;
	private String htmlResult;

	private StationsDataSource stationDatasource;

	public ProcessPublicTranportDirection(Activity context, Vehicle vehicle,
			String htmlResult) {
		this.context = context;
		this.vehicle = vehicle;
		this.htmlResult = htmlResult;

		this.stationDatasource = new StationsDataSource(context);
	}

	public Activity getContext() {
		return context;
	}

	public void setContext(Activity context) {
		this.context = context;
	}

	public String getHtmlResult() {
		return htmlResult;
	}

	public void setHtmlResult(String htmlResult) {
		this.htmlResult = htmlResult;
	}

	public DirectionsEntity getDirectionsFromHtml() {
		DirectionsEntity ptDirectionEntity = new DirectionsEntity();

		String[] htmlDirectionsParts = htmlResult
				.split(Constants.REGEX_DIRECTION_PARTS);
		if (htmlDirectionsParts.length > 2) {
			ptDirectionEntity.setVehicle(vehicle);
			ptDirectionEntity.setVt(getDirectionsHiddenVariables("vt",
					htmlDirectionsParts));
			ptDirectionEntity.setLid(getDirectionsHiddenVariables("lid",
					htmlDirectionsParts));
			ptDirectionEntity.setRid(getDirectionsHiddenVariables("rid",
					htmlDirectionsParts));
			ptDirectionEntity
					.setDirectionsNames(getDirectionsNames(htmlDirectionsParts));
			ptDirectionEntity
					.setDirectionsList(getDirectionsList(htmlDirectionsParts));
		}

		return ptDirectionEntity;
	}

	/**
	 * Get the hidden variables for each direction (vt, lid and rid)
	 * 
	 * @param name
	 *            variable name (vt, lid or rid)
	 * @param htmlDirectionsParts
	 *            an array containing all parts from the HTML result
	 * @return an ArrayList containing the hidden variables
	 */
	private ArrayList<String> getDirectionsHiddenVariables(String name,
			String... htmlDirectionsParts) {
		ArrayList<String> hiddenVariableValues = new ArrayList<String>();
		Pattern pattern = Pattern.compile(String.format(
				Constants.REGEX_DIRECTION_HIDDEN_VARIABLE, name));

		for (int i = 0; i < htmlDirectionsParts.length; i++) {
			Matcher matcher = pattern.matcher(htmlDirectionsParts[i]);

			if (matcher.find()) {
				hiddenVariableValues.add(matcher.group(1));
			}
		}

		return hiddenVariableValues;
	}

	/**
	 * Get the directions' names for the selected vehicle
	 * 
	 * @param htmlDirectionsParts
	 *            an array containing all parts from the HTML result
	 * @return an ArrayList, containing all directions' names
	 */
	private ArrayList<String> getDirectionsNames(String... htmlDirectionsParts) {
		ArrayList<String> directionsNames = new ArrayList<String>();
		Pattern pattern = Pattern.compile(Constants.REGEX_DIRECTION_NAME);

		for (int i = 0; i < htmlDirectionsParts.length; i++) {
			Matcher matcher = pattern.matcher(htmlDirectionsParts[i]);

			if (matcher.find()) {
				String directionName = matcher.group(1);
				directionName = Utils.getValueBefore(directionName, "(");
				directionName = Utils.getValueBefore(directionName, "/");
				directionName = Utils.getValueBefore(directionName, "   ")
						.trim();
				directionsNames.add(directionName);
			}
		}

		return directionsNames;
	}

	/**
	 * Get a list for each direction fulfilled with all stations for it
	 * 
	 * @param htmlDirectionsParts
	 *            an array containing all parts from the HTML result
	 * @return and ArrayList for each direction fulfilled with an ArrayList
	 *         containing all stations
	 */
	private ArrayList<ArrayList<Station>> getDirectionsList(
			String... htmlDirectionsParts) {
		ArrayList<ArrayList<Station>> ptDirectionsList = new ArrayList<ArrayList<Station>>();
		Pattern pattern = Pattern.compile(Constants.REGEX_DIRECTION_STATION);

		stationDatasource.open();
		for (int i = 0; i < htmlDirectionsParts.length; i++) {
			Matcher matcher = pattern.matcher(htmlDirectionsParts[i]);
			ArrayList<Station> ptStationsList = new ArrayList<Station>();

			while (matcher.find()) {
				// Get the station id (special number used to retrieve
				// information) and station name
				String stationId = matcher.group(1);
				String stationName = matcher.group(2).trim();
				stationName = Utils.getValueBeforeLast(stationName, "(");

				// Get the station number
				String stationNumber = matcher.group(2).trim();
				stationNumber = Utils.getValueAfterLast(stationNumber, "(");
				stationNumber = Utils.getValueBefore(stationNumber, ")");
				stationNumber = Utils.getOnlyDigits(stationNumber);

				// Get the station coordinates from the DB (if exists)
				Station dbStation = stationDatasource.getStation(stationNumber);
				String stationLat = dbStation.getLat();
				String stationLon = dbStation.getLon();

				// Get the station type
				VehicleType stationType = vehicle.getType();

				// Create the PublicTransport station and add it to the list
				PublicTransportStation ptStation = new PublicTransportStation(
						new Station(stationNumber, stationName, stationLat,
								stationLon, stationType, null), stationId);
				ptStationsList.add(ptStation);
			}

			// Add the stations to the directions list
			if (ptStationsList.size() > 0) {
				ptDirectionsList.add(ptStationsList);
			}
		}
		stationDatasource.close();

		return ptDirectionsList;
	}
}
