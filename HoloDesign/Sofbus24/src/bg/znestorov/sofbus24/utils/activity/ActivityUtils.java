package bg.znestorov.sofbus24.utils.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import bg.znestorov.sofbus24.main.R;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * The class contains only a static methods, helping with Activity interactions
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class ActivityUtils {

	/**
	 * Request the focus and show a keyboard on EditText field
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param editText
	 *            the EditText field
	 */
	public static void showKeyboard(Context context, EditText editText) {
		// Focus the field
		editText.requestFocus();

		// Show soft keyboard for the user to enter the value
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}

	/**
	 * Request the focus and hide the keyboard on EditText field
	 * 
	 * @param context
	 *            Context of the current activity
	 * @param editText
	 *            the EditText field
	 */
	public static void hideKeyboard(Context context, EditText editText) {
		// Hide soft keyboard
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/**
	 * Init the UIL image loader
	 * 
	 * @param context
	 *            the current activity context
	 */
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getCacheDirectory(context);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
				.taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 1)
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.imageDownloader(new BaseImageDownloader(context))
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.build();

		ImageLoader.getInstance().init(config);
	}

	/**
	 * Create the display image options according via the Universal Image Loader
	 * options
	 * 
	 * @return the configured display image options
	 */
	public static DisplayImageOptions displayImageOptions() {
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
				.delayBeforeLoading(100).cacheInMemory(true).cacheOnDisc(true)
				.build();

		return displayImageOptions;
	}

	/**
	 * Show no Internet alert dialog
	 * 
	 * @param context
	 *            current Activity context
	 */
	public static void showNoInternetAlertDialog(Activity context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_menu_info_details)
				.setTitle(context.getString(R.string.app_dialog_title_error))
				.setMessage(context.getString(R.string.app_internet_error))
				.setNegativeButton(context.getString(R.string.app_button_ok),
						null).show();
	}

	/**
	 * Create custom AlertDialog with custom fields
	 * 
	 * @param context
	 *            the current Activity context
	 * @param icon
	 *            the icon of the AlertDialog
	 * @param title
	 *            the title of the AlertDialog
	 * @param message
	 *            the message content of the AlertDialog
	 * @param positiveButton
	 *            the positive button text of the AlertDialog
	 * @param positiveOnClickListener
	 *            the positive onClickListener of the AlertDialog
	 * @param negativeButton
	 *            the negative button text of the AlertDialog
	 * @param negativeOnClickListener
	 *            the negative onClickListener of the AlertDialog
	 */
	public static void showCustomAlertDialog(Activity context, int icon,
			CharSequence title, CharSequence message,
			CharSequence positiveButton,
			OnClickListener positiveOnClickListener,
			CharSequence negativeButton, OnClickListener negativeOnClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(icon).setTitle(title).setMessage(message);

		if (positiveButton != null) {
			builder.setPositiveButton(positiveButton, positiveOnClickListener);
		}

		if (negativeButton != null) {
			builder.setNegativeButton(negativeButton, negativeOnClickListener);
		}

		builder.show();
	}

	/**
	 * Restart the application
	 * 
	 * @param context
	 *            the current Activity context
	 */
	public static void restartApplication(Activity context) {
		// Close the application
		context.finish();
		android.os.Process.killProcess(android.os.Process.myPid());

		// Start the application again
		Intent i = context.getPackageManager().getLaunchIntentForPackage(
				context.getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(i);
	}

}
