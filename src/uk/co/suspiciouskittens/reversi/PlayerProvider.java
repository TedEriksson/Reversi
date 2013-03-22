package uk.co.suspiciouskittens.reversi;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

public class PlayerProvider extends ContentProvider {

	public static final String AUTHORITY = "uk.co.suspiciouskittens.players";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/players");

	private SQLiteDatabase playersDB;

	private static final String CAPITALS_TABLE = "capitals";
	public static final String KEY_ID = "_id";
	public static final String KEY_COUNTRY = "country";
	public static final String KEY_CAPITAL = "capital";
	public static final int COUNTRY_COLUMN = 1;
	public static final int CAPITAL_COLUMN = 2;

	private static final int CAPITALS = 1;
	private static final int COUNTRY_ID = 2;
	private static final UriMatcher uriMatcher;

	private static final String DATABASE_NAME = "capitals.db";
	private static final int DATABASE_VERSION = 1;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "capitals", CAPITALS);
		uriMatcher.addURI(AUTHORITY, "capitals/#", COUNTRY_ID);
	}

	private String makeNewWhere(String where, Uri uri, int matchResult) {

		if (matchResult != COUNTRY_ID) {
			return where;
		} else {
			String newWhereSoFar = KEY_ID + "=" + uri.getPathSegments().get(1);
			if (TextUtils.isEmpty(where))
				return newWhereSoFar;
			else
				return newWhereSoFar + " AND (" + where + ')';
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int matchResult = uriMatcher.match(uri);
		String newWhere = makeNewWhere(where, uri, matchResult);

		if (matchResult == COUNTRY_ID || matchResult == CAPITALS) {
			int count = playersDB.delete(CAPITALS_TABLE, newWhere, whereArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		} else
			throw new IllegalArgumentException("Unknown URI " + uri);
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case CAPITALS:
			return "vnd.android.cursor.dir/vnd.brookes.country";
		case COUNTRY_ID:
			return "vnd.android.cursor.item/vnd.brookes.country";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = playersDB.insert(CAPITALS_TABLE, KEY_COUNTRY, values);
		if (rowID > 0) {
			Uri newuri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(newuri, null);
			return uri;
		} else
			throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {

		CapitalsDatabaseHelper helper = new CapitalsDatabaseHelper(
				this.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
		this.playersDB = helper.getWritableDatabase();
		return (playersDB != null);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sort) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(CAPITALS_TABLE);

		if (uriMatcher.match(uri) == COUNTRY_ID) {
			qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
		}

		Cursor c = qb.query(playersDB, projection, selection, selectionArgs,
				null, null, sort);

		// register to watch for changes which are
		// signalled by notifyChange elsewhere
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {

		int matchResult = uriMatcher.match(uri);
		String newWhere = makeNewWhere(where, uri, matchResult);

		if (matchResult == COUNTRY_ID || matchResult == CAPITALS) {
			int count = playersDB.update(CAPITALS_TABLE, values, newWhere,
					whereArgs);

			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		} else
			throw new IllegalArgumentException("Unknown URI " + uri);
	}

	private static class CapitalsDatabaseHelper extends SQLiteOpenHelper {

		public CapitalsDatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + CAPITALS_TABLE + " (" + KEY_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_COUNTRY
					+ " TEXT," + KEY_CAPITAL + " TEXT);");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + CAPITALS_TABLE);
			onCreate(db);
		}

	}
}
