package it.capitanilproductions.remi;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.NavUtils;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

public class DetailActivity extends ListActivity implements OnActionExpandListener, OnQueryTextListener, OnItemClickListener, /*OnItemLongClickListener,*/ OnClickListener, MultiChoiceModeListener {
	
	private static final String TAG="REMI-DETAIL";

	private static final String ITEMNAME="ItemName";
	private static final String ITEMCHECK="ItemCheck";
	private static final String ITEMPRIORITY="ItemPriority";
	
	private static String listName;
	private static boolean listABOrder;
	private static boolean listMTBottom;
	
	private ArrayList<RemiItem> items;
	private ArrayList<RemiItem> queriedItems=new ArrayList<RemiItem>();
	
	private int selectedPosition;
//	private int dialog;
	
	private ListView lw;

	public View gesturedView;
	
	private String search;
	
	Intent result;
	private int checkCount=0;
	private int totalCount=0;
	
	private Comparator<RemiItem> comparator;
	
	public ActionMode mActionMode; 
	private boolean[] deletingItems=null;
	private int selected=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		result=new Intent();
		setContentView(R.layout.activity_master);
//		retreiving values from intent
		Bundle extras=getIntent().getExtras();
		listName=extras.getString(MasterActivity.CHOSENLIST);
		if(listName==null) finish(); //if no list name is passed to this activity it commits suicide
		listABOrder=extras.getBoolean(MasterActivity.CHOSENLISTABORDER);
		listMTBottom=extras.getBoolean(MasterActivity.CHOSENLISTMTBOTTOM);
		comparator=new ItemComparator(listMTBottom, listABOrder);
		
        items=new ArrayList<RemiItem>();
        JsonReader reader=null;
        try{
	        InputStreamReader buf = new InputStreamReader(new FileInputStream(this.getFilesDir().getPath().toString()+"/"+listName));
	        reader=new JsonReader(new BufferedReader(buf));
//	        Log.d(TAG, "Begin loading data...");
			reader.beginArray();
	        while(reader.hasNext()){
	        	RemiItem nextItem=readNextItem(reader);
	        	if(nextItem!=null){
	        		items.add(nextItem);
	        		totalCount++;
	        		if(nextItem.check) checkCount++;
	        	}
//	        	Log.d(TAG, "Just read an item: "+nextItem);
	        }
	        reader.endArray();
	        reader.close();
//	        Log.d(TAG, "All items succesfully read");
        } catch(Exception e){
        	try {
        		Log.d(TAG, "Loading failed...", e);
				if(reader!=null) reader.close();
			} catch (IOException e1) {
				Toast.makeText(this, "Items loading critical error, report to developer!!", Toast.LENGTH_SHORT).show();
			}
        }
        

        updateView("onCreate");
		
		lw=((ListView)findViewById(android.R.id.list));
		lw.setOnItemClickListener(this);
//		lw.setOnItemLongClickListener(this);
		lw.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		lw.setMultiChoiceModeListener(this);
		
		setTitle(listName);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}
	
	private RemiItem readNextItem(JsonReader reader) {
		String itemName=null;
		boolean itemCheck=false;
		int itemPriority=RemiItem.MEDIUM_PRIOROTY;
		
		try{
			reader.beginObject();
			while(reader.hasNext()){
				String key=reader.nextName();
				switch(key){
				case ITEMNAME:{
					itemName=reader.nextString();
					break;
				}
				case ITEMCHECK:{
					itemCheck=reader.nextBoolean();
					break;
				}
				case ITEMPRIORITY:{
					itemPriority=reader.nextInt();
				}
				}
			}
			reader.endObject();
		}catch(Exception e){
			return null;
		}
		return new RemiItem(itemName, itemCheck, itemPriority);
	}

@Override
protected void onPause() {
	super.onPause();

//	Log.d(TAG, "onPause");
    File metadata=new File(this.getFilesDir(), listName);
    try {
		metadata.createNewFile();
	} catch (IOException e1) {
		e1.printStackTrace();
	}
    
    JsonWriter writer=null;
    try{
        BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(this.getFilesDir().getPath().toString()+"/"+listName));
        writer=new JsonWriter(new OutputStreamWriter(buf, "UTF-8"));
        
        writer.beginArray();
//        Log.d(TAG, "Start writing data... may take some time");
        for(RemiItem currentItem:items){
//        	Log.d(TAG, "writing: "+currentItem.toString());
        	writer.beginObject();
        	writer.name(ITEMNAME).value(currentItem.name);
        	writer.name(ITEMCHECK).value(currentItem.check);
        	writer.name(ITEMPRIORITY).value(currentItem.priority);
        	writer.endObject();
//        	Log.d(TAG, "Object succesfully wrote");
            writer.flush(); //needed to flush because the jsonArray closed before any byte was written
        }
        writer.endArray();
        writer.flush();
//        Log.d(TAG, "All data succesfully stored!!");
    }catch (Exception e){
    	Log.d(TAG, "Issues saving data", e);
    	try {
			if(writer!=null) writer.close();
		} catch (IOException e1) {
		}
    }
//    set result


}
	
//  override metodi per creazione e gestione menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
	  	MenuInflater inflater=getMenuInflater();
	  	inflater.inflate(R.menu.detail_activity_menu, menu);
	  	
	  	MenuItem searchMenuItem=menu.findItem(R.id.action_search_item);
	  	searchMenuItem.setOnActionExpandListener(this);
	    SearchView searchView = (SearchView) searchMenuItem.getActionView();
	    searchView.setOnQueryTextListener(this);
	    searchView.setQueryHint(getResources().getString(R.string.search_hint));
		  	
	  	return true; 
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	case R.id.action_search_item:{
    		return true;
    	}
    	case R.id.action_add_item:{
//    		dialog=R.string.list_creation;
    		showAddItemDialog();
    		return true;
    	}
    	case R.id.clean_list:{
    		Iterator<RemiItem> iterator=items.iterator();
    		while(iterator.hasNext()){
    			RemiItem currItem=iterator.next();
    			if(currItem.check==true){
    				iterator.remove();
    				totalCount--;
    			}
    		}
    		checkCount=0;
    		result.putExtra(MasterActivity.RETURNEDCHECKEDITEMS, checkCount);
    		result.putExtra(MasterActivity.RETURNEDTOTALITEMS, totalCount);
    		setResult(RESULT_OK, result);
	    	updateView("Clean list");
	    	return true;
    	}
    	case android.R.id.home:{
        	result.putExtra(MasterActivity.RETURNEDCHECKEDITEMS, checkCount);
        	result.putExtra(MasterActivity.RETURNEDTOTALITEMS, totalCount);
        	setResult(RESULT_OK, result);
    		NavUtils.navigateUpFromSameTask(this);
    		return true;
    	}
    	default:{
    		Toast.makeText(this, "Bottone sconosciuto pigiato", Toast.LENGTH_LONG).show();
    		return false;
    	}
    	}
    }
	
	@Override
	public boolean onQueryTextSubmit(String query) {
		// la searchbar non deve far niente in automatico, è tutto gestito nella onQueryTextChange
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		//per ogni nuova ricerca ricarico le liste che matchano
		if(!newText.isEmpty()) search=newText;
		else search=null;
		updateView("onQueryTextChange");
		return true;
	}

	@Override
	public boolean onMenuItemActionExpand(MenuItem item) {
		return true;
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem item) {
//		quando la serachview collassa faccio ricaricare tutte le liste
		if(item.getItemId()==R.id.action_search_item){
			search=null;
			updateView("onMenuItemActionCollapse");
		}
		return true;
	}
	
//	metodi per i dialog
	public void showAddItemDialog(){
		DialogFragment newFrag=DetailDialog.newInstance(R.string.action_add_item);
		newFrag.show(getFragmentManager(), "DetailFragment");
	}
	
	public void showModifyItemDialog(){
		String itemName=((TextView)gesturedView.findViewById(R.id.detailRowEntry)).getText().toString();
		DialogFragment modifyDialog=ModifyItemDialog.newInstance(itemName);
		modifyDialog.show(getFragmentManager(), "DetailFragment");
	}
	
	public void confirmCreateItem(View textentryView) {
		String name=((EditText)textentryView.findViewById(R.id.newItemName)).getText().toString();
		for(RemiItem currItem:items){
			if(currItem.name.compareTo(name)==0){
				currItem.check=false;
				checkCount--;
				updateView("confirmCreateItem");
				return;
			}
		}
		Spinner spinner=((Spinner)textentryView.findViewById(R.id.prioritySpinner));
		items.add(new RemiItem(name, spinner.getSelectedItemPosition()));
		totalCount++;
		updateView("confirmCreateItem");
	}
	
//	metodi per le gesture
	
//	metodi privati di varia utilità
	private void updateView(String from){
//		if there is a text in the query
		Log.d(TAG, "from: "+from+" search: "+search);
		Collections.sort(items, comparator);
		queriedItems.clear();
		if(search!=null){
			for(RemiItem currItem: items){
//				Log.d(TAG, "Checking list "+currItem.name);
//				add in queriedLists each lists whose name contains the query text
				if(currItem.name.contains(search)){
					queriedItems.add(currItem);
//					Log.d(TAG, currList.name);
				}
			}
		} else {
			queriedItems.addAll(items);
		}
        ListAdapter adapter=new DetailArrayAdapter(this, R.layout.detail_row, queriedItems);
        setListAdapter(adapter);
        
    	result.putExtra(MasterActivity.RETURNEDCHECKEDITEMS, checkCount);
    	result.putExtra(MasterActivity.RETURNEDTOTALITEMS, totalCount);
    	setResult(RESULT_OK, result);
        
//        Log.e(TAG, from);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		Log.d(TAG, "item click");
		String clickedItemName=((TextView)view.findViewById(R.id.detailRowEntry)).getText().toString();
		for(RemiItem currentItem:queriedItems){
			if(currentItem.name.compareTo(clickedItemName)==0){
				if(currentItem.check) checkCount--;
				else checkCount++;
				currentItem.check=!currentItem.check;
				updateView("onItemClick");
				return;
			}
		}
	}

	public void confirmModifyItem(View textentryView) {
		String newName=((EditText)textentryView.findViewById(R.id.newItemName)).getText().toString();
		RemiItem oldItem=queriedItems.get(selectedPosition);
		String oldName=oldItem.name;
//		chech if there is an item in the items db with the same name
		for(RemiItem currItem:items){
			if(currItem.name.compareTo(newName)==0){
//				check if the item in the items db is itself
				if(newName.compareTo(oldName)!=0){
//					if not i delete oldName item from items db
//					queriedItems.remove(selectedPosition);
					items.remove(oldItem);
					totalCount--;
				}
//				both cases the newName item will be set as unchecked
				if(currItem.check){
					currItem.check=false;
					checkCount--;
				}
				updateView("ItemModify");
				return;
			}
		}
//		if no item with newName was found update oldItem name and set it unchecked
		oldItem.name=newName;
		oldItem.check=false;
		updateView("ItemModify");
	}

	/*@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,	long id) {
//		temporarily opens the modify item dialog
		selectedPosition=position;
		DialogFragment frag=ModifyItemDialog.newInstance(items.get(selectedPosition).name);
		frag.show(getFragmentManager(), "modify_item");
		return true;
	}*/


	@Override
	public void onClick(View v) {
//		Log.d(TAG, "view click");
		String clickedItemName=((CheckBox)v.findViewById(R.id.detailRowEntry)).getText().toString();
		for(RemiItem currentItem:queriedItems){
			if(currentItem.name.compareTo(clickedItemName)==0){
				if(currentItem.check) checkCount--;
				else checkCount++;
				currentItem.check=!currentItem.check;
				updateView("onItemClick");
				return;
			}
		}		
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	  	MenuInflater inflater=getMenuInflater();
	  	inflater.inflate(R.menu.detail_cab_menu, menu);
	  	int size=queriedItems.size();
	  	if(deletingItems==null) deletingItems=new boolean[size];
	  	for(int i=0; i<size; i++) deletingItems[i]=false;
	  	selected=0;
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch(item.getItemId()){
		case R.id.cab_delete:{
//			cancella elementi spuntati
			int size=deletingItems.length;
			for(int i=0; i<size; i++){
				if(deletingItems[i]) items.remove(queriedItems.get(i));
			}
			mode.finish();
			updateView("ContextualActionBar");
			return true;
		}
		case R.id.cab_alarm:{
//			start the clock app
			Intent intent = new Intent(Intent.ACTION_INSERT);
			intent.setData(CalendarContract.Events.CONTENT_URI);
			intent.putExtra(Events.TITLE, listName);
			startActivity(intent);
			mode.finish();
			return true;
		}
		case R.id.cab_high_priority:{
			int size=deletingItems.length;
			for(int i=0; i<size; i++){
				if(deletingItems[i]) queriedItems.get(i).priority=RemiItem.HIGH_PRIOROTY;
			}
			mode.finish();
			updateView("ContextualActionBar");
			return true;
		}
		case R.id.cab_medium_priority:{
			int size=deletingItems.length;
			for(int i=0; i<size; i++){
				if(deletingItems[i]) queriedItems.get(i).priority=RemiItem.MEDIUM_PRIOROTY;
			}
			mode.finish();
			updateView("ContextualActionBar");
			return true;
		}
		case R.id.cab_low_priority:{
			int size=deletingItems.length;
			for(int i=0; i<size; i++){
				if(deletingItems[i]) queriedItems.get(i).priority=RemiItem.LOW_PRIOROTY;
			}
			mode.finish();
			updateView("ContextualActionBar");
			return true;
		}
		default:{
			return false;
		}
		}
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mActionMode=null;
		
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		deletingItems[position]=!deletingItems[position];
		if(checked) selected++;
		else selected--;
		String subtitle=Integer.toString(selected)+
				" "+
				getResources().getString(R.string.cab_subtitle_selected);
		mode.setSubtitle(subtitle);
	}

//	TODO: find a way to modify an item name, maybe through a button in the detail row that makes the textview editable
// 	TODO: priorità definite dall'utente
}


/** Done list:
	added the possibility to create a calendar event for a list
	added a priority field on items that influences the items order
	added a little colored rectangle on the right of each item to show it's priority
	
 */
