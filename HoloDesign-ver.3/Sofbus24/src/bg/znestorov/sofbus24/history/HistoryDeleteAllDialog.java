package bg.znestorov.sofbus24.history;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import bg.znestorov.sofbus24.main.R;

public class HistoryDeleteAllDialog extends DialogFragment {

	public interface OnDeleteAllHistoryListener {
		public void onDeleteAllHistoryClicked();
	}

	private Activity context;
	private int icon;
	private String title;
	private Spanned message;
	private String negativeBtn;
	private String positiveBtn;
	private OnClickListener positiveOnClickListener;

	private OnDeleteAllHistoryListener onDeleteAllHistoryListener;

	public static HistoryDeleteAllDialog newInstance() {
		return new HistoryDeleteAllDialog();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onDeleteAllHistoryListener = (OnDeleteAllHistoryListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnDeleteAllHistoryListener interface...");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		context = getActivity();
		icon = android.R.drawable.ic_menu_delete;
		title = getString(R.string.app_dialog_title_important);
		message = Html
				.fromHtml(getString(R.string.history_menu_remove_all_confirmation));
		negativeBtn = getString(R.string.app_button_no);
		positiveBtn = getString(R.string.app_button_yes);

		positiveOnClickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((OnDeleteAllHistoryListener) onDeleteAllHistoryListener)
						.onDeleteAllHistoryClicked();
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(icon).setTitle(title).setMessage(message)
				.setNegativeButton(negativeBtn, null)
				.setPositiveButton(positiveBtn, positiveOnClickListener);

		return builder.create();
	}
}