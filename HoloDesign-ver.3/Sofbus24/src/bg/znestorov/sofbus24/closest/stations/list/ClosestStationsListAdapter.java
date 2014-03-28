package bg.znestorov.sofbus24.closest.stations.list;

import java.util.List;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bg.znestorov.sofbus24.databases.FavouritesDataSource;
import bg.znestorov.sofbus24.entity.Station;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.main.Sofbus24;
import bg.znestorov.sofbus24.utils.MapUtils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Array Adapter used to set each row of the ClosestStations Fragment
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ClosestStationsListAdapter extends ArrayAdapter<Station> {

	private final FavouritesDataSource favouritesDataSource;
	private final Activity context;
	private final LatLng currentLocation;
	private final List<Station> stations;

	// Used for optimize performance of the ListView
	static class ViewHolder {
		ImageView addToFavourites;
		TextView stationCaption;
		TextView stationDistance;
	}

	public ClosestStationsListAdapter(Activity context, LatLng currentLocation,
			List<Station> stations) {
		super(context, R.layout.activity_closest_stations_list_item, stations);
		this.context = context;
		this.currentLocation = currentLocation;
		this.stations = stations;
		this.favouritesDataSource = new FavouritesDataSource(context);
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
					R.layout.activity_closest_stations_list_item, null);

			// Configure view holder
			viewHolder = new ViewHolder();
			viewHolder.addToFavourites = (ImageView) rowView
					.findViewById(R.id.cs_list_item_favourite);
			viewHolder.stationCaption = (TextView) rowView
					.findViewById(R.id.cs_list_item_station_caption);
			viewHolder.stationDistance = (TextView) rowView
					.findViewById(R.id.cs_list_item_station_distance);
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Fill the data
		Station station = stations.get(position);
		viewHolder.addToFavourites.setImageResource(getFavouriteImage(station));
		viewHolder.stationCaption.setText(String.format(station.getName()
				+ " (%s)", station.getNumber()));
		viewHolder.stationDistance.setText(String.format(
				context.getString(R.string.cs_list_item_distance_text),
				MapUtils.getMapDistance(currentLocation, station))
				+ context.getString(R.string.app_distance_meters));

		// Set the actions over the ImageView
		actionsOverFavouritesImageViews(viewHolder, station);

		return rowView;
	}

	/**
	 * Get the favourites image according to this if exists in the Favourites
	 * Database
	 * 
	 * @param station
	 *            the station on the current row
	 * @return the station image id
	 */
	private Integer getFavouriteImage(Station station) {
		Integer favouriteImage;

		favouritesDataSource.open();
		if (favouritesDataSource.getStation(station) == null) {
			favouriteImage = R.drawable.ic_fav_empty;
		} else {
			favouriteImage = R.drawable.ic_fav_full;
		}
		favouritesDataSource.close();

		return favouriteImage;
	}

	/**
	 * Click listeners over the addFavourites image
	 * 
	 * @param viewHolder
	 *            holder containing all elements in the layout
	 * @param station
	 *            the station on the current row
	 */
	public void actionsOverFavouritesImageViews(final ViewHolder viewHolder,
			final Station station) {
		viewHolder.addToFavourites.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Sofbus24.setFavouritesChanged(true);

				favouritesDataSource.open();
				if (favouritesDataSource.getStation(station) == null) {
					favouritesDataSource.createStation(station);
					viewHolder.addToFavourites
							.setImageResource(R.drawable.ic_fav_full);
					Toast.makeText(
							context,
							Html.fromHtml(String.format(context
									.getString(R.string.metro_item_add_toast),
									station.getName(), station.getNumber())),
							Toast.LENGTH_SHORT).show();
				} else {
					favouritesDataSource.deleteStation(station);
					viewHolder.addToFavourites
							.setImageResource(R.drawable.ic_fav_empty);
					Toast.makeText(
							context,
							Html.fromHtml(String.format(
									context.getString(R.string.metro_item_remove_toast),
									station.getName(), station.getNumber())),
							Toast.LENGTH_SHORT).show();
				}
				favouritesDataSource.close();
			}
		});
	}
}