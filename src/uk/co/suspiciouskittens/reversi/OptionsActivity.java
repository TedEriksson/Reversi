package uk.co.suspiciouskittens.reversi;

import java.io.IOException;
import java.io.InputStream;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class OptionsActivity extends Activity {

	private EditText player1, player2;
	private Button player1Import, player2Import;
	private ImageView player1Image, player2Image;
	private Button saveButton;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		// Show the Up button in the action bar.
		setupActionBar();
		SharedPreferences prefs = this.getSharedPreferences(
				"uk.co.suspiciouskittens.reversi.prefs", Context.MODE_PRIVATE);

		// String player1Text = prefs.getString(p1Text, "CANT FIND");

		player1 = (EditText) findViewById(R.id.optionsPlayer1EditText);
		player1Import = (Button) findViewById(R.id.optionsPlayer1Button);
		player1Image = (ImageView) findViewById(R.id.player1image);
		player2 = (EditText) findViewById(R.id.optionsPlayer2EditText);
		player2Import = (Button) findViewById(R.id.optionsPlayer2Button);
		player2Image = (ImageView) findViewById(R.id.player2image);

		player1.setText(prefs.getString(GameScreen.PREFS + ".p1Text",
				"CANT FIND"));
		player2.setText(prefs.getString(GameScreen.PREFS + ".p2Text",
				"CANT FIND"));

		saveButton = (Button) findViewById(R.id.save_changes);

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences prefs = getSharedPreferences(
						"uk.co.suspiciouskittens.reversi.prefs",
						Context.MODE_PRIVATE);
				prefs.edit()
						.putString(GameScreen.PREFS + ".p1Text",
								player1.getText().toString()).commit();
				prefs.edit()
						.putString(GameScreen.PREFS + ".p2Text",
								player2.getText().toString()).commit();
				
				Intent intent = new Intent(getApplicationContext(),
						MenuScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			}
		});

		player1Import.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doLaunchContactPicker(v, 1);

			}
		});

		player2Import.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doLaunchContactPicker(v, 2);

			}
		});

	}

	private static final int CONTACT_PICKER_RESULT = 1001;
	private static final int CONTACT_PICKER_RESULT2 = 1002;

	public void doLaunchContactPicker(View view, int player) {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
		if (player == 2) {
			startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT2);
		} else {
			startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		int player = 1;
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CONTACT_PICKER_RESULT2:
				player++;
			case CONTACT_PICKER_RESULT:
				Cursor cursor = null;
				String name = "";
				String personId;
				Uri person = null;
				try {
					Uri result = data.getData();
					// Log.v(DEBUG_TAG, "Got a contact result: "
					// + result.toString());
					// get the contact id from the Uri
					String id = result.getLastPathSegment();
					// query for everything email
					cursor = getContentResolver().query(Contacts.CONTENT_URI,
							null, Contacts._ID + "=?", new String[] { id },
							null);
					int idIdx = cursor.getColumnIndex(Contacts._ID);
					int nameIdx = cursor.getColumnIndex(Contacts.DISPLAY_NAME);
					// let's just get the first email
					if (cursor.moveToFirst()) {
						personId = cursor.getString(idIdx);
						name = cursor.getString(nameIdx);
						// Log.v(DEBUG_TAG, "Got email: " + email);
					} else {
						// Log.w(DEBUG_TAG, "No results");
					}
					person = ContentUris.withAppendedId(Contacts.CONTENT_URI,
							Long.parseLong(id));
				} catch (Exception e) {
					// Log.e(DEBUG_TAG, "Failed to get email data", e);
				} finally {
					if (cursor != null) {
						cursor.close();
					}
					// EditText emailEntry = (EditText)
					// findViewById(R.id.invite_email);

					InputStream photoStream = Contacts
							.openContactPhotoInputStream(getContentResolver(),
									person);
					if (photoStream != null) {
						Bitmap photo = BitmapFactory.decodeStream(photoStream);
						if (player == 1) {
							player1Image.setImageBitmap(photo);
						} else if (player == 2)
							player2Image.setImageBitmap(photo);

						try {
							photoStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						if (player == 1) {
							player1Image
									.setImageResource(R.drawable.cell_black);

						} else if (player == 2) {
							player2Image
									.setImageResource(R.drawable.cell_white);
						}
					}
					if (player == 1) {
						player1.setText(name);

					} else if (player == 2) {
						player2.setText(name);

					}

					if (name.length() == 0) {
						Toast.makeText(this, "No name found for contact.",
								Toast.LENGTH_LONG).show();
					}
				}
				break;
			}
		} else {
			// gracefully handle failure
			// Log.w(DEBUG_TAG, "Warning: activity result not ok");
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
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
