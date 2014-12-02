package it.capitanilproductions.remi;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

public class DetailActivity extends ListActivity implements OnActionExpandListener, OnQueryTextListener {
	
	private static String listName;
	private static boolean listABOrder;
	private static boolean listMTBottom;

	private SQLiteDatabase db=null;
	private SQLiteOpenHelper helper=null;
	private Cursor query;
	private int dialog;
	
	private String selection []={
		DBList.COLOUMN_NAME,
		DBList.COLOUMN_IS_CHECKED,
		DBList.COLOUMN_ID
	};
	
	private String columnNameArray []={
			DBList.COLOUMN_NAME
	};
	
	private String baseSelection=DBList.COLOUMN_LIST+"='"+listName+"'";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		retreiving values from intent
		Bundle extras=getIntent().getExtras();
		listName=extras.getString(MasterActivity.CHOSENLIST);
		if(listName==null) onDestroy(); //if no list name is passed to this activity it commits suicide
		listABOrder=extras.getBoolean(MasterActivity.CHOSENLISTABORDER);
		listMTBottom=extras.getBoolean(MasterActivity.CHOSENLISTMTBOTTOM);
//		database opening
		helper=new DBList(this);
		db=helper.getWritableDatabase();
		query=db.query(DBList.ITEM_TABLE, selection, baseSelection, null, null, null, null);
		
		ListAdapter adapter=new DetailCursorAdapter(this, R.layout.detail_row, query, 0);
		setListAdapter(adapter);
	}
	
//  override metodi per creazione e gestione menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
	  	MenuInflater inflater=getMenuInflater();
	  	inflater.inflate(R.menu.master_activity_actions, menu);
	  	
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
    		showDialog();
    		return true;
    	}
    	default:{
    		Toast.makeText(this, "Bottone sconosciuto pigiato", Toast.LENGTH_LONG).show();
    		return true;
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
		if(!newText.isEmpty()){
			updateView(DBList.COLOUMN_NAME+" LIKE '"+newText+"%'");
		} else{
			updateView(null);
		}
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
			updateView(null);
		}
		return true;
	}
	
//	metodi per i dialog
	public void showDialog(){
		DialogFragment newFrag=DetailDialog.newInstance(R.string.action_add_item);
		newFrag.show(getFragmentManager(), "DetailFragment");
	}
	
	
//	metodi privati di varia utilità
	private void updateView(String selection){
        query=db.query(DBList.ITEM_TABLE, null, selection+" AND "+baseSelection, null, null, null, null);
        ListAdapter adapter=new DetailCursorAdapter(this, R.layout.detail_row, query, 0);
        setListAdapter(adapter);
	}

	public void confirmCreateItem(View textentryView) {
		String name=((EditText)textentryView.findViewById(R.id.newItemName)).getText().toString();
		name=name.replace("'", "''");
		ContentValues values=new ContentValues();
		values.put(DBList.COLOUMN_NAME, name);
		values.put(DBList.COLOUMN_IS_CHECKED, 0);
		values.put(DBList.COLOUMN_LIST, listName);
		if(db.query(DBList.LIST_TABLE, columnNameArray, DBList.COLOUMN_NAME+"='"+name+"' AND "+baseSelection, null, null, null, null).getCount()!=0){
			db.update(DBList.ITEM_TABLE, values, DBList.COLOUMN_NAME+"='"+name+"' AND "+baseSelection, null);
		} else db.insert(DBList.ITEM_TABLE, null, values);
		updateView(null);
	}
}
