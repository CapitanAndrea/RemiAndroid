package it.capitanilproductions.remi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class DeleteListDialog extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final View textentryView=LayoutInflater.from(getActivity()).inflate(R.layout.master_dialog_layout, null);
		((EditText)textentryView.findViewById(R.id.new_list_name)).setText(getArguments().getString("oldListName"));
		((CheckBox)textentryView.findViewById(R.id.new_list_abo)).setChecked(getArguments().getBoolean("oldAbo"));
		((CheckBox)textentryView.findViewById(R.id.new_list_mtb)).setChecked(getArguments().getBoolean("oldMtb"));
		builder.setTitle(R.string.delete_list_title)
			   .setView(textentryView)
               .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   //positive button means modify list
                	   ((MasterActivity)getActivity()).confirmModifyList(textentryView);
                       // FIRE ZE MISSILES!
                   }
               })
               .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   //negative button means delete list
                	   ((MasterActivity)getActivity()).confirmDeleteList(getArguments().getString("title"));
                   }
               })
               .setNeutralButton(R.string.dialog_neutral, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// neutral button means do nothing
					}
				});
        // Create the AlertDialog object and return it
        return builder.create();
	}
}
