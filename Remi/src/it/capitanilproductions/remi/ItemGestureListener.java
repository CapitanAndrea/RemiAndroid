package it.capitanilproductions.remi;

import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

public class ItemGestureListener extends SimpleOnGestureListener {

	DetailActivity activity;
	
	public ItemGestureListener(DetailActivity context){
		activity=context;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		//accept the gesture
		return true;
	}
	
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
//		Log.d("REMI", "Single tap detected: "+e.toString());
//		activity.onItemClick(null);
		return true;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
//		Log.d("REMI", "Fling detected: "+e1.toString()+e2.toString());
//		activity.onItemClick(null);
		return true;
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Log.d("REMI", "Double tap detected: "+e.toString());
		activity.showModifyItemDialog();
		return true;
	}
}
