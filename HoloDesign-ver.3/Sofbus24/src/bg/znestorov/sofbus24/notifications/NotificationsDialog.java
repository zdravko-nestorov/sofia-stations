package bg.znestorov.sofbus24.notifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Utils;

/**
 * DialogFragment used to show a messaged when the elapsed time is passed
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class NotificationsDialog extends DialogFragment {

	private Activity context;
	private String[] vehicleInfo;

	private Ringtone ringer;
	private Vibrator vibrator;

	public static final String BUNDLE_VEHICLE_INFO = "VEHICLE_INFO";

	public static NotificationsDialog newInstance(String[] vehicleInfo) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_VEHICLE_INFO, vehicleInfo);

		NotificationsDialog notificationsVBTimeDialog = new NotificationsDialog();
		notificationsVBTimeDialog.setArguments(bundle);

		return notificationsVBTimeDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		context = getActivity();
		vehicleInfo = (String[]) getArguments().getSerializable(
				BUNDLE_VEHICLE_INFO);

		// Set up the alarm
		Uri notification = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		ringer = RingtoneManager.getRingtone(context, notification);
		// TODO Remove the comment
		// ringer.play();

		// Set up the vibration
		int dot = 200;
		int dash = 500;
		int short_gap = 200;
		int medium_gap = 500;
		int long_gap = 1500;
		long[] pattern = {
				0, // Start immediately
				dot, short_gap, dot, short_gap, dot, medium_gap, dash,
				short_gap, dash, short_gap, dash, medium_gap, dot, short_gap,
				dot, short_gap, dot, long_gap, };

		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(pattern, 0);

		// Create the DialogFragment
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inflater = context.getLayoutInflater();
		View view = inflater.inflate(R.layout.activity_notifications_dialog,
				null);
		builder.setView(view);

		// Add the dialog fragment a title
		builder.setTitle(getString(R.string.notifications_title));

		// Initialize the layout fields
		initLayoutFields(view);

		// Add the OK button
		builder.setNeutralButton(R.string.app_button_ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopAlarm();
				context.finish();
			}
		});

		setCancelable(false);

		return builder.create();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopAlarm();
	}

	private void initLayoutFields(View view) {
		TextView stationName = (TextView) view
				.findViewById(R.id.notifications_dialog_station_name);
		TextView stationNumber = (TextView) view
				.findViewById(R.id.notifications_dialog_station_number);
		ImageView vehicleImage = (ImageView) view
				.findViewById(R.id.notifications_dialog_image_vehicle);
		TextView vehicleCaption = (TextView) view
				.findViewById(R.id.notifications_dialog_vehicle_caption);
		TextView vehicleDirection = (TextView) view
				.findViewById(R.id.notifications_dialog_vehicle_direction);
		TextView vehicleTime = (TextView) view
				.findViewById(R.id.notifications_dialog_vehicle_time);
		TextView currentTime = (TextView) view
				.findViewById(R.id.notifications_dialog_current_time);

		vehicleDirection.setSelected(true);

		stationName.setText(vehicleInfo[0]);
		stationNumber.setText(vehicleInfo[1]);
		vehicleImage.setImageResource(Integer.parseInt(vehicleInfo[2]));
		vehicleCaption.setText(vehicleInfo[3]);
		vehicleDirection.setText(vehicleInfo[4]);
		vehicleTime.setText(getString(R.string.notifications_message,
				vehicleInfo[5]));
		currentTime.setText(getString(R.string.vb_time_current_time,
				Utils.getCurrentDateTime()));
	}

	/**
	 * Stop the ringer and the vibration and close the dialog
	 */
	private void stopAlarm() {
		if (vibrator != null) {
			vibrator.cancel();
		}

		if (ringer != null) {
			ringer.stop();
		}
	}

}