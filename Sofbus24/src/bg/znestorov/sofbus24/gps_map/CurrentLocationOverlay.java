package bg.znestorov.sofbus24.gps_map;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import bg.znestorov.sofbus24.utils.Constants;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

// Overwriting MyLocationOverlay, so include more functions
public class CurrentLocationOverlay extends MyLocationOverlay {

	private MapController mc;
	private Point currentPoint = new Point();

	private boolean centerOnCurrentLocation = false;

	private int height;
	private int width;

	public CurrentLocationOverlay(Context context, MapView mapView) {
		super(context, mapView);
		this.mc = mapView.getController();
	}

	@Override
	public synchronized void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		if (mc != null && centerOnCurrentLocation
				&& inZoomActiveArea(currentPoint)) {
			mc.animateTo(getMyLocation());
		}
	}

	private boolean inZoomActiveArea(Point currentPoint) {
		if ((currentPoint.x > Constants.PADDING_ACTIVE_ZOOM && currentPoint.x < width
				- Constants.PADDING_ACTIVE_ZOOM)
				&& (currentPoint.y > Constants.PADDING_ACTIVE_ZOOM && currentPoint.y < height
						- Constants.PADDING_ACTIVE_ZOOM)) {
			return false;
		}
		return true;
	}

	public void setCenterOnCurrentLocation(boolean centerOnCurrentLocation) {
		this.centerOnCurrentLocation = centerOnCurrentLocation;
	}
}