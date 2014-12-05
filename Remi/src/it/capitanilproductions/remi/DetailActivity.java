package it.capitanilproductions.remi;

import it.capitanilproductions.remi.R.id;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

public class DetailActivity extends ListActivity implements OnActionExpandListener, OnQueryTextListener {
	
	private static final String TAG="REMI";
	
	private static String listName;
	private static boolean listABOrder;
	private static boolean listMTBottom;

	private SQLiteDatabase db=null;
	private SQLiteOpenHelper helper=null;
	private Cursor query;
	private int dialog;
	
	private ListView lw;
	
	private String selection []={
		DBList.COLOUMN_NAME,
		DBList.COLOUMN_IS_CHECKED,
		DBList.COLOUMN_ID
	};
	
	private String columnNameArray []={
			DBList.COLOUMN_NAME
	};
	
	public View gesturedView;
	
	private String search;
	private String baseSelection;
	private String ordering;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master);
//		retreiving values from intent
		Bundle extras=getIntent().getExtras();
		listName=extras.getString(MasterActivity.CHOSENLIST);
		if(listName==null) onDestroy(); //if no list name is passed to this activity it commits suicide
		listABOrder=extras.getBoolean(MasterActivity.CHOSENLISTABORDER);
		listMTBottom=extras.getBoolean(MasterActivity.CHOSENLISTMTBOTTOM);
		ordering=orderByClause(listABOrder, listMTBottom);
//		database opening
		helper=new DBList(this);
		db=helper.getWritableDatabase();
		baseSelection=DBList.COLOUMN_LIST+"='"+listName+"'";
		query=db.query(DBList.ITEM_TABLE, null, baseSelection, null, null, null, ordering);
		
		ListAdapter adapter=new DetailCursorAdapter(this, R.layout.detail_row, query, 0);
		setListAdapter(adapter);
		
		lw=((ListView)findViewById(android.R.id.list));
		
		setTitle(listName);
		getActionBar().setDisplayHomeAsUpEnabled(true); //TODO: risolvere il bug del pulsante home che non funziona
		
	}
	
//  override metodi per creazione e gestione menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
	  	MenuInflater inflater=getMenuInflater();
	  	inflater.inflate(R.menu.detail_activity_menu, menu);
	  	
	  	MenuItem searchMenuItem=menu.findItem(R.id.action_searchlist);
	  	searchMenuItem.setOnActionExpandListener(this);
	    SearchView searchView = (SearchView) searchMenuItem.getActionView();
	    searchView.setOnQueryTextListener(this);
	    searchView.setQueryHint(getResources().getString(R.string.search_hint));
		  	
	  	return true; 
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	case R.id.action_searchlist:{
    		return true;
    	}
    	case R.id.action_add_list:{
//    		Toast.makeText(this, "Nuova lista creata!!", Toast.LENGTH_SHORT).show();
    		dialog=R.string.list_creation;
    		showAddItemDialog();
    		return true;
    	}
    	case R.id.clean_list:{
    		query.moveToFirst();

    		while(!query.isAfterLast()){
    			Log.d(TAG, DBList.COLOUMN_NAME+"='"+query.getString(query.getColumnIndex(DBList.COLOUMN_NAME))+"' AND "+baseSelection);
    			if(query.getInt(query.getColumnIndex(DBList.COLOUMN_IS_CHECKED))==1){
    				db.delete(DBList.ITEM_TABLE,
    						DBList.COLOUMN_NAME+"='"+query.getString(query.getColumnIndex(DBList.COLOUMN_NAME))+"' AND "+baseSelection,
    						null);
    			}
    			query.moveToNext();
    		}
    	updateView("Clean list");
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
		if(!newText.isEmpty()) search=DBList.COLOUMN_NAME+" LIKE '"+newText+"%'";
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
		if(item.getItemId()==R.id.action_searchlist){
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
		DialogFragment modifyDialog=ModifyItemDialog.newInstance(R.string.modify_item_title, itemName);
		modifyDialog.show(getFragmentManager(), "DetailFragment");
	}
	
	public void confirmCreateItem(View textentryView) {
		String name=((EditText)textentryView.findViewById(R.id.newItemName)).getText().toString();
		name=name.replace("'", "''");
		ContentValues values=new ContentValues();
		values.put(DBList.COLOUMN_NAME, name);
		values.put(DBList.COLOUMN_IS_CHECKED, 0);
		values.put(DBList.COLOUMN_LIST, listName);
		if(db.query(DBList.ITEM_TABLE, columnNameArray, DBList.COLOUMN_NAME+"='"+name+"' AND "+baseSelection, null, null, null, null).getCount()!=0){
			db.update(DBList.ITEM_TABLE, values, DBList.COLOUMN_NAME+"='"+name+"' AND "+baseSelection, null);
		} else db.insert(DBList.ITEM_TABLE, null, values);
		updateView("confirmCreateItem");
	}
	
//	metodi per le gesture
	
//	metodi privati di varia utilità
	private void updateView(String from){
		if (search==null) search=baseSelection;
		else search=search+" AND "+baseSelection;
        query=db.query(DBList.ITEM_TABLE, null, search, null, null, null, ordering);
        
        ListAdapter adapter=new DetailCursorAdapter(this, R.layout.detail_row, query, 0);
        setListAdapter(adapter);
        
//        Log.e(TAG, from);
	}

	private String orderByClause(boolean abOrder, boolean mtBottom){
		String clause=null;
		if(mtBottom){ //mtBottom enabled
			clause=DBList.COLOUMN_IS_CHECKED;
			if(abOrder) clause=clause+","+DBList.COLOUMN_NAME;
		} else{ //mtBottom disabled
			if(abOrder) clause=DBList.COLOUMN_NAME;
		}
		return clause;
	}

	public void onItemClick(View view) {
		View selectedView= view==null? gesturedView : view;
		CheckBox item=(CheckBox)selectedView.findViewById(R.id.detailRowEntry);
		ContentValues values=new ContentValues();
//		with the introduction of gestures the checkbox no more updates itself,
//		therefore if the element was unchecked it has to be stored as checked
//		to be properly shown as "done" when the UI updates
		values.put(DBList.COLOUMN_IS_CHECKED, item.isChecked()==false ? 1 : 0);
		String clause=baseSelection+" AND "+DBList.COLOUMN_NAME+"='"+item.getText().toString()+"'";
		db.update(DBList.ITEM_TABLE, values, clause, null);
		updateView("onItemClick");
	}

	public void confirmModifyItem(View textentryView) {
		String newName=((EditText)textentryView.findViewById(R.id.newItemName)).getText().toString();
		newName=newName.replace("'", "''");
		String oldName=((TextView)gesturedView.findViewById(R.id.detailRowEntry)).getText().toString();
		oldName=oldName.replace("'", "''");
//		chech if there is an item in the items db with the same name
		if(db.query(DBList.ITEM_TABLE, columnNameArray, DBList.COLOUMN_NAME+"='"+newName+"' AND "+baseSelection, null, null, null, null).getCount()!=0){
//			check if the item in the items db is itself
			if(newName!=oldName){
//				if not i delete oldName item from items db
				db.delete(DBList.ITEM_TABLE, DBList.COLOUMN_NAME+"='"+oldName+"' AND "+baseSelection, null);
			}
//			both cases the newName item will be set as unchecked
			ContentValues values=new ContentValues();
			values.put(DBList.COLOUMN_IS_CHECKED, 0);
			db.update(DBList.COLOUMN_NAME, values, DBList.COLOUMN_NAME+"='"+newName+"' AND "+baseSelection, null);
		}
//		updates item name and set it unchecked
		ContentValues values=new ContentValues();
		values.put(DBList.COLOUMN_NAME, newName);
		values.put(DBList.COLOUMN_IS_CHECKED, 0);
//		ottengo il nome dalla lista da cancellare dal cursor
		db.update(DBList.ITEM_TABLE, values, DBList.COLOUMN_NAME+"='"+oldName+"' AND "+baseSelection, null);
		updateView("cnfirmModifyItem");
		
	}

	public void onItemLongClick(View v) {
		// TODO Auto-generated method stub
//		mostra dialog
		
	}

}

//TODO togliere il campo posizione nella tabella degli elementi (e i campi elementi totali ed elementi fatti nella tabella delle liste)
//TODO aggiungere la possibilità di tornare indietro

/* Done list:
	done items text is now strikethrough and gray
	il nome della lista scelta viene visualizzato come titolo nella action bar della detail activity
	implementate le seguenti gestures:
		tap singolo -> cambia segno all'elemento
		swipe (entrambe le direzioni) -> cambia segno all'elemento
		double tap -> modifica nome elemento
	eliminazione degli elementi della lista mediante "pulisci"



 */
