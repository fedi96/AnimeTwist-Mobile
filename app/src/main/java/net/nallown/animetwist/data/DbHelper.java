package net.nallown.animetwist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.nallown.animetwist.at.videos.Video;

import java.sql.SQLException;

/**
 * Created by Nasir on 15/10/2014.
 */
public class DbHelper {
	private final String LOG_TAG = getClass().getSimpleName();

	public static final String UNIQUE_COLUMN = "title";
	public static final String ONGOING_COLUMN = "ongoing";
	public static final String FOLDER_COLUMN = "folder";
	public static final String THUMBNAIL_COLUMN = "thumbnail";
	private DatabaseHelper dbHelper = null;
	private SQLiteDatabase database = null;

	private static final String DATABASE_NAME = "Series";
	private static final String FTS_VIRTUAL_TABLE = "Info";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE =
			"CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE + " USING fts3("
			+ UNIQUE_COLUMN + ", " + ONGOING_COLUMN + ", " + FOLDER_COLUMN
			+ ", " + THUMBNAIL_COLUMN + " UNIQUE (" + UNIQUE_COLUMN + "));";

	private final Context context;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		private final String LOG_TAG = getClass().getSimpleName();

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase sqLiteDatabase) {
			Log.w(LOG_TAG, DATABASE_CREATE);
			sqLiteDatabase.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {
			Log.w(LOG_TAG, "Upgrading database from version " + oldV + " to " + newV +
			", which will destroy all data");
			sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
			onCreate(sqLiteDatabase);
		}
	}

	public DbHelper(Context context) {
		this.context = context;
	}

	public DbHelper open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		if (dbHelper != null) {
			dbHelper.close();
		}
	}

	public long createList(Video video) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(UNIQUE_COLUMN, video.getTitle());
		initialValues.put(ONGOING_COLUMN, video.isOngoing());
		initialValues.put(FOLDER_COLUMN, video.getFolder());
		initialValues.put(THUMBNAIL_COLUMN, video.getThumbnailUrl());

		return database.insert(FTS_VIRTUAL_TABLE, null, initialValues);
	}

	public Cursor searchUnique(String inputText) throws SQLException {
		String query = "SELECT docid as _id," + UNIQUE_COLUMN
				+ " from " + FTS_VIRTUAL_TABLE + " where " + UNIQUE_COLUMN + " MATCH '"
				+ inputText + "';";

		Cursor cursor = database.rawQuery(query, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public boolean deleteAll() {
		int success = database.delete(FTS_VIRTUAL_TABLE, null, null);
		return success > 0;
	}
}
