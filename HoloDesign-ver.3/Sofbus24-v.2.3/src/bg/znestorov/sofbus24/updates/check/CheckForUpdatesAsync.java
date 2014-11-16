package bg.znestorov.sofbus24.updates.check;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import bg.znestorov.sofbus24.about.Configuration;
import bg.znestorov.sofbus24.about.UpdateApplicationDialog;
import bg.znestorov.sofbus24.databases.Sofbus24SQLite;
import bg.znestorov.sofbus24.entity.ConfigEntity;
import bg.znestorov.sofbus24.entity.UpdateTypeEnum;
import bg.znestorov.sofbus24.main.R;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.Utils;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

/**
 * Asynchronous class used for checking for updates from a URL address and parse
 * it to a configuration object
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
public class CheckForUpdatesAsync extends AsyncTask<Void, Void, ConfigEntity> {

	private FragmentActivity context;
	private UpdateTypeEnum updateType;

	public CheckForUpdatesAsync(FragmentActivity context,
			UpdateTypeEnum updateType) {

		this.context = context;
		this.updateType = updateType;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		ActivityUtils.lockScreenOrientation(context);
	}

	@Override
	protected ConfigEntity doInBackground(Void... params) {
		ConfigEntity appConfig = null;

		try {
			// Get the configuration as an InputStream from the station URL
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new URL(Constants.CONFIGURATION_URL)
					.openStream());

			// Create a configuration object from the Document
			appConfig = new ConfigEntity(doc);
		} catch (Exception e) {
			appConfig = new ConfigEntity();
		}

		if (updateType == UpdateTypeEnum.DB) {
			updateDb(appConfig);
		}

		return appConfig;
	}

	@Override
	protected void onPostExecute(ConfigEntity newConfig) {
		super.onPostExecute(newConfig);

		if (updateType == UpdateTypeEnum.APP) {
			updateApp(newConfig);
		}

		CheckForUpdatesPreferences.setLastCheckDate(context,
				Utils.getCurrentDate(), updateType);

		ActivityUtils.unlockScreenOrientation(context);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		CheckForUpdatesPreferences.clearLastCheckDate(context, updateType);

		ActivityUtils.unlockScreenOrientation(context);
	}

	/**
	 * Check if the application should be updated and show an DialogFragment if
	 * needed
	 * 
	 * @param newConfig
	 *            the new application configuration
	 */
	private void updateApp(ConfigEntity newConfig) {

		ConfigEntity currentConfig = new ConfigEntity(context);

		if (newConfig.isValidConfig()
				&& currentConfig.getVersionCode() < newConfig.getVersionCode()) {
			DialogFragment dialogFragment = UpdateApplicationDialog
					.newInstance(String.format(
							context.getString(R.string.about_update_app_new),
							newConfig.getVersionName()));
			dialogFragment.show(context.getSupportFragmentManager(),
					"dialogFragment");
		}
	}

	/**
	 * Check if the dabatase should be updated
	 * 
	 * @param newConfig
	 *            the new application configuration
	 */
	private void updateDb(ConfigEntity newConfig) {

		ConfigEntity currentConfig = new ConfigEntity(context);

		if (newConfig.isValidConfig()
				&& currentConfig.getSofbus24DbVersion() < newConfig
						.getSofbus24DbVersion()) {

			Configuration.editDbConfigurationVersionField(context, newConfig);

			try {
				InputStream dbInputStream = new URL(
						Constants.CONFIGURATION_SOFBUS_24_DB_URL).openStream();
				if (dbInputStream != null) {
					createSofbusDbInFiles(dbInputStream);
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Replace the existing Sofbus 24 DB in files folder with the one that was
	 * download from the URL address
	 * 
	 * @param dbInputStream
	 *            the stations DB input stream
	 * @throws Exception
	 */
	private void createSofbusDbInFiles(InputStream dbInputStream)
			throws Exception {

		context.deleteFile(Sofbus24SQLite.DB_NAME);

		FileOutputStream dbOutputStream = null;
		try {
			dbOutputStream = context.openFileOutput(Sofbus24SQLite.DB_NAME,
					Context.MODE_PRIVATE);
			byte[] buf = new byte[1024];
			int len;
			while ((len = dbInputStream.read(buf)) > 0) {
				dbOutputStream.write(buf, 0, len);
			}
		} finally {
			dbOutputStream.close();
			dbInputStream.close();
		}
	}
}