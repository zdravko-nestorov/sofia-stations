package bg.znestorov.sofbus24.main;

import android.os.Bundle;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

public class VirtualBoardsTimeDialog extends VirtualBoardsTime {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActivityUtils.showAsPopup(this, false);
		super.onCreate(savedInstanceState);
	}

}