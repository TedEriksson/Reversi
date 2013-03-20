package uk.co.suspiciouskittens.reversi;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	private Cell[] board;
	private int boardSize;

	public ImageAdapter(Context c, Cell[] board, int boardSize) {
		context = c;
		this.board = board;
		this.boardSize = boardSize;
	}

	@Override
	public int getCount() {
		return board.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(parent
					.getWidth() / boardSize, parent.getWidth() / boardSize));

			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageView.setPadding(0, 0, 0, 0);
			imageView.setMaxHeight(10);
		} else {
			imageView = (ImageView) convertView;
		}

		if (board[position].isHinting()) {
			imageView.setImageResource(R.drawable.cell_move);
		} else {
			switch (board[position].getState()) {
			case 1:
				imageView.setImageResource(R.drawable.cell_black);
				break;

			case 2:
				imageView.setImageResource(R.drawable.cell_white);
				break;

			default:
				imageView.setImageResource(R.drawable.cell_background);
				break;

			}
		}

		return imageView;
	}
}
