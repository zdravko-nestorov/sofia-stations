package bg.znestorov.sofbus24.databases;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Stations SQLite helper class, responsible for DB life-cycle
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 */
public class VehiclesSQLite extends SQLiteOpenHelper {

	// Table and columns names
	public static final String TABLE_VEHICLES = "vehicles";
	public static final String COLUMN_NUMBER = "number";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_DIRECTION = "direction";

	// The Android's default system path of the database
	private static String DB_PATH = "//data//data//bg.znestorov.sofbus24.main//databases//";
	private static String DB_NAME = "vehicles.db";
	private static final int DATABASE_VERSION = 2;

	// Database creation SQL statement
	private static final String DATABASE_CREATE_VEHICLES = "CREATE TABLE "
			+ TABLE_VEHICLES + "(" + COLUMN_NUMBER + " TEXT NOT NULL, "
			+ COLUMN_TYPE + " TEXT NOT NULL, " + COLUMN_DIRECTION
			+ " TEXT NOT NULL, " + "PRIMARY KEY(" + COLUMN_NUMBER + ", "
			+ COLUMN_TYPE + "));";

	private SQLiteDatabase dbVehicles;
	private final Context context;

	/**
	 * The constructor takes and keeps a reference of the passed context in
	 * order to access to the application assets and resources.
	 * 
	 * @param context
	 *            the current context
	 */
	public VehiclesSQLite(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_VEHICLES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		context.deleteDatabase(DB_NAME);
		createDataBase(null);
	}

	@Override
	public synchronized void close() {
		if (dbVehicles != null)
			dbVehicles.close();

		super.close();
	}

	/**
	 * Create an empty database on the system and rewrites it with the ready
	 * database
	 */
	public void createDataBase(InputStream is) {
		// Check if the DB already exists
		boolean dbExist = checkDataBase();

		if (!dbExist) {
			try {
				copyDataBase(is);
			} catch (IOException e) {
				throw new Error(e.getMessage());
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time the application is opened
	 * 
	 * @return if the DB exists or not
	 */
	private boolean checkDataBase() {
		File dbFile = new File(DB_PATH + DB_NAME);

		return dbFile.exists();
	}

	/**
	 * Copies the vehicles database from the local assets-folder to the just
	 * created empty database in the system folder, from where it can be
	 * accessed and handled. This is done by transferring ByteStream.
	 * 
	 * @throws IOException
	 */
	private void copyDataBase(InputStream is) throws IOException {
		// Open the local DB as the input stream
		InputStream myInput = context.getAssets().open(DB_NAME);
		if (is != null) {
			myInput = is;
		} else {
			myInput = context.getAssets().open(DB_NAME);
		}

		// Create the folder if it is not already created
		File dbFolder = new File(DB_PATH);
		if (!dbFolder.exists()) {
			dbFolder.mkdir();
		}

		// Path to the just created empty DB
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty DB as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// Transfer the bytes from the InputFile to the OutputFile
		byte[] buffer = new byte[1024];
		int length;

		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * Open the stations DB in read-only mode
	 * 
	 * @return the stations DB
	 * @throws SQLException
	 */
	public SQLiteDatabase openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		dbVehicles = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);

		return dbVehicles;
	}
}