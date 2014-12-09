package it.capitanilproductions.remi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.ResourceCursorAdapter;

public class DetailCursorAdapter extends ResourceCursorAdapter implements OnClickListener, OnLongClickListener, OnTouchListener {

	private DetailActivity listener;
	private GestureDetector detector;
	
	public DetailCursorAdapter(DetailActivity context, int layout, Cursor c,
			boolean autoRequery) {
		super(context, layout, c, autoRequery);
		listener=context;
		detector=new GestureDetector(listener, new ItemGestureListener(listener));
	}

	public DetailCursorAdapter(DetailActivity context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
		listener=context;
		detector=new GestureDetector(listener, new ItemGestureListener(listener));
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		CheckBox row=((CheckBox)view.findViewById(R.id.detailRowEntry));
		boolean check;
		String itemName=cursor.getString(cursor.getColumnIndex(DBList.COLOUMN_NAME));
		itemName=itemName.replace("''", "'");
		check=cursor.getInt(cursor.getColumnIndex(DBList.COLOUMN_IS_CHECKED))==1 ? true : false;
		row.setText(itemName);
		if(check){
			row.setPaintFlags(row.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			row.setTextColor(Color.GRAY);
		}
		else{
			row.setPaintFlags(row.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
			row.setTextColor(Color.BLACK);
		}
		row.setChecked(check);
		row.setOnClickListener(this);
		row.setOnLongClickListener(this);
		row.setOnTouchListener(this);
	}

	@Override
	public void onClick(View v) {
		listener.onItemClick(v);
		
	}

	@Override
	public boolean onLongClick(View v) {
		listener.onItemLongClick(v);
		return false;
	}

//	should work, but better find info about this warning
	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouch(View v, MotionEvent event) {
//		v.performClick();
		listener.gesturedView=v;
		detector.onTouchEvent(event);
		return true;
	}

}
