package uk.co.suspiciouskittens.reversi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PlayerDatabase extends SQLiteOpenHelper {
	private String DATABASE_NAME;

	public PlayerDatabase(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		DATABASE_NAME = name;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/* db.execSQL("CREATE TABLE " + DATABASE_NAME + " (" + KEY_ID
                + " INTEGER PRIMARY KEY, " 
                + KEY_NAME
+ " TEXT;");
*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
