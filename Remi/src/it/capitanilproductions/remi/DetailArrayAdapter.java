package it.capitanilproductions.remi;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailArrayAdapter extends ArrayAdapter<RemiItem> {

	DetailActivity activity;
	List<RemiItem> data;
	
	public DetailArrayAdapter(DetailActivity context, int resource, List<RemiItem> objects) {
		super(context, resource, objects);
		activity=context;
		data=objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row=convertView;
		RemiItem selectedItem=data.get(position);
		boolean check=selectedItem.check;
		
		if(row==null){
			row=((Activity)activity).getLayoutInflater().inflate(R.layout.detail_row, parent, false);
		}
		TextView entry=(TextView)row.findViewById(R.id.detailRowEntry);
		entry.setText(selectedItem.name);
		if(check){
			entry.setPaintFlags(entry.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			entry.setTextColor(Color.GRAY);
		}
		else{
			entry.setPaintFlags(entry.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
			entry.setTextColor(Color.BLACK);
		}
		ImageView checkIcon=(ImageView)row.findViewById(R.id.detailCheck);
		if(selectedItem.check) checkIcon.setImageResource(android.R.drawable.checkbox_on_background);
		else checkIcon.setImageResource(android.R.drawable.checkbox_off_background); 
//		entry.setOnClickListener(activity);
		checkIcon=(ImageView)row.findViewById(R.id.detailPriority);
		switch (selectedItem.priority){
		case RemiItem.HIGH_PRIOROTY:{
			checkIcon.setImageResource(R.drawable.high_priority);
			break;
		}
		case RemiItem.MEDIUM_PRIOROTY:{
			checkIcon.setImageResource(R.drawable.mid_priority);
			break;
		}
		case RemiItem.LOW_PRIOROTY:{
			checkIcon.setImageResource(R.drawable.low_priority);
			break;
		}
		default: break;
		}
		return row;
	}

}
