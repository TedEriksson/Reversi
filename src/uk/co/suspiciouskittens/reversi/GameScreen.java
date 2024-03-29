package uk.co.suspiciouskittens.reversi;

import java.util.Random;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class GameScreen extends Activity {

	final private int boardSize = 8;
	private Cell[] board = new Cell[boardSize * boardSize];
	private ImageAdapter boardAdapter;
	private GridView boardGrid;
	private TextView player1Score, player2Score;
	private TextView player1Time, player2Time;
	private TextView player2, player1;
	private Button showMoves;
	private int playerNo = 1, vs, gameMode;
	private String[] playerNames = new String[2];
	private boolean prevPlayerCantMove = false;
	private int[] score = { 2, 2 };
	public static Activity activity;
	private CountDownTimer timer;
	private long[] timeLeft = { 60000, 60000 };
	private Random random = new Random();
	private boolean timerRunning = false, gameOver = false;
	private static boolean playSounds = true;
	private ImageView player1Contact, player2Contact;
	public static final String PREFS = "uk.co.suspiciouskittens.reversi.prefs";
	private String player1Id, player2Id;
	private long time, savedTime = -1;
	private int beepCountDown = 30000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_screen);
		SharedPreferences prefs = this.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		activity = this;

		// Show the Up button in the action bar.
		setupActionBar();

		// Get intent from previous Activity with the extras
		Intent intent = getIntent();
		vs = intent.getIntExtra("VS", 2);
		gameMode = intent.getIntExtra("GAME_MODE", 2);

		// Initialise the Cells
		for (int i = 0; i < board.length; i++) {
			board[i] = new Cell();
			board[i].setPosition(i);
		}

		// Place starting pieces
		board[(board.length / 2) - (boardSize / 2) - 1].setState(2);
		board[(board.length / 2) - (boardSize / 2)].setState(1);
		board[(board.length / 2) + (boardSize / 2) - 1].setState(1);
		board[(board.length / 2) + (boardSize / 2)].setState(2);
		
		player1Contact = (ImageView) findViewById(R.id.player1_contact_image);
		player2Contact = (ImageView) findViewById(R.id.player2_contact_image);

		// Set up Player name tags
		player1 = (TextView) findViewById(R.id.player1);
		player2 = (TextView) findViewById(R.id.player2);

		// Set up score views
		player1Score = (TextView) findViewById(R.id.player1_score);
		player2Score = (TextView) findViewById(R.id.player2_score);

		
		// Set default scores
		player1Score.setText(getString(R.string.score) + " " + score[0]);
		player2Score.setText(getString(R.string.score) + " " + score[1]);

		// Sets current players background colour
		player2.setBackgroundColor(getResources().getColor(R.color.alpha));
		player1.setBackgroundColor(getResources().getColor(R.color.yellow));

		// Set up grid view for tiles
		boardGrid = (GridView) findViewById(R.id.gameboard_grid);

		// Set columns of grid view
		boardGrid.setNumColumns(boardSize);

		// Create image adapter passing the Cell[] and length of each side
		boardAdapter = new ImageAdapter(this, board, boardSize, prefs.getBoolean(GameScreen.PREFS + ".alt", false));

		// Set the grids adapter
		boardGrid.setAdapter(boardAdapter);

		// Starts a String that will become the title of the Activity
		String titleMessage = "VS ";

		// Sets player2's name to CPU if player chose vs CPU
		if (vs == 1) {
			titleMessage += getString(R.string.player2) + " ";
			player2.setText(R.string.player2);
		} else {
			titleMessage += getString(R.string.cpu) + " ";
		}

		// Checks chosen game mode and sets the title
		if (gameMode == 0) {
			titleMessage += "Playing " + getString(R.string.normal);
		} else {
			titleMessage += "Playing " + getString(R.string.timed);
		}

		// Sets the title to the String just created
		setTitle(titleMessage);
		
		
		time = Long.parseLong(prefs.getString(PREFS + ".timeText", "60")) * 1000;
		timeLeft[0] = time;
		timeLeft[1] = time;
		playerNames[0] = prefs.getString(PREFS + ".p1Text", "Player 1");
		
		player1Id = prefs.getString(GameScreen.PREFS + ".p1ID",
				"-1");
		
		player2Id = prefs.getString(GameScreen.PREFS + ".p2ID",
				"-1");
		
		ContactPhotoLoader photoLoader = new ContactPhotoLoader();
		
		if(player1Id.equals("-1")) {
			player1Contact.setImageResource(R.drawable.cell_black);
		} else {
			player1Contact.setImageBitmap(photoLoader.load(player1Id,
					getContentResolver()));
		}
		
		if (vs == 0) {
			playerNames[1] = getString(R.string.cpu);
		} else {
			playerNames[1] = prefs.getString(PREFS + ".p2Text", "Player 2");
			if(player2Id.equals("-1")) {
				player2Contact.setImageResource(R.drawable.cell_white);
			} else {
				player2Contact.setImageBitmap(photoLoader.load(player2Id,
						getContentResolver()));
			}
		}
		// Set up timer views
				player1Time = (TextView) findViewById(R.id.player1_time);
				player2Time = (TextView) findViewById(R.id.player2_time);
				if (gameMode == 1) {
					player1Time.setText("Time: " + timeLeft[0] / 1000);
					player2Time.setText("Time: " + timeLeft[1] / 1000);
				} else {
					player1Time.setText("Time: ∞");
					player2Time.setText("Time: ∞");
				}
 

		player1.setText(playerNames[0]);
		player2.setText(playerNames[1]);
		
		playSounds = prefs.getBoolean(GameScreen.PREFS + ".sounds", true);

		showMoves = (Button) findViewById(R.id.show_moves);
		
		if(!prefs.getBoolean(GameScreen.PREFS + ".moves", true))
			showMoves.setEnabled(false);
		showMoves.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Queue queue = checkCanMove();
				while (!queue.isEmpty()) {
					board[queue.remove()].setHinting(true);
				}
				boardAdapter.notifyDataSetChanged();
			}

		});

		// Creates an OnItemClickListener
		OnItemClickListener boardClickListener = new OnItemClickListener() {

			// Sets what will happen when an item is clicked
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if ((vs == 0 && playerNo == 1 && !gameOver)
						|| (vs == 1 && !gameOver))
					playTurn(position);
			}

		};
		// Set the grid item listener
		boardGrid.setOnItemClickListener(boardClickListener);

	}
	
	protected void onPause () {
		super.onPause();
		if(gameMode == 1 && timer != null) {
			savedTime = timeLeft[playerNo -1];
			timer.cancel();
		}
	}
	
	protected void onResume () {
		super.onResume();
		if(gameMode == 1 && timer != null) {
			startTimer(savedTime);
			savedTime = -1;
		}
	}

	// Calculates each players core and passes back an array
	private int[] calcScore() {
		score[0] = 0;
		score[1] = 0;
		for (int n = 0; n < board.length; n++) {
			if (board[n].getState() == 1) {
				score[0]++;
			} else if (board[n].getState() == 2) {
				score[1]++;
			}
		}
		return score;
	}

	private void endGo() {
		swapPlayers();
		if (checkCanMove().isEmpty()) {
			if (prevPlayerCantMove) {
				onGameEnd(1);
			} else {
				Toast cantMove = Toast.makeText(getApplicationContext(),
						playerNames[playerNo - 1] + " cannot play a move",
						Toast.LENGTH_LONG);
				cantMove.show();
				prevPlayerCantMove = true;
				endGo();
			}
		} else {
			prevPlayerCantMove = false;
		}
		if (vs == 0 && playerNo == 2) {
			cpuGo();
		}
	}

	// Swaps the playerNo and sets colour denoting who's go it is
	private void swapPlayers() {
		if (gameMode == 1 && timerRunning)
			stopTimer();
		if (playerNo == 1) {
			player1.setBackgroundColor(getResources().getColor(R.color.alpha));
			player2.setBackgroundColor(getResources().getColor(R.color.yellow));
			playerNo = 2;
		} else {
			player2.setBackgroundColor(getResources().getColor(R.color.alpha));
			player1.setBackgroundColor(getResources().getColor(R.color.yellow));
			playerNo = 1;
		}
		if (gameMode == 1) {
			timerRunning = true;
			startTimer(time);
		}
	}

	// This method checks if a move is valid and then creates a new board based
	// on the item clicked. It returns the new board, if the move is not valid
	// it returns a null board
	private Cell[] checkMove(int position) {
		// This will hold an array of positions that surround the chosen
		// position
		int[] direction;
		// Creates a new board from original
		Cell[] tempBoard = new Cell[board.length];
		for (int i = 0; i < board.length; i++) {
			board[i].setHinting(false);
			tempBoard[i] = new Cell();
			tempBoard[i].setState(board[i].getState());
		}

		// Sets inverse to enemy player number
		int inverse = 1;
		if (playerNo == 1) {
			inverse = 2;
		}

		boolean moveMade = false;

		// If the position is empty
		if (tempBoard[position].getState() == 0) {
			// Run checkValidMove which returns an array of positions
			// surrounding our position
			direction = checkValidMove(position);

			// Starting at up loop through all 8 positions surrounding chosen
			// one
			for (int i = 0; i < 8; i++) {
				// if the current direction (e.g. above us) is not an edge and
				// is an enemy token
				if (direction[i] != -1
						&& tempBoard[direction[i]].getState() == inverse) {
					Queue queue = new Queue();
					// add our starting position to queue
					queue.add(position);
					// create a temporary direction so we don't overwrite our
					// current one
					int tempDirection = direction[i];
					// using -1 as an exit state
					while (tempDirection != -1) {
						// add next position to queue
						queue.add(tempDirection);
						// set tempDirection to checkValidMoveDirection which
						// check one direction to see if it is a valid direction
						// (i.e. not out of bounds)
						tempDirection = checkValidMoveDirection(i + 1,
								tempDirection);
						// if not out of bounds and it is our players piece, add
						// to the queue then exit while loop
						if (tempDirection != -1
								&& tempBoard[tempDirection].getState() == playerNo) {
							queue.add(tempDirection);
							tempDirection = -1;
						}
						// if not out of bound but empty cell exit while loop
						if (tempDirection != -1
								&& tempBoard[tempDirection].getState() == 0) {
							tempDirection = -1;
						}
					}
					// if the last piece in the queue is ours and the first cell
					// is empty the set moveMade to true and add this queue of
					// positions to our temporary game board as our pieces
					if (tempBoard[queue.get(queue.size() - 1)].getState() == playerNo
							&& tempBoard[queue.remove()].getState() == 0) {
						moveMade = true;
						while (!queue.isEmpty()) {
							tempBoard[queue.remove()].setState(playerNo);
						}
					}
				}
			}
		}
		// if no moves were made return a null board
		if (!moveMade) {
			tempBoard = null;
		}
		return tempBoard;
	}

	// Runs through each direction checking if the are an edge. returns array of
	// positions surrounding our position and -1 if that position is out of
	// bounds
	private int[] checkValidMove(int position) {
		int[] array = new int[8];
		for (int i = 0; i < 8; i++) {
			array[i] = checkValidMoveDirection(i + 1, position);
		}
		return array;
	}

	// returns position of our chosen direction and -1 if out of bounds
	private int checkValidMoveDirection(int direction, int position) {
		// 1=Up 2=UpRight 3=Right 4 = DownRight etc.
		switch (direction) {
		case 1:
			if (Math.floor((float) position / boardSize) != 0) {
				return position - boardSize;
			}
			break;
		case 2:
			if (Math.floor((float) position / boardSize) != 0
					&& position % boardSize != boardSize - 1) {
				return position - boardSize + 1;
			}
			break;
		case 3:
			if (position % boardSize != boardSize - 1

			) {
				return position + 1;
			}
			break;
		case 4:
			if (position % boardSize != boardSize - 1
					&& Math.floor((float) position / boardSize) != boardSize - 1) {
				return position + boardSize + 1;
			}
			break;
		case 5:
			if (Math.floor((float) position / boardSize) != boardSize - 1) {
				return position + boardSize;
			}
			break;
		case 6:
			if (position % boardSize != 0
					&& Math.floor((float) position / boardSize) != boardSize - 1) {
				return position + boardSize - 1;
			}
			break;
		case 7:
			if (position % boardSize != 0) {
				return position - 1;
			}
			break;
		case 8:
			if (Math.floor((float) position / boardSize) != 0
					&& position % boardSize != 0) {
				return position - boardSize - 1;
			}
			break;
		}
		return -1;
	}

	private Queue checkCanMove() {
		Queue positions = new Queue();
		for (int i = 0; i < board.length; i++) {
			if (checkMove(i) != null) {
				positions.add(i);
			}
		}
		return positions;
	}

	private void cpuGo() {
		new CountDownTimer(random.nextInt(1000) + 500, 500) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinish() {
				Queue queuePos = checkCanMove();

				if (!queuePos.isEmpty()) {
					int bestMove = 0, bestPos = queuePos.get(0);
					while (!queuePos.isEmpty()) {
						int tempMove = 0;
						int position = queuePos.remove();
						Cell[] tempBoard = checkMove(position);
						for (int i = 0; i < tempBoard.length; i++) {
							if (tempBoard[i].getState() == playerNo
									&& board[i].getState() != playerNo) {
								if (i == 0 || i == boardSize - 1
										|| i == board.length - boardSize - 1
										|| i == board.length - 1)
									tempMove += 10;
								if (Math.floor(i / boardSize) == 0
										|| Math.ceil(i / boardSize) == boardSize
										|| i % boardSize == 0
										|| i % boardSize == boardSize - 1)
									tempMove += 2;
								tempMove++;
							}
						}
						if (tempMove > bestMove) {
							bestPos = position;
						} else if (tempMove == bestMove) {
							if (random.nextInt(2) == 1) {
								bestPos = position;
							}
						}
					}
					if(playerNo == 2)
						playTurn(bestPos);
				}
			}
		}.start();

	}

	private void playTurn(int position) {
		// Creates a temporary board and runs checkMove passing which
		// Cell has been clicked
		Cell[] tempBoard = checkMove(position);

		// If checkMove does not returns null board then set pieces on
		// real board, swap players and check and set scores
		if (tempBoard != null) {
			for (int i = 0; i < board.length; i++) {
				board[i].setState(tempBoard[i].getState());
			}
			board[position].setState(playerNo);
			calcScore();
			player1Score.setText(getString(R.string.score) + " " + score[0]);
			player2Score.setText(getString(R.string.score) + " " + score[1]);
			boardAdapter.notifyDataSetChanged();
			endGo();
		} else {
			// Creates a Toast
			Toast toast = Toast.makeText(getApplicationContext(),
					"Not a valid move!", Toast.LENGTH_LONG);
			// else show "Not a valid move!" toast
			toast.show();
		}
	}

	private void onGameEnd(int endType) {
		
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		boolean didPlayer1win = false;
		if (gameMode == 1 && timerRunning) {
			stopTimer();
			timerRunning = false;
		}
		gameOver = true;
		AlertDialog.Builder builder = new AlertDialog.Builder(
				GameScreen.activity);
		switch (endType) {
		case 1:
			didPlayer1win = (score[0] > score[1]);
			builder.setMessage("The Winner is "
					+ (didPlayer1win ? playerNames[0] : playerNames[1]));
			break;
		case 2:
			didPlayer1win = (playerNo == 2);
			builder.setMessage(playerNames[playerNo - 1]
					+ " ran out of time! The Winner is "
					+ (didPlayer1win ? playerNames[0] : playerNames[1]));
			break;
		}
		
		values.put(HighScoreProvider.KEY_NAME, didPlayer1win ? playerNames[0] : playerNames[1]);
		values.put(HighScoreProvider.KEY_SCORE, didPlayer1win ? score[0] : score[1]);
		values.put(HighScoreProvider.KEY_PIC_ID, didPlayer1win ? player1Id : player2Id);
		cr.insert(HighScoreProvider.CONTENT_URI, values);
		
		builder.setTitle("Game Over")
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						})
				.setPositiveButton("New Game",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								newGame();
							}
						});
		Dialog dialog = builder.create();
		dialog.show();
	}
	
	private void startTimer(long timerLength) {
		
		timer = new CountDownTimer(timerLength, 100) {

			@Override
			public void onTick(long millisUntilFinished) {
				beepCountDown -= 100;
				if(beepCountDown <= 0) {
					playBeep(getApplicationContext());
					beepCountDown = 30000;
				}
				timeLeft[playerNo - 1] = millisUntilFinished;
				if (playerNo == 1) {
					player1Time.setText("Time: " + millisUntilFinished/1000);
					timeLeft[0] = millisUntilFinished;
				} else {
					player2Time.setText("Time: " + millisUntilFinished/1000);
					timeLeft[1] = millisUntilFinished;
				}
			}

			@Override
			public void onFinish() {
				onGameEnd(2);
			}
		}.start();

	}

	private void stopTimer() {
		player1Time.setText("Time: " + time/1000);
		player2Time.setText("Time: " + time/1000);
		beepCountDown = 30000;
		timer.cancel();
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
		getMenuInflater().inflate(R.menu.game_screen, menu);
		return true;
	}
	
	public void onBackPressed() {
		//super.onBackPressed();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder .setMessage(getString(R.string.back_to_main_message))
				.setTitle(getString(R.string.back_to_main_title))
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						})
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								NavUtils.navigateUpFromSameTask(GameScreen.activity);
							}
						});
		Dialog dialog = builder.create();
		dialog.show();
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
			onBackPressed();
			return true;
		case R.id.newgame:
			newGame();
		}
		return super.onOptionsItemSelected(item);
	}

	private void newGame() {
		Intent intent = getIntent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public static void playBeep(Context context){  
		if(playSounds) {
			MediaPlayer mp = MediaPlayer.create(context, R.raw.beep); 
			mp.start();
		}
		}

}
