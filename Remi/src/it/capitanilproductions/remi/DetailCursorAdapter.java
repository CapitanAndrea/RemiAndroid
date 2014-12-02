package it.capitanilproductions.remi;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ResourceCursorAdapter;

public class DetailCursorAdapter extends ResourceCursorAdapter {

	public DetailCursorAdapter(Context context, int layout, Cursor c,
			boolean autoRequery) {
		super(context, layout, c, autoRequery);
	}

	public DetailCursorAdapter(Context context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		CheckedTextView row=((CheckedTextView)view.findViewById(R.id.detailRowEntry));
		String itemName=cursor.getString(cursor.getColumnIndex(DBList.COLOUMN_NAME));
		itemName=itemName.replace("''", "'");
		row.setText(itemName);
		row.setChecked(cursor.getInt(cursor.getColumnIndex(DBList.COLOUMN_IS_CHECKED))==1 ? true : false);
	}

}
