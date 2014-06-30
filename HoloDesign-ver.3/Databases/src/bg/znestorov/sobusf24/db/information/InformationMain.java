package bg.znestorov.sobusf24.db.information;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import bg.znestorov.sofbus24.db.entity.Station;
import bg.znestorov.sofbus24.db.entity.Vehicle;

public class InformationMain {

	public static HashMap<String, Object> getInformation(Logger logger,
			String type, String number) {
		String htmlResponse = HtmlRequest
				.retrieveVehicles(logger, type, number);
		if (htmlResponse == null || "".equals(htmlResponse)) {
			logger.info("Problem with the HTTP GET request to the SUMC site for vehicle[Type="
					+ type + ", Number=" + number + "]");
			return null;
		}

		Vehicle vehicle = HtmlResult.getVehicle(logger, htmlResponse, type,
				number);
		ArrayList<Station> stationsList = HtmlResult.getStations(logger,
				htmlResponse);
		if (stationsList.isEmpty()) {
			logger.info("Problem with the HTTP result - no info is found for vehicle[Type=\"\r\n"
					+ type + ", Number=" + number + "]");
		}

		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("vehicle", vehicle);
		resultMap.put("stations", stationsList);

		return resultMap;
	}
}