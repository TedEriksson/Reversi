package uk.co.suspiciouskittens.reversi;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;

public class HighScoresActivity extends Activity {

	private ContentResolver cr;
	private int numberScores;
	private ListView highList;
	String table;

	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_scores);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		highList = (ListView) findViewById(R.id.highlist);
		
		cr = getContentResolver();
		Cursor c = cr.query(HighScoreProvider.CONTENT_URI, null, null, null,HighScoreProvider.KEY_SCORE + " DESC LIMIT 10");
		
		numberScores = c.getCount();
		
		if (numberScores == 0) {
			populateProvider();
			c = cr.query(HighScoreProvider.CONTENT_URI, null, null, null, HighScoreProvider.KEY_SCORE + " DESC LIMIT 10");
			numberScores = c.getCount();
		}
		
		String[] from = new String[] {HighScoreProvider.KEY_PIC_ID, HighScoreProvider.KEY_NAME,HighScoreProvider.KEY_SCORE};
		
		/*if (c.moveToFirst()) {
			do {
				from[i] = loader.load(c.getString(HighScoreProvider.PIC_ID_COLUMN), cr);
				i++;
			} while (c.moveToNext());
		}*/
		
		
		int[] to = new int[] {R.id.highimage, R.id.highname, R.id.highscore};
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.highscoreslist, c, from, to);

		final ContactPhotoLoader loader = new ContactPhotoLoader();
		
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){
			   public boolean setViewValue(View view, Cursor cursor, int columnIndex){
			       if(view.getId() == R.id.highimage){
			           if(!cursor.getString(columnIndex).equals("-1")) {
			           ((ImageView)view).setImageBitmap(loader.load(cursor.getString(columnIndex), cr));
			           } else {
			        	   ((ImageView)view).setImageResource(R.drawable.cell_black);
			           }
			           return true; //true because the data was bound to the view
			       }
			       return false;
			   }
			});
		
		highList.setAdapter(adapter);
		
		
	}

	private void populateProvider() {
		String[] randomNames = {"Bob","Jim","Tim","Tom"};
		String[] randomScores = { "13","12","31","24"};
		ContentResolver cr = getContentResolver();

		ContentValues values = new ContentValues();
		for (int i = 0; i < randomNames.length; i++) {
			values.put(HighScoreProvider.KEY_NAME, randomNames[i]);
			values.put(HighScoreProvider.KEY_SCORE, randomScores[i]);
			values.put(HighScoreProvider.KEY_PIC_ID, "-1");
			cr.insert(HighScoreProvider.CONTENT_URI, values);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_high_scores, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
