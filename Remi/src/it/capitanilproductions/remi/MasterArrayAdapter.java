package it.capitanilproductions.remi;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MasterArrayAdapter extends ArrayAdapter<RemiList> {
	
	Context activity;
	List<RemiList> data;

	public MasterArrayAdapter(Context context, int resource, RemiList[] objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
	}

	public MasterArrayAdapter(Context context, int resource, List<RemiList> objects) {
		super(context, resource, objects);
		activity=context;
		data=objects;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row=convertView;
		RemiList selectedList=data.get(position);
		
		if(row==null){
			row=((Activity)activity).getLayoutInflater().inflate(R.layout.master_row, parent, false);
		}
		((TextView)row.findViewById(R.id.rowListName)).setText(selectedList.name);
		int value=selectedList.checkedItems;
		((TextView)row.findViewById(R.id.itemsChecked)).setText(""+value);
		value=selectedList.totalItems;
		((TextView)row.findViewById(R.id.totalItems)).setText(value+"");
		ImageView imgView=((ImageView)row.findViewById(R.id.mtbIcon));
		if(selectedList.mtBottom) imgView.setVisibility(View.VISIBLE);
		else imgView.setVisibility(View.INVISIBLE);
		imgView=((ImageView)row.findViewById(R.id.aboIcon));
		if(selectedList.abOrder) imgView.setVisibility(View.VISIBLE);
		else imgView.setVisibility(View.INVISIBLE);
		return row;
	}
	
	
}
