package it.capitanilproductions.remi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class MasterDialog extends DialogFragment {
	
	public static MasterDialog newInstance(int title){
        MasterDialog frag = new MasterDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		int title = getArguments().getInt("title");
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View textentryView=factory.inflate(R.layout.master_dialog_layout, null);
        ((EditText)textentryView.findViewById(R.id.new_list_name)).setHint(R.string.new_list_hint);
        return new AlertDialog.Builder(getActivity())
        //.setIcon(R.drawable.alert_dialog_icon)
    	.setView(textentryView)
        .setTitle(title)
        .setPositiveButton(android.R.string.ok,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	//positive button means create a new list with the given attributes
                    ((MasterActivity)getActivity()).confirmCreateList(textentryView);
                }
            }
        )
        .setNegativeButton(R.string.dialog_neutral,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	//negative button means do nothing.
                }
            }
        )
        .create();
	}

}
