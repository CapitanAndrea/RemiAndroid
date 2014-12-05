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

public class DetailDialog extends DialogFragment {
	
	public static DetailDialog newInstance(int title){
        DetailDialog frag = new DetailDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		int title = getArguments().getInt("title");
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View textentryView=factory.inflate(R.layout.detail_dialog_layout, null);
        ((EditText)textentryView.findViewById(R.id.newItemName)).setHint(R.string.new_item_hint);
        Log.d("REMI", "ERRORE PESISSIMOOOO");
        return new AlertDialog.Builder(getActivity())
        //.setIcon(R.drawable.alert_dialog_icon)
    	.setView(textentryView)
        .setTitle(title)
        .setPositiveButton(android.R.string.ok,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	//positive button means create a new list with the given attributes
                    ((DetailActivity)getActivity()).confirmCreateItem(textentryView);
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
