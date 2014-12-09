package it.capitanilproductions.remi;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class MasterCursorAdapter extends ResourceCursorAdapter {

	private MasterActivity activity;
	
	public MasterCursorAdapter(MasterActivity context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
		activity=context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
//		set list name
		TextView field=((TextView)view.findViewById(R.id.rowListName));
		String name=cursor.getString(cursor.getColumnIndex(DBList.COLOUMN_NAME));
		name=name.replace("''", "'");
		field.setText(name);
//		set done items
		field=((TextView)view.findViewById(R.id.itemsChecked));
		int value=cursor.getInt(cursor.getColumnIndex(DBList.COLOUMN_CHECKED));
		name=""+value;
		field.setText(name);
//		set total items
		field=((TextView)view.findViewById(R.id.totalItems));
		value=cursor.getInt(cursor.getColumnIndex(DBList.COLOUMN_CHECKED));
		name=""+value;
		field.setText(name);
//		set mtb icon
		boolean option=cursor.getInt(cursor.getColumnIndex(DBList.COLOUMN_MOVETOBOTTOM))==1 ? true:false;
		ImageView image;
		if(option){
			image=(ImageView)view.findViewById(R.id.mtbIcon);
			image.setImageResource(R.drawable.ic_mtb);
		}
//		set abo icon
		option=cursor.getInt(cursor.getColumnIndex(DBList.COLOUMN_ABORDER))==1 ? true:false;
		if(option){
			image=(ImageView)view.findViewById(R.id.aboIcon);
			image.setImageResource(R.drawable.ic_abo);
		}
	}

}
