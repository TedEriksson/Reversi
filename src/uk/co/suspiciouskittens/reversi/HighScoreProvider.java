package uk.co.suspiciouskittens.reversi;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.util.Log;

public class HighScoreProvider extends ContentProvider {
	
	public static final String AUTHORITY = "uk.co.suspiciouskittens.reversi.highscores";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/highscores");
	
	private SQLiteDatabase highScoresDB;
	private static final String HIGHSCORES_TABLE = "highscores";
	public static final String KEY_ID = "_id";
	public static final String KEY_PIC_ID = "picID";
	public static final String KEY_NAME = "name";
	public static final String KEY_SCORE = "score";
	public static final int PIC_ID_COLUMN = 3;
	public static final int NAME_COLUMN = 1;
	public static final int SCORE_COLUMN = 2;
	
	private static final String DATABASE_NAME = "highscores.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final int NAME = 1;
	private static final int SCORE_ID = 2;
	private static final int PIC_ID = 3;
	private static final UriMatcher uriMatcher;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY,HIGHSCORES_TABLE, NAME);
		uriMatcher.addURI(AUTHORITY,HIGHSCORES_TABLE + "/#", SCORE_ID);
		uriMatcher.addURI(AUTHORITY,HIGHSCORES_TABLE + "/#/#", PIC_ID);
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		drop();
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		 if (uriMatcher.match(uri) != NAME) {
	            throw new IllegalArgumentException("Unknown URI " + uri);
	        }
		long rowID = highScoresDB.insert(HIGHSCORES_TABLE, KEY_NAME, values);
		Log.d("rowID: ", Long.toString(rowID));
		if (rowID > 0) {
			Uri newuri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(newuri, null);
			return uri;
		} else
			throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		HighScoresDatabaseHelper helper = new HighScoresDatabaseHelper(
				this.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
		this.highScoresDB = helper.getWritableDatabase();
		return (highScoresDB != null);
	}
	
	public void drop() {
		HighScoresDatabaseHelper helper = new HighScoresDatabaseHelper(
				this.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
		helper.onUpgrade(highScoresDB, 1, 1);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(HIGHSCORES_TABLE);

		if (uriMatcher.match(uri) == NAME) {
			//qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
		}

		Cursor c = qb.query(highScoresDB, projection, selection, selectionArgs,
				null, null, sortOrder);

		// register to watch for changes which are
		// signalled by notifyChange elsewhere
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private static class HighScoresDatabaseHelper extends SQLiteOpenHelper {

		public HighScoresDatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + HIGHSCORES_TABLE + " (" + KEY_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT," + KEY_SCORE + " INTEGER," + KEY_PIC_ID + " TEXT);");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + HIGHSCORES_TABLE);
			onCreate(db);
		}

	}

}
