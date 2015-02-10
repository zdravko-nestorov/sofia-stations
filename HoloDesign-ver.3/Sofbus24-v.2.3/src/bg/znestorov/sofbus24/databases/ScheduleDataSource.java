package bg.znestorov.sofbus24.databases;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import bg.znestorov.sofbus24.entity.ScheduleCacheEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;

/**
 * Schedule cache data source used for DB interactions
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class ScheduleDataSource {

	// Database fields
	private SQLiteDatabase database;
	private ScheduleSQLite dbHelper;
	private String[] scheduleColumns = { ScheduleSQLite.COLUMN_PK_SCHE_ID,
			ScheduleSQLite.COLUMN_SCHE_TYPE, ScheduleSQLite.COLUMN_SCHE_NUMBER,
			ScheduleSQLite.COLUMN_SCHE_HTML,
			ScheduleSQLite.COLUMN_SCHE_TIMESTAMP };

	public ScheduleDataSource(Activity context) {
		this.dbHelper = new ScheduleSQLite(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Inserting a schedule cache (the html response for the current schedule)
	 * into the database
	 * 
	 * @param dataType
	 *            the type of the inserted data
	 * @param station
	 *            the input station
	 * @param dataNumber
	 *            the number of the inserted data
	 * @return if the data is successfull inserted into the db
	 */
	public boolean createScheduleCache(VehicleTypeEnum dataType,
			String dataNumber, String htmlSchedule) {

		if (htmlSchedule != null && !"".equals(htmlSchedule)) {

			// Creating ContentValues object and insert the data in it
			ContentValues values = new ContentValues();
			values.put(ScheduleSQLite.COLUMN_SCHE_TYPE,
					String.valueOf(dataType));
			values.put(ScheduleSQLite.COLUMN_SCHE_NUMBER, dataNumber);
			values.put(ScheduleSQLite.COLUMN_SCHE_HTML, htmlSchedule);

			// Insert the ContentValues data into the database
			long rowId = database.insert(ScheduleSQLite.TABLE_SOF_SCHE, null,
					values);

			return rowId != -1;
		} else {
			return false;
		}
	}

	/**
	 * Get a shcedule cache via a cache type and number
	 * 
	 * @param dataType
	 *            the type of the searched data
	 * @param dataNumber
	 *            the number of the searched data
	 * @return a ScheduleCacheEntity object with a data for the searched number
	 *         and type
	 */
	public ScheduleCacheEntity getScheduleCache(VehicleTypeEnum dataType,
			String dataNumber) {

		ScheduleCacheEntity scheduleCache = null;

		String[] dataColumns = new String[] { scheduleColumns[3],
				scheduleColumns[4] };
		String selection = ScheduleSQLite.COLUMN_SCHE_TYPE + " = ? AND "
				+ ScheduleSQLite.COLUMN_SCHE_NUMBER + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(dataType),
				dataNumber };

		// Selecting the row that contains the vehicle data
		Cursor cursor = database.query(ScheduleSQLite.TABLE_SOF_SCHE,
				dataColumns, selection, selectionArgs, null, null, null);

		if (cursor.getCount() > 0) {

			// Moving the cursor to the first column of the selected row
			cursor.moveToFirst();

			scheduleCache = new ScheduleCacheEntity(cursor.getString(0),
					cursor.getString(1));
		}

		// Closing the cursor
		cursor.close();

		return scheduleCache;
	}

	/**
	 * Delete the schedule cache for the selected type
	 * 
	 * @param dataType
	 *            the type of the searched data
	 * @return if the cache is successfully deleted
	 */
	public boolean deleteScheduleCache(VehicleTypeEnum dataType) {

		String where = ScheduleSQLite.COLUMN_SCHE_TYPE + " = ?";
		String[] whereArgs = new String[] { String.valueOf(dataType) };

		return database.delete(ScheduleSQLite.TABLE_SOF_SCHE, where, whereArgs) > 0;
	}

	/**
	 * Delete the schedule cache before a selected number of days
	 * 
	 * @param numberOfDays
	 *            the records beyond these number of days will be deleted
	 * @return if the cache is successfully deleted
	 */
	public boolean deleteScheduleCache(int numberOfDays) {

		if (numberOfDays > 0) {
			String where = ScheduleSQLite.COLUMN_SCHE_TIMESTAMP + " < ?";
			String[] whereArgs = new String[] { String.format(
					"DATE('NOW','-%s DAY')", numberOfDays) };

			return database.delete(ScheduleSQLite.TABLE_SOF_SCHE, where,
					whereArgs) > 0;
		} else {
			return false;
		}
	}

	/**
	 * Delete all schedule cache from the database;
	 * 
	 * @return if the cache is successfully deleted
	 */
	public boolean deleteAllScheduleCache() {
		return database.delete(ScheduleSQLite.TABLE_SOF_SCHE, null, null) > 0;
	}

}