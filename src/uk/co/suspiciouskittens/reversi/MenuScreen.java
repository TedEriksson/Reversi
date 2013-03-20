package uk.co.suspiciouskittens.reversi;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MenuScreen extends Activity {

	private Button normalButton;
	private Button timedButton;
	private Button optionsButton;
	public static Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_menu_screen);
		activity = this;
		normalButton = (Button) findViewById(R.id.normal_button);
		timedButton = (Button) findViewById(R.id.timed_button);
		optionsButton = (Button) findViewById(R.id.options_button);

		normalButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MenuScreen.activity);
				builder.setMessage(
						getString(R.string.dialog_start_message_play) + " "
								+ getString(R.string.normal))
						.setTitle(getString(R.string.dialog_title_play))
						.setNegativeButton(R.string.cpu,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												getApplicationContext(),
												GameScreen.class);
										intent.putExtra("VS", 0);
										intent.putExtra("GAME_MODE", 0);
										startActivity(intent);
									}
								})
						.setPositiveButton(R.string.player,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												getApplicationContext(),
												GameScreen.class);
										intent.putExtra("VS", 1);
										intent.putExtra("GAME_MODE", 0);
										startActivity(intent);
									}
								});
				Dialog dialog = builder.create();
				dialog.show();

			}

		});

		timedButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MenuScreen.activity);
				builder.setMessage(
						getString(R.string.dialog_start_message_play) + " "
								+ getString(R.string.timed))
						.setTitle(getString(R.string.dialog_title_play))
						.setNegativeButton(R.string.cpu,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												getApplicationContext(),
												GameScreen.class);
										intent.putExtra("VS", 0);
										intent.putExtra("GAME_MODE", 1);
										startActivity(intent);
									}
								})
						.setPositiveButton(R.string.player,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												getApplicationContext(),
												GameScreen.class);
										intent.putExtra("VS", 1);
										intent.putExtra("GAME_MODE", 1);
										startActivity(intent);
									}
								});
				Dialog dialog = builder.create();
				dialog.show();

			}

		});
		
		optionsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						getApplicationContext(),
						OptionsActivity.class);
				startActivity(intent);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_menu_screen, menu);
		return true;
	}
}
