package it.capitanilproductions.remi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class ModifyItemDialog extends DialogFragment {
	
	private static String oldName;
	
	public static DialogFragment newInstance(int title, String itemName){
		oldName=itemName;
        ModifyItemDialog frag = new ModifyItemDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final View textentryView=LayoutInflater.from(getActivity()).inflate(R.layout.detail_dialog_layout, null);
		((EditText)textentryView.findViewById(R.id.newItemName)).setHint(getResources().getString(R.string.new_item_hint));
		((EditText)textentryView.findViewById(R.id.newItemName)).setText(oldName);
		builder.setView(textentryView)
			.setTitle(R.string.modify_item_title)
			.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
                	   //positive button means modify item
                	((DetailActivity)getActivity()).confirmModifyItem(textentryView);
				}
			})
            .setNeutralButton(R.string.dialog_neutral, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
						// neutral button means do nothing
				}
			});
		
		return builder.create();
	}

}
