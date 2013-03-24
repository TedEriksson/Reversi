package uk.co.suspiciouskittens.reversi;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.support.v4.app.NavUtils;

public class OptionsActivity extends Activity {

	private EditText player1, player2, time;
	private Button player1Import, player2Import;
	private ImageView player1Image, player2Image;
	private Button saveButton, clearButton;
	private ContactPhotoLoader photoLoader = new ContactPhotoLoader();
	private String player1Id = "", player2Id = "";
	private ToggleButton showMoves;
	public static Activity activity;
	private boolean moves = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		// Show the Up button in the action bar.
		setupActionBar();
		SharedPreferences prefs = this.getSharedPreferences(
				"uk.co.suspiciouskittens.reversi.prefs", Context.MODE_PRIVATE);

		player1 = (EditText) findViewById(R.id.optionsPlayer1EditText);
		player1Import = (Button) findViewById(R.id.optionsPlayer1Button);
		player1Image = (ImageView) findViewById(R.id.player1image);
		player2 = (EditText) findViewById(R.id.optionsPlayer2EditText);
		player2Import = (Button) findViewById(R.id.optionsPlayer2Button);
		player2Image = (ImageView) findViewById(R.id.player2image);
		time = (EditText) findViewById(R.id.optionstime);
		showMoves = (ToggleButton) findViewById(R.id.hints_toggle);
		
		showMoves.setChecked(prefs.getBoolean(GameScreen.PREFS + ".moves", true));

		time.setText(prefs.getString(GameScreen.PREFS + ".timeText",
				"60000"));
		player1.setText(prefs.getString(GameScreen.PREFS + ".p1Text",
				"Player 1"));
		player2.setText(prefs.getString(GameScreen.PREFS + ".p2Text",
				"Player 2"));

		player1Id = prefs.getString(GameScreen.PREFS + ".p1ID", "-1");

		player2Id = prefs.getString(GameScreen.PREFS + ".p2ID", "-1");

		if (player1Id.equals("-1")) {
			player1Image.setImageResource(R.drawable.cell_black);
		} else {
			player1Image.setImageBitmap(photoLoader.load(player1Id,
					getContentResolver()));
		}

		if (player2Id.equals("-1")) {
			player2Image.setImageResource(R.drawable.cell_white);
		} else {
			player2Image.setImageBitmap(photoLoader.load(player2Id,
					getContentResolver()));
		}

		saveButton = (Button) findViewById(R.id.save_changes);
		clearButton = (Button) findViewById(R.id.delete_data_button);

		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deleteData();
			}
		});
		showMoves.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				moves = showMoves.isChecked();
			}
		});
		
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences prefs = getSharedPreferences(
						"uk.co.suspiciouskittens.reversi.prefs",
						Context.MODE_PRIVATE);
				prefs.edit()
						.putString(GameScreen.PREFS + ".p1Text",
								player1.getText().toString())
						.putString(GameScreen.PREFS + ".p2Text",
								player2.getText().toString())
						.putString(GameScreen.PREFS + ".p1ID",
								player1Id.toString())
						.putString(GameScreen.PREFS + ".p2ID",
								player2Id.toString())
						.putString(GameScreen.PREFS + ".timeText", time.getText().toString())
						.putBoolean(GameScreen.PREFS + ".moves", moves).commit();

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
		String id = null;
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CONTACT_PICKER_RESULT2:
				player++;
			case CONTACT_PICKER_RESULT:
				Cursor cursor = null;
				String name = "";
				String personId = null;
				try {
					Uri result = data.getData();

					id = result.getLastPathSegment();

					cursor = getContentResolver().query(Contacts.CONTENT_URI,
							null, Contacts._ID + "=?", new String[] { id },
							null);
					int idIdx = cursor.getColumnIndex(Contacts._ID);
					int nameIdx = cursor.getColumnIndex(Contacts.DISPLAY_NAME);

					if (cursor.moveToFirst()) {
						personId = cursor.getString(idIdx);
						name = cursor.getString(nameIdx);

					}
				} catch (Exception e) {

				} finally {
					if (cursor != null) {
						cursor.close();
					}
					Bitmap photo = photoLoader.load(personId,
							getContentResolver());

					if (player == 1) {
						player1Id = personId;
						if (photo == null || player1Id == "-1") {
							player1Id = "-1";
							player1Image
									.setImageResource(R.drawable.cell_black);
						} else
							player1Image.setImageBitmap(photo);
					} else if (player == 2) {
						player2Id = personId;
						if (photo == null || player2Id == "-1") {
							player2Id = "-1";
							player2Image
									.setImageResource(R.drawable.cell_white);
						} else
							player2Image.setImageBitmap(photo);
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
	
	private void deleteData() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder .setMessage(getString(R.string.delete_data))
				.setTitle(getString(R.string.dialog_title_delete))
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						})
				.setPositiveButton(R.string.delete,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								SharedPreferences prefs = getSharedPreferences(
										"uk.co.suspiciouskittens.reversi.prefs",
										Context.MODE_PRIVATE);
								prefs.edit().clear().commit();
								Intent intent = new Intent(
										getApplicationContext(),
										MenuScreen.class);
								startActivity(intent);
							}
						});
		Dialog dialog = builder.create();
		dialog.show();
	}

}
