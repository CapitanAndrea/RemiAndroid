package it.capitanilproductions.remi;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ResourceCursorAdapter;

public class DetailCursorAdapter extends ResourceCursorAdapter implements OnClickListener {

	private DetailActivity activity;
	
	public DetailCursorAdapter(Context context, int layout, Cursor c,
			boolean autoRequery) {
		super(context, layout, c, autoRequery);
	}

	public DetailCursorAdapter(Context context, int layout, Cursor c, int flags, DetailActivity detailActivity) {
		super(context, layout, c, flags);
		activity=detailActivity;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		CheckBox row=((CheckBox)view.findViewById(R.id.detailRowEntry));
		String itemName=cursor.getString(cursor.getColumnIndex(DBList.COLOUMN_NAME));
		itemName=itemName.replace("''", "'");
		row.setText(itemName);
		row.setChecked(cursor.getInt(cursor.getColumnIndex(DBList.COLOUMN_IS_CHECKED))==1 ? true : false);
		row.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		activity.onItemClick(v);
		
	}

}
