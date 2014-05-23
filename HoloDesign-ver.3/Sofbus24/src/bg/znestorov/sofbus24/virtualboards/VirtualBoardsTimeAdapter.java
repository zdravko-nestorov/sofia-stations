package bg.znestorov.sofbus24.virtualboards;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.entity.Vehicle;
import bg.znestorov.sofbus24.entity.VirtualBoardsStation;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;

/**
 * Array Adapted user to set each row a vehicle with its arrival times from the
 * SKGT site
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class VirtualBoardsTimeAdapter extends ArrayAdapter<Vehicle> implements
		Filterable {

	private Activity context;
	private VirtualBoardsStation vbTimeStation;
	private String timeType;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		ImageView vehicleImage;
		TextView stationCaption;
		TextView stationDirection;
		TextView stationTime;
	}

	public VirtualBoardsTimeAdapter(Activity context,
			VirtualBoardsStation vbTimeStation) {
		super(context, R.layout.activity_virtual_boards_time_list_item,
				vbTimeStation.getVehiclesList());

		this.context = context;
		this.vbTimeStation = vbTimeStation;

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		this.timeType = sharedPreferences.getString(
				Constants.PREFERENCE_KEY_TIME_TYPE,
				Constants.PREFERENCE_DEFAULT_VALUE_TIME_TYPE);
	}

	/**
	 * Creating the elements of the ListView
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder viewHolder;

		// Reuse views
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(
					R.layout.activity_virtual_boards_time_list_item, null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.vehicleImage = (ImageView) rowView
					.findViewById(R.id.vb_time_item_image_vehicle);
			viewHolder.stationCaption = (TextView) rowView
					.findViewById(R.id.vb_time_item_vehicle_caption);
			viewHolder.stationDirection = (TextView) rowView
					.findViewById(R.id.vb_time_item_vehicle_direction);
			viewHolder.stationTime = (TextView) rowView
					.findViewById(R.id.cs_list_item_vehicle_time);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Fill the data
		Vehicle stationVehicle = vbTimeStation.getVehiclesList().get(position);

		viewHolder.vehicleImage
				.setImageResource(getVehicleImage(stationVehicle));
		viewHolder.stationCaption.setText(getVehicleCaption(stationVehicle));
		viewHolder.stationDirection.setText(stationVehicle.getDirection());
		viewHolder.stationTime.setText(getRowTimeCaption(stationVehicle));

		return rowView;
	}

	/**
	 * Choose the coresponding image from the resources according to the vehicle
	 * type
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 * @return the corresponding image to the vehicle
	 */
	private int getVehicleImage(Vehicle stationVehicle) {
		int vehicleImage;

		switch (stationVehicle.getType()) {
		case BUS:
			vehicleImage = R.drawable.ic_bus;
			break;
		case TROLLEY:
			vehicleImage = R.drawable.ic_trolley;
			break;
		default:
			vehicleImage = R.drawable.ic_tram;
			break;
		}

		return vehicleImage;
	}

	/**
	 * Create the vehicle caption using the vehicle type and vehicle number
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 * @return the vehicle caption
	 */
	private String getVehicleCaption(Vehicle stationVehicle) {
		String vehicleCaption;
		String vehicleTypeText;

		switch (stationVehicle.getType()) {
		case BUS:
			vehicleTypeText = context.getString(R.string.vb_time_bus);
			break;
		case TROLLEY:
			vehicleTypeText = context.getString(R.string.vb_time_trolley);
			break;
		default:
			vehicleTypeText = context.getString(R.string.vb_time_tram);
			break;
		}

		vehicleCaption = String.format(vehicleTypeText,
				stationVehicle.getNumber());

		return vehicleCaption;
	}

	/**
	 * Create a separated string, using the elements from the list
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 * @return a separated string with the arrival times
	 */
	private String getArrivalTimes(Vehicle stationVehicle) {
		ArrayList<String> arrivalTimesList = stationVehicle.getArrivalTimes();
		StringBuilder arrivalTimes = new StringBuilder("");

		for (int i = 0; i < arrivalTimesList.size(); i++) {
			arrivalTimes.append(arrivalTimesList.get(i)).append(", ");
		}

		arrivalTimes.deleteCharAt(arrivalTimes.length() - 2).trimToSize();

		return arrivalTimes.toString();
	}

	/**
	 * Create a separated string, using the elements from the list
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 * @return a separated string with the remaining times
	 */
	private String getRemainingTimes(Vehicle stationVehicle) {
		ArrayList<String> arrivalTimesList = stationVehicle.getArrivalTimes();
		String currentTime = vbTimeStation.getTime(context);
		StringBuilder arrivalTimes = new StringBuilder("");

		for (int i = 0; i < arrivalTimesList.size(); i++) {
			String timeToUse = Utils.getDifference(context,
					arrivalTimesList.get(i), currentTime);
			arrivalTimes.append(timeToUse).append(", ");
		}

		arrivalTimes.deleteCharAt(arrivalTimes.length() - 2).trimToSize();

		return arrivalTimes.toString();
	}

	/**
	 * Create the text for the last TextView of the row (containing the times of
	 * arrival or remaining times)
	 * 
	 * @param stationVehicle
	 *            the station vehicle
	 * @return the last TextView text of each row (containing the times of
	 *         arrival or remaining times)
	 */
	private Spanned getRowTimeCaption(Vehicle stationVehicle) {
		Spanned rowTimeCaption;

		if (timeType.equals(Constants.PREFERENCE_DEFAULT_VALUE_TIME_TYPE)) {
			rowTimeCaption = Html.fromHtml(String.format(
					context.getString(R.string.vb_time_item_remaining_time),
					getRemainingTimes(stationVehicle)));
		} else {
			rowTimeCaption = Html.fromHtml(String.format(
					context.getString(R.string.vb_time_item_time_of_arrival),
					getArrivalTimes(stationVehicle)));
		}

		return rowTimeCaption;
	}
}