package bg.znestorov.sofbus24.main;

import android.os.Bundle;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

public class PublicTransportScheduleDialog extends PublicTransportSchedule {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActivityUtils.showAsPopup(this, false, false);
		super.onCreate(savedInstanceState);
		this.setFinishOnTouchOutside(true);
	}

}